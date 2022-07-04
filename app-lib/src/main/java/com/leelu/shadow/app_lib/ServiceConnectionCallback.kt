package com.leelu.shadow.app_lib

import android.content.ComponentName
import android.os.IBinder

/**
 *
 * CreateDate: 2022/4/14 17:05
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
interface ServiceConnectionCallback : InnerBaseCallback {
    fun onServiceConnected(name: ComponentName?, service: IBinder?)
    fun onServiceDisconnected(name: ComponentName?)
}