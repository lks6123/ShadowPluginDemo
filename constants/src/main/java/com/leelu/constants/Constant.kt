package com.leelu.constants

/**
 *
 * CreateDate: 2022/3/15 15:17
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
object Constant {
    // 插件包的绝对路径的 Key
    const val KEY_PLUGIN_ZIP_PATH = "key_plugin_zip_path"

    //启动 Activity 时，Activity 全名的 Key
    const val KEY_ACTIVITY_CLASSNAME = "key_activity_classname"

    //启动Service时，service全称的 key
    const val KEY_SERVICE_CLASSNAME = "key_service_classname"

    //bundle中传递 partKey 时的 key
    const val KEY_PLUGIN_PART_KEY = "key_plugin_part_key"

    //启动 Activity 或者 Service 时， Intent中额外携带的参数
    const val KEY_EXTRAS = "key_extras"

    //bindService 时传递 Flag 的 key
    const val KEY_BIND_SERVICE_FLAG = "key_bind_service_flag"

    //LoaderRuntime 包的 默认 name
    const val DEFAULT_LOADER_RUNTIME_NAME = "default_load_runtime_name"


    const val PART_KEY = "pluginApp"
    const val PART_KEY2 = "pluginAblum"
    const val PART_KEY_BASE = "pluginBase"

}