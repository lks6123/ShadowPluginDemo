package com.leelu.shadow

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.util.Log
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.ProcessUtils
import com.leelu.shadow.app_lib.BridgeHolder
import com.leelu.shadow.manager.PluginHelper
import com.leelu.shadow.manager.ShadowManagerIml
import com.tencent.shadow.core.common.LoggerFactory
import com.tencent.shadow.dynamic.host.DynamicRuntime

/**
 * CreateDate: 2022/3/15 17:41
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
class MyApplication : Application() {
    private val TAG = this.javaClass.simpleName
    override fun onCreate() {
        super.onCreate()
        app = this

        if (ProcessUtils.isMainProcess()) {
            CrashUtils.init {
                Log.e(TAG, "crash", it.throwable)
            }
            LoggerFactory.setILoggerFactory(AndroidLogLoggerFactory())
            //在全动态架构中，Activity组件没有打包在宿主而是位于被动态加载的runtime，
            //为了防止插件crash后，系统自动恢复crash前的Activity组件，此时由于没有加载runtime而发生classNotFound异常，导致二次crash
            //因此这里恢复加载上一次的runtime
            DynamicRuntime.recoveryRuntime(this)
            PluginHelper.init {
                BridgeHolder.initBridge(BridgeIml())
                BridgeHolder.getBridge().addHostData("ShadowManager", ShadowManagerIml)
            }
        }

    }


    companion object {
        lateinit var app: MyApplication
    }
}