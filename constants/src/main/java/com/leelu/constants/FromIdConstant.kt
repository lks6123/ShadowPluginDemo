package com.leelu.constants

/**
 *
 * CreateDate: 2022/5/10 16:38
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
object FromIdConstant {
    //初始化 Loader 和 Runtime
    const val LOAD_LOADER_AND_RUNTIME: Long = 1000

    //安装插件
    const val INSTALL_PLUGIN: Long = 1001

    //加载插件
    const val LOAD_PLUGIN: Long = 1002

    //插件是否已加载
    const val IS_PLUGIN_LOAD: Long = 1003

    //调用插件的 Application
    const val CALL_PLUGIN_APPLICATION: Long = 1004

    //标识启动Activity
    const val START_ACTIVITY: Long = 1005

    //启动Service
    const val START_SERVICE: Long = 1006

    //停止Service
    const val STOP_SERVICE: Long = 1007

    //bind service
    const val BIND_SERVICE: Long = 1008

    //unbind Service
    const val UNBIND_SERVICE: Long = 1009

    //插件的Application的onCreate是否已调用
    const val IS_PLUGIN_APPLICATION_CALLED: Long = 1010
}