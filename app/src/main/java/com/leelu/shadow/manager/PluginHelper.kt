/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.leelu.shadow.manager

import android.util.Log
import com.leelu.shadow.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.InputStream

object PluginHelper {
    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    private val TAG: String = this.javaClass.simpleName
    private const val sPluginManagerName = "plugin-manager-release.apk"
    private const val loaderName = "loader-debug.zip"

    fun init(onSuccess: (() -> Unit)? = null) {
        coroutineScope.launch(Dispatchers.IO) {
            val pluginManagerFile = unZipResource(sPluginManagerName)!!
            val pluginZipFile = unZipResource(loaderName)!!
            Shadow.instance.initShadow(pluginManagerFile, pluginZipFile)
            onSuccess?.invoke()
        }
    }


    fun unZipResource(assetsName: String): File? {
        return try {
            val context = MyApplication.app.applicationContext
            val installPath = File(context.filesDir, assetsName)
            val zip: InputStream = context.assets.open(assetsName)
            FileUtils.copyInputStreamToFile(zip, installPath)
            Log.d(TAG, "assetsName Resource unZip Path = $installPath")
            installPath
        } catch (e: Exception) {
            Log.d(TAG, "unZipResource failed", e)
            null
        }
    }
}