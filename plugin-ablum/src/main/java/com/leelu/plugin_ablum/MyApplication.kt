package com.leelu.plugin_ablum

import android.app.Application
import android.util.Log

/**
 *
 * CreateDate: 2022/4/1 14:21
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("MyApplication","pluginAblum Application onCreate")
    }
}