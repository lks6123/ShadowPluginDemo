package com.leelu.shadow.app_lib
/**
 *
 * CreateDate: 2022/4/7 13:59
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
interface InnerInstallPluginCallback : InnerBaseCallback {
    fun onInstallPluginSuccess(path: String)
}