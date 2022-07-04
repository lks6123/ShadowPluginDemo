package com.leelu.plugin_app

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.leelu.shadow.app_lib.BridgeHolder
import com.leelu.shadow.app_lib.ShadowManager
import okhttp3.*
import java.io.IOException

class MainActivity : Activity() {
    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val okHttpClient = OkHttpClient.Builder()
            .build()
        okHttpClient
            .newCall(
                Request.Builder()
                    .url("https://www.baidu.com")
                    .get()
                    .build()
            )
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d(TAG, "ResponseBody: " + response.body?.string())
                    response.body?.close()
                }
            })

        findViewById<Button>(R.id.btn_jump_test_activity).setOnClickListener {
            BridgeHolder.getBridge().getHostData<ShadowManager>("ShadowManager")?.startActivity(
                "pluginApp", "com.leelu.plugin_app.TestActivity"
            )
//            startActivity(Intent(this, TestActivity::class.java))
        }

        findViewById<Button>(R.id.btn_jump_test_activity2).setOnClickListener {

        }
        findViewById<Button>(R.id.btn_jump_user_activity).setOnClickListener {
            BridgeHolder.getBridge().getHostData<ShadowManager>("ShadowManager")?.startActivity(
                "pluginAblum", "com.leelu.plugin_ablum.MainActivity"
            )
        }
    }
}