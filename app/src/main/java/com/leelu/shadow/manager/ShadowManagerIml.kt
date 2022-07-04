package com.leelu.shadow.manager

import android.os.Bundle
import com.leelu.shadow.app_lib.Callback
import com.leelu.shadow.app_lib.ShadowManager
import kotlinx.coroutines.runBlocking

/**
 *
 * CreateDate: 2022/7/4 10:33
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
object ShadowManagerIml : ShadowManager {

    override fun installPlugin(assetsName: String): String {
        return runBlocking {
            Shadow.instance.installPlugin(assetsName)
        }
    }

    override fun loadPlugin(partKey: String): Boolean {
        return runBlocking {
            Shadow.instance.loadPlugin(partKey)
        }
    }

    override fun callPluginApplication(partKey: String) {
        runBlocking {
            Shadow.instance.callPluginApplication(partKey)
        }
    }

    override fun startActivity(
        pluginName: String,
        activityName: String,
        bundle: Bundle?,
        onSuccess: (() -> Unit)?,
        onError: ((Exception) -> Unit)?
    ) {
        Shadow.instance.startActivity(pluginName,activityName,bundle,onSuccess,onError)
    }

    override fun startService(
        pluginName: String,
        serviceName: String,
        bundle: Bundle?,
        onSuccess: (() -> Unit)?,
        onError: ((Exception) -> Unit)?
    ) {
        TODO("Not yet implemented")
    }

    override fun stopService(pluginName: String, serviceName: String) {
        TODO("Not yet implemented")
    }

    override fun bindService(
        pluginName: String,
        serviceName: String,
        flags: Int,
        callback: Callback,
        bundle: Bundle?
    ) {
        TODO("Not yet implemented")
    }

    override fun unbindService(pluginName: String, serviceName: String, callback: Callback) {
        TODO("Not yet implemented")
    }
}