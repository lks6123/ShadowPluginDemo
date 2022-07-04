package com.leelu.plugin_app

import android.app.Application
import android.util.Log

/**
 *
 * CreateDate: 2022/4/1 14:20
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("MyApplication", "pluginApp Application onCreate")
    }
}