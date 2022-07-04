package com.leelu.shadow.app_lib

interface Callback {
    fun onServiceConnected(
        name: android.content.ComponentName?,
        service: android.os.IBinder?
    )

    fun onServiceDisconnected(name: android.content.ComponentName?)
}
