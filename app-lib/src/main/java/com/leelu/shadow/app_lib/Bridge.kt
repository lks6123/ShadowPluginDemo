package com.leelu.shadow.app_lib

/**
 *
 * CreateDate: 2022/7/1 10:58
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
interface Bridge {
    fun <T : Any> getHostData(key: String): T?
    fun <T : Any> addHostData(key: String, data: T)
}