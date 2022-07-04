package com.leelu.shadow.manager

import com.tencent.shadow.dynamic.host.PluginManagerUpdater
import java.io.File
import java.util.concurrent.Future

/**
 *
 * CreateDate: 2022/4/12 11:27
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
interface ManagerUpdater : PluginManagerUpdater {
    override fun wasUpdating(): Boolean {
        return false
    }

    override fun isAvailable(file: File?): Future<Boolean>? {
        return null
    }

    override fun update(): Future<File>? {
        return null
    }

    override fun getLatest(): File
}