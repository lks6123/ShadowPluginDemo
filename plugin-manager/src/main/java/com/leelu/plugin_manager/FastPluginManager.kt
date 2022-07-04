package com.leelu.plugin_manager

import android.content.Context
import android.os.RemoteException
import android.util.Pair
import com.tencent.shadow.core.common.LoggerFactory
import com.tencent.shadow.core.manager.installplugin.InstalledPlugin
import com.tencent.shadow.core.manager.installplugin.InstalledType
import com.tencent.shadow.core.manager.installplugin.PluginConfig
import com.tencent.shadow.core.manager.installplugin.PluginConfig.PluginFileInfo
import com.tencent.shadow.dynamic.host.FailedException
import com.tencent.shadow.dynamic.manager.PluginManagerThatUseDynamicLoader
import org.json.JSONException
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.*

abstract class FastPluginManager(context: Context?) : PluginManagerThatUseDynamicLoader(context) {
    private val mFixedPool = Executors.newFixedThreadPool(4)
    private val mLogger = LoggerFactory.getLogger(FastPluginManager::class.java)

    @Throws(
        IOException::class,
        JSONException::class,
        InterruptedException::class,
        ExecutionException::class
    )
    fun installPlugin(zip: String?, hash: String?, odex: Boolean): PluginConfig {
        val pluginConfig = installPluginFromZip(File(zip), hash)
        installPluginToDatabase(odex, pluginConfig)
        return pluginConfig
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun installPluginToDatabase(
        odex: Boolean,
        pluginConfig: PluginConfig
    ): InstalledPlugin {
        val uuid = pluginConfig.UUID
        val futures: MutableList<Future<*>> = LinkedList()
        val extractSoFutures: MutableList<Future<Pair<String, String>>> = LinkedList()
        if (pluginConfig.runTime != null && pluginConfig.pluginLoader != null) {
            val odexRuntime: Future<*> = mFixedPool.submit<Any> {
                oDexPluginLoaderOrRunTime(
                    uuid, InstalledType.TYPE_PLUGIN_RUNTIME,
                    pluginConfig.runTime.file
                )
            }
            futures.add(odexRuntime)
            val odexLoader: Future<*> = mFixedPool.submit<Any> {
                oDexPluginLoaderOrRunTime(
                    uuid, InstalledType.TYPE_PLUGIN_LOADER,
                    pluginConfig.pluginLoader.file
                )
            }
            futures.add(odexLoader)
        }

        val soDirMap: MutableMap<String, String> = HashMap()
        pluginConfig.plugins?.forEach { entry: Map.Entry<String, PluginFileInfo?> ->
            val partKey = entry.key
            val apkFile = entry.value?.file
            val extractSo =
                mFixedPool.submit<Pair<String, String>> { extractSo(uuid, partKey, apkFile) }
            futures.add(extractSo)
            extractSoFutures.add(extractSo)
            if (odex) {
                val odexPlugin: Future<*> = mFixedPool.submit<Any> {
                    oDexPlugin(uuid, partKey, apkFile)
                }
                futures.add(odexPlugin)
            }
        }

        for (future in futures) {
            future.get()
        }
        for (future in extractSoFutures) {
            val pair = future.get()
            soDirMap[pair.first] = pair.second
        }
        onInstallCompleted(pluginConfig, soDirMap)
        return getInstalledPlugins(1)[0]
    }

    @Throws(RemoteException::class)
    protected fun callApplicationOnCreate(partKey: String?) {
        //当Application 的 onCreate 没有被调用时，才去调用
        if (!isApplicationOnCreateCalled(partKey)) {
            mPluginLoader.callApplicationOnCreate(partKey)
        }
    }

    @Throws(RemoteException::class)
    //判断一个插件的 Application 的 onCreate 是否已被调用。true 表示已调用，false 标识未调用
    protected fun isApplicationOnCreateCalled(partKey: String?): Boolean {
        val map = mPluginLoader.loadedPlugin
        val isCall = map[partKey] as? Boolean?
        return (isCall != null && isCall)
    }

    //这个方法是针对插件内部自带有loaderRuntime时调用的
    @Throws(RemoteException::class, TimeoutException::class, FailedException::class)
    protected fun loadPlugin(uuid: String?, partKey: String?) {
        loadLoaderAndRuntime(uuid, partKey)
        loadPluginOnly(partKey)
    }

    @Throws(RemoteException::class, TimeoutException::class, FailedException::class)
    protected fun loadLoaderAndRuntime(uuid: String?, partKey: String?) {
        if (mPpsController == null) {
            bindPluginProcessService(getPluginProcessServiceName(partKey))
            waitServiceConnected(10, TimeUnit.SECONDS)
        }
        loadRunTime(uuid)
        loadPluginLoader(uuid)
    }

    @Throws(RemoteException::class)
    protected fun loadPluginOnly(partKey: String?) {
        if (!isPluginLoad(partKey)) {
            mPluginLoader.loadPlugin(partKey)
        }
    }

    @Throws(RemoteException::class)
    protected fun isPluginLoad(partKey: String?): Boolean {
        return if (mPluginLoader == null) {
            throw IllegalAccessException("mPluginLoader is null. maybe loaderRuntime init false")
        } else {
            mPluginLoader.loadedPlugin.containsKey(partKey)
        }
    }

    protected abstract fun getPluginProcessServiceName(partKey: String?): String
}