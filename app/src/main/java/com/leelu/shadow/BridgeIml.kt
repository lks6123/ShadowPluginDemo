package com.leelu.shadow

import com.leelu.shadow.app_lib.Bridge
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * CreateDate: 2022/7/1 11:03
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
class BridgeIml : Bridge {
    private val hostData = ConcurrentHashMap<String, Any>()
    override fun <T : Any> getHostData(key: String): T? {
        return try {
            hostData[key] as T
        } catch (e: Exception) {
            null
        }
    }

    override fun <T : Any> addHostData(key: String, data: T) {
        hostData[key] = data
    }
}