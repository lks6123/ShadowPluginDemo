package com.leelu.plugin_app

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log

class MyService : Service() {

    override fun onBind(intent: Intent): IBinder {
        Log.d("MyService_Plugin", "onBind Service")
//        ToastUtils.showShort("MyService_Plugin MyService onBind")
        return object : IMyAidlInterface.Stub() {
            @Throws(RemoteException::class)
            override fun basicTypes(
                anInt: Int,
                aLong: Long,
                aBoolean: Boolean,
                aFloat: Float,
                aDouble: Double,
                aString: String
            ): String {

            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("MyService_Plugin", "unBind Service")
//        ToastUtils.showShort("MyService_Plugin MyService onUnbind")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService_Plugin", "onStartCommand Service")
//        ToastUtils.showShort("MyService_Plugin MyService onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("MyService_Plugin", "onCreate Service")
//        ToastUtils.showShort("MyService_Plugin MyService onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyService_Plugin", "onDestroy Service")
//        ToastUtils.showShort("MyService_Plugin MyService onDestroy")
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d("MyService_Plugin", "onRebind Service")
//        ToastUtils.showShort("MyService_Plugin MyService onRebind")
    }
}