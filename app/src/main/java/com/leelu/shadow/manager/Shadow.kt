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

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import com.leelu.constants.Constant
import com.leelu.constants.FromIdConstant
import com.leelu.shadow.MyApplication
import com.leelu.shadow.app_lib.InnerBaseCallback
import com.leelu.shadow.app_lib.InnerInitLoadAndRuntimeCallback
import com.leelu.shadow.app_lib.InnerInstallPluginCallback
import com.leelu.shadow.app_lib.InnerLoadPluginCallback
import com.tencent.shadow.core.common.LoggerFactory
import com.tencent.shadow.dynamic.host.DynamicPluginManager
import com.tencent.shadow.dynamic.host.PluginManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@SuppressLint("StaticFieldLeak")
class Shadow private constructor() {
    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    private val mLogger = LoggerFactory.getLogger(Shadow::class.java)
    private lateinit var pluginManager: PluginManager
    private var mContext: Context = MyApplication.app

    companion object {
        /**
         * 获取 ShadowManager 的单实例
         *
         * @return ShadowManager 的实例对象
         */
        val instance: Shadow by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Shadow()
        }
    }

    suspend fun initShadow(
        managerFile: File,
        loadAndRuntimeFile: File
    ) {
        pluginManager = getPluginManager(managerFile)
        delay(500)
        initLoaderAndRuntime(loadAndRuntimeFile)
    }

    private fun getPluginManager(apk: File): PluginManager {
        return DynamicPluginManager(object : ManagerUpdater {
            override fun getLatest(): File {
                return apk
            }

        })
    }


    private suspend fun initLoaderAndRuntime(
        loadAndRuntimeFile: File,
        pluginName: String? = Constant.DEFAULT_LOADER_RUNTIME_NAME,
    ): Boolean {
        return suspendCoroutine { continuation ->
            pluginManager.enter(
                mContext,
                FromIdConstant.LOAD_LOADER_AND_RUNTIME,
                Bundle().apply {
                    putString(Constant.KEY_PLUGIN_ZIP_PATH, loadAndRuntimeFile.absolutePath)
                    putString(Constant.KEY_PLUGIN_PART_KEY, pluginName)
                },
                object : InnerInitLoadAndRuntimeCallback {
                    override fun onInitSuccess() {
                        mLogger.debug("InitLoadAndRuntime success")
                        continuation.resume(true)
                    }

                    override fun onError(e: Exception) {
                        mLogger.error("InitLoadAndRuntime failed", e)
                        continuation.resume(false)
                    }
                }
            )
        }
    }

    suspend fun installPlugin(assetPath: String): String {
        val file = PluginHelper.unZipResource(assetPath)
        if (file == null) {
            LogUtils.d("unZipResource failed")
            return ""
        }
        return installPlugin(file)
    }


    suspend fun installPlugin(
        file: File,
    ): String {
        return suspendCoroutine { continuation ->
            pluginManager.enter(
                mContext,
                FromIdConstant.INSTALL_PLUGIN,
                Bundle().apply {
                    putString(Constant.KEY_PLUGIN_ZIP_PATH, file.absolutePath)
                },
                object : InnerInstallPluginCallback {
                    override fun onInstallPluginSuccess(path: String) {
                        mLogger.debug("installPlugin success, path = $path")
                        continuation.resume(path)
                    }

                    override fun onError(e: Exception) {
                        mLogger.error("installPlugin failed,file = ${file.absoluteFile}", e)
                        continuation.resumeWithException(e)
                    }
                }
            )
        }
    }

    suspend fun loadPlugin(
        pluginName: String,
    ): Boolean {
        return suspendCoroutine { continuation ->
            pluginManager.enter(
                mContext,
                FromIdConstant.LOAD_PLUGIN,
                Bundle().apply {
                    putString(Constant.KEY_PLUGIN_PART_KEY, pluginName)
                },
                object : InnerLoadPluginCallback {
                    override fun loadPluginSuccess() {
                        mLogger.debug("loadPlugin success, pluginName = $pluginName")
                        continuation.resume(true)
                    }

                    override fun onError(e: Exception) {
                        mLogger.error("loadPlugin failed , pluginName = $pluginName", e)
                        continuation.resume(false)
                    }
                }
            )
        }
    }

    suspend fun callPluginApplication(
        pluginName: String,
    ) {
        return suspendCoroutine { continuation ->
            pluginManager.enter(
                mContext,
                FromIdConstant.CALL_PLUGIN_APPLICATION,
                Bundle().apply {
                    putString(Constant.KEY_PLUGIN_PART_KEY, pluginName)
                },
                object : InnerBaseCallback {
                    override fun onSuccess() {
                        mLogger.debug("callPluginApplication success, pluginName = $pluginName")
                        continuation.resume(Unit)
                    }

                    override fun onError(e: Exception) {
                        mLogger.error("callPluginApplication failed , pluginName = $pluginName", e)
                        continuation.resume(Unit)
                    }
                }
            )
        }
    }

    fun startActivity(
        pluginName: String,
        activityName: String,
        extras: Bundle?,
        onSuccess: (() -> Unit)? = null,
        onError: ((e: Exception) -> Unit)? = null,
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            pluginManager.enter(
                mContext,
                FromIdConstant.START_ACTIVITY,
                Bundle().apply {
                    putString(Constant.KEY_PLUGIN_PART_KEY, pluginName)
                    putString(Constant.KEY_ACTIVITY_CLASSNAME, activityName)
                    extras?.let { putBundle(Constant.KEY_EXTRAS, it) }
                },
                object : InnerBaseCallback {
                    override fun onSuccess() {
                        mLogger.debug("startActivity success, pluginName = $pluginName, activityName=$activityName")
                        onSuccess?.invoke()
                    }

                    override fun onError(e: Exception) {
                        mLogger.error(
                            "startActivity failed , pluginName = $pluginName , activityName = $activityName",
                            e
                        )
                        onError?.invoke(e)
                    }
                }
            )
        }
    }
}