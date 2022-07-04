package com.leelu.plugin_manager


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.leelu.constants.Constant
import com.leelu.constants.FromIdConstant
import com.leelu.shadow.app_lib.*
import com.tencent.shadow.core.common.LoggerFactory
import com.tencent.shadow.dynamic.host.EnterCallback
import com.tencent.shadow.dynamic.loader.PluginServiceConnection
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class UpPluginManager(private val mContext: Context) : FastPluginManager(
    mContext
) {
    private val mLogger = LoggerFactory.getLogger(UpPluginManager::class.java)

    private var loaderAndRuntimePath: Bundle? = null

    private val conMap: ConcurrentHashMap<String, PluginServiceConnection> =
        ConcurrentHashMap()

    //PluginManager实现的别名，用于区分不同PluginManager实现的数据存储路径
    override fun getName() = "up-manager"

    //宿主中注册的PluginProcessService实现的类名,可以根据partKey返回不同的值。这里的partKey是初始化loaderRuntime时传递的partKey
    override fun getPluginProcessServiceName(partKey: String?) =
        "com.leelu.shadow.MainPluginProcessService"


    override fun enter(context: Context, fromId: Long, bundle: Bundle, callback: EnterCallback?) {
        when (fromId) {
            //初始化安装加载 loaderRuntime
            FromIdConstant.LOAD_LOADER_AND_RUNTIME -> {
                initLoaderAndRuntime(bundle, callback)
            }
            //安装插件
            FromIdConstant.INSTALL_PLUGIN -> {
                managerInstallPlugin(bundle, callback)
            }
            //加载插件
            FromIdConstant.LOAD_PLUGIN -> {
                managerLoadPlugin(bundle, callback)
            }

            //初始化插件，即调用插件的 Application 的 onCreate 方法
            FromIdConstant.CALL_PLUGIN_APPLICATION -> {
                managerCallPluginApplication(bundle, callback)
            }

            //启动插件中的一个 Activity
            FromIdConstant.START_ACTIVITY -> {
                onStartActivity(context, bundle, callback)
            }
            //启动插件中的一个 Service
            FromIdConstant.START_SERVICE -> {
                onPluginService(context, bundle, callback)
            }
            //关闭插件中的一个 Service
            FromIdConstant.STOP_SERVICE -> {
                onStopService(context, bundle, callback)
            }
            //绑定插件中的一个 Service
            FromIdConstant.BIND_SERVICE -> {
                onBindService(context, bundle, callback)
            }
            //解绑插件中的一个 Service
            FromIdConstant.UNBIND_SERVICE -> {
                onUnbindService(bundle, callback)
            }
        }
    }

    private fun initLoaderAndRuntime(
        bundle: Bundle,
        callback: EnterCallback?
    ) {
        val pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH)
        val partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY)
        mLogger.debug(
            "from_id_load_loader_and_runtime , pluginZipPath = $pluginZipPath, partKey = $partKey"
        )
        val cal = callback as? InnerInitLoadAndRuntimeCallback
        try {
            val installedPlugin = installPlugin(pluginZipPath, null, true)
            loadLoaderAndRuntime(installedPlugin.UUID, partKey)
            loaderAndRuntimePath = bundle
            cal?.onInitSuccess()
        } catch (e: Exception) {
            cal?.onError(e)
        }
    }

    private fun managerInstallPlugin(
        bundle: Bundle,
        callback: EnterCallback?
    ) {
        val pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH)
        mLogger.debug("from_id_install_plugin pluginZipPath = $pluginZipPath")
        val cal = callback as? InnerInstallPluginCallback
        try {
            val installPlugin = installPlugin(pluginZipPath, null, true)
            val path = installPlugin.storageDir.absolutePath
            mLogger.debug("from_id_install_plugin installedPlugin = $path")
            cal?.onInstallPluginSuccess(path)
        } catch (e: Exception) {
            cal?.onError(e)
        }
    }

    private fun managerLoadPlugin(
        bundle: Bundle,
        callback: EnterCallback?
    ) {
        val partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY)
        val cal = callback as? InnerLoadPluginCallback
        mLogger.debug("from_id_load_plugin partKey = $partKey")
        try {
            loadPluginOnly(partKey)
            cal?.loadPluginSuccess()
        } catch (e: Exception) {
            cal?.onError(e)
        }
    }



    private fun managerCallPluginApplication(
        bundle: Bundle,
        callback: EnterCallback?
    ) {
        val partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY)
        mLogger.debug("from_id_call_plugin_application partKey = $partKey")
        val cal = callback as? InnerBaseCallback
        try {
            loadPluginOnly(partKey)
            callApplicationOnCreate(partKey)
            cal?.onSuccess()
        } catch (e: Exception) {
            cal?.onError(e)
        }
    }


    private fun onStartActivity(
        context: Context,
        bundle: Bundle,
        callback: EnterCallback?
    ) {
        //从bundle中取出数据。
        //plugin的partKey。打包插件时定义的，插件zip中的config文件中也可以看到
        val partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY)
        //要启动的activity全名
        val className = bundle.getString(Constant.KEY_ACTIVITY_CLASSNAME)
        mLogger.debug(
            "from_id_start_activity , partKey = $partKey, className = $className"
        )
        val cal = callback as? InnerBaseCallback
        if (className.isNullOrBlank()) {
            cal?.onError(
                NullPointerException("activityClassName isNullOrBlank , className = $className")
            )
            return
        }

        //启动时附带的extras中的数据
        val extras = bundle.getBundle(Constant.KEY_EXTRAS)
        //启动过程是阻塞的，需要在io线程进行
        try {
            //加载插件
            loadPluginOnly(partKey)
            //调用插件的application，如果是第一次启动这个插件的话
            callApplicationOnCreate(partKey)
            //构建启动的intent
            val pluginIntent = Intent().apply { setClassName(context.packageName, className) }
            extras?.let { pluginIntent.replaceExtras(it) }
            //对intent进行转换。启动占位activity。（占位activity会代理我们插件中activity的生命周期和方法）
            val intent = mPluginLoader.convertActivityIntent(pluginIntent)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            //通过mPluginLoader调用的方法，会通过bind调用到DynamicPluginLoader中的方法，DynamicPluginLoader最终基本都会调用到 ShadowPluginLoader 中去。而我们会在 loader中创建一个类，继承自ShadowPluginLoader。
            //调用 ShadowPluginLoader 中对应的 startActivityInPluginProcess 方法
            mPluginLoader.startActivityInPluginProcess(intent)
            cal?.onSuccess()
        } catch (e: Exception) {
            cal?.onError(e)
        }
    }


    private fun onPluginService(context: Context, bundle: Bundle, callback: EnterCallback?) {
        //plugin的partKey。打包插件时定义的，插件zip中的config文件中也可以看到
        val partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY)
        //要启动的 service 全名
        val className = bundle.getString(Constant.KEY_SERVICE_CLASSNAME)
        mLogger.debug(
            "from_id_start_service , partKey = $partKey, className = $className"
        )
        val cal = callback as? InnerBaseCallback
        if (className.isNullOrBlank()) {
            cal?.onError(
                NullPointerException("serviceClassName isNullOrBlank , className = $className")
            )
            return
        }
        //启动时附带的extras中的数据
        val extras = bundle.getBundle(Constant.KEY_EXTRAS)
        try {
            //加载插件
            loadPluginOnly(partKey)
            //调用插件的application，如果是第一次启动这个插件的话
            callApplicationOnCreate(partKey)
            //构建启动的intent
            val pluginIntent = Intent()
            pluginIntent.setClassName(
                context.packageName,
                className
            )
            extras?.let { pluginIntent.replaceExtras(it) }

            //启动service
            val callSuccess = mPluginLoader.startPluginService(pluginIntent)
            if (callSuccess != null) {
                cal?.onSuccess()
            } else {
                cal?.onError(
                    RuntimeException("start service失败 className==$className")
                )
            }
        } catch (e: Exception) {
            cal?.onError(e)
        }
    }

    private fun onStopService(context: Context, bundle: Bundle, callback: EnterCallback?) {
        val className = bundle.getString(Constant.KEY_SERVICE_CLASSNAME)
        mLogger.debug(
            "from_id_stop_service, className = $className"
        )
        val cal = callback as? InnerBaseCallback
        if (className.isNullOrBlank()) {
            cal?.onError(
                NullPointerException("serviceClassName isNullOrBlank , className = $className")
            )
            return
        }
        try {
            //构建intent
            val pluginIntent = Intent()
            pluginIntent.setClassName(
                context.packageName,
                className
            )
            val result = mPluginLoader.stopPluginService(pluginIntent)
            if (result) {
                cal?.onSuccess()
            } else {
                cal?.onError(
                    RuntimeException("stop service failed")
                )
            }
        } catch (e: Exception) {
            cal?.onError(e)
        }

    }

    private fun onBindService(context: Context, bundle: Bundle, callback: EnterCallback?) {
        //plugin的partKey。打包插件时定义的，插件zip中的config文件中也可以看到
        val partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY)
        //要 bind 的 service 全名
        val serviceName = bundle.getString(Constant.KEY_SERVICE_CLASSNAME)
        val flags = bundle.getInt(Constant.KEY_BIND_SERVICE_FLAG)
        mLogger.debug(
            "from_id_bind_service, partKey = $partKey, serviceName = $serviceName , flags = $flags"
        )
        val cal = callback as ServiceConnectionCallback
        if (serviceName == null) {
            cal.onError(
                NullPointerException("serviceClassName == null")
            )
            return
        }
        //启动时附带的extras中的数据
        val extras = bundle.getBundle(Constant.KEY_EXTRAS)
        val pluginServiceConnection = object : PluginServiceConnection {
            private val weak = WeakReference(cal)
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mLogger.debug("call PluginServiceConnection: onServiceConnected")
                val weakCall = weak.get()
                if (weakCall == null) {
                    mLogger.debug("PluginServiceConnection onServiceConnected， but weakCall is null")
                } else {
                    weakCall.onServiceConnected(name, service)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mLogger.debug("call PluginServiceConnection: onServiceDisconnected")
                val weakCall = weak.get()
                if (weakCall == null) {
                    mLogger.debug("PluginServiceConnection onServiceDisconnected， but weakCall is null")
                } else {
                    weakCall.onServiceDisconnected(name)
                }

            }
        }
        mLogger.debug("bindService, callback = $callback,  ServiceConnection = $pluginServiceConnection")

        try {
            //加载插件
            loadPluginOnly(partKey)
            //调用插件的application，如果是第一次启动这个插件的话
            callApplicationOnCreate(partKey)
            //构建启动的intent
            val pluginIntent = Intent()
            pluginIntent.setClassName(
                context.packageName,
                serviceName
            )
            extras?.let { pluginIntent.replaceExtras(it) }
            //bindService
            val callSuccess =
                mPluginLoader.bindPluginService(pluginIntent, pluginServiceConnection, flags)
            if (callSuccess) {
                conMap[serviceName] = pluginServiceConnection
                cal.onSuccess()
            } else {
                cal.onError(
                    RuntimeException("bind service failed, className==$serviceName")
                )
            }
        } catch (e: Exception) {
            cal.onError(e)
        }
    }


    private fun onUnbindService(bundle: Bundle, callback: EnterCallback?) {
        //plugin的partKey。打包插件时定义的，插件zip中的config文件中也可以看到
        val partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY)
        //要 unbind 的 service 全名
        val serviceName = bundle.getString(Constant.KEY_SERVICE_CLASSNAME)
        mLogger.debug(
            "from_id_unbind_service, partKey = $partKey, serviceName = $serviceName"
        )

        val pluginServiceConnection = conMap[serviceName]
        val cal = callback as? InnerBaseCallback
        mLogger.debug(
            "unbindService callback = $callback, " +
                    "serviceConnection = $pluginServiceConnection , map = $conMap"
        )
        if (pluginServiceConnection != null) {
            conMap.remove(serviceName)
            try {
                mPluginLoader.unbindService(pluginServiceConnection)
                cal?.onSuccess()
            } catch (e: Exception) {
                cal?.onError(e)
            }
        } else {
            cal?.onError(
                RuntimeException("Don`t found serviceConnection。 please check")
            )
        }
    }

}