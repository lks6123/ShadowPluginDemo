package com.leelu.shadow.app_lib

/**
 *
 * CreateDate: 2022/7/1 10:57
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
object BridgeHolder {
    private lateinit var bridge: Bridge

    fun initBridge(bridge: Bridge) {
        BridgeHolder.bridge = bridge
    }

    fun getBridge(): Bridge {
        return bridge
    }
}