package com.leelu.plugin_ablum

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

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
                    Log.d(TAG, "ResponseBody: " + response.body?.toString())
                }
            })

    }

}