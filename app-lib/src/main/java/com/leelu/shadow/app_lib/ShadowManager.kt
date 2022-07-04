package com.leelu.shadow.app_lib

import android.os.Bundle
import com.leelu.shadow.app_lib.Callback

/**
 *
 * CreateDate: 2022/6/27 15:21
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
interface ShadowManager {
    fun installPlugin(assetsName: String): String?
    fun loadPlugin(partKey: String): Boolean
    fun callPluginApplication(partKey: String)
    fun startActivity(
        pluginName: String,
        activityName: String,
        bundle: Bundle? = null,
        onSuccess: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    )

    fun startService(
        pluginName: String,
        serviceName: String,
        bundle: Bundle? = null,
        onSuccess: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    )

    fun stopService(
        pluginName: String,
        serviceName: String
    )

    fun bindService(
        pluginName: String,
        serviceName: String,
        flags: Int,
        callback: Callback,
        bundle: Bundle? = null
    )

    fun unbindService(
        pluginName: String,
        serviceName: String,
        callback: Callback
    )

}