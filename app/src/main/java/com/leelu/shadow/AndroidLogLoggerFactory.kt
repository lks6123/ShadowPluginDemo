package com.leelu.shadow

import android.util.Log
import com.tencent.shadow.core.common.ILoggerFactory
import com.tencent.shadow.core.common.Logger
import org.slf4j.helpers.MessageFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * CreateDate: 2022/3/15 17:41
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
class AndroidLogLoggerFactory : ILoggerFactory {
    private val loggerMap: ConcurrentMap<String, Logger> = ConcurrentHashMap()

    companion object {
        private const val SPLIT = " :: "
        private const val LOGGER_NAME = "UpShadowFoundation"
        private const val LOG_LEVEL_TRACE = 5
        private const val LOG_LEVEL_DEBUG = 4
        private const val LOG_LEVEL_INFO = 3
        private const val LOG_LEVEL_WARN = 2
        private const val LOG_LEVEL_ERROR = 1
        val instance: AndroidLogLoggerFactory by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AndroidLogLoggerFactory()
        }
    }

    override fun getLogger(name: String): Logger {
        val simpleLogger = loggerMap[name]
        return if (simpleLogger != null) {
            simpleLogger
        } else {
            val newInstance: Logger = IVLogger(name)
            val oldInstance = loggerMap.putIfAbsent(name, newInstance)
            oldInstance ?: newInstance
        }
    }

    internal inner class IVLogger(private val name: String) : Logger {
        override fun getName() = name

        override fun isTraceEnabled(): Boolean {
            return true
        }

        override fun trace(msg: String) {
            log(LOG_LEVEL_TRACE, msg, null)
        }

        override fun trace(format: String, o: Any) {
            val tuple = MessageFormatter.format(format, o)
            log(LOG_LEVEL_TRACE, tuple.message, null)
        }

        override fun trace(format: String, o: Any, o1: Any) {
            val tuple = MessageFormatter.format(format, o, o1)
            log(LOG_LEVEL_TRACE, tuple.message, null)
        }

        override fun trace(format: String, objects: Array<Any?>?) {
            val tuple = MessageFormatter.arrayFormat(format, objects)
            log(LOG_LEVEL_TRACE, tuple.message, null)
        }

        override fun trace(msg: String, throwable: Throwable) {
            log(LOG_LEVEL_TRACE, msg, throwable)
        }

        override fun isDebugEnabled(): Boolean {
            return true
        }

        override fun debug(msg: String) {
            log(LOG_LEVEL_DEBUG, msg, null)
        }

        override fun debug(format: String, o: Any) {
            val tuple = MessageFormatter.format(format, o)
            log(LOG_LEVEL_DEBUG, tuple.message, null)
        }

        override fun debug(format: String, o: Any, o1: Any) {
            val tuple = MessageFormatter.format(format, o, o1)
            log(LOG_LEVEL_DEBUG, tuple.message, null)
        }

        override fun debug(format: String, objects: Array<Any?>?) {
            val tuple = MessageFormatter.arrayFormat(format, objects)
            log(LOG_LEVEL_DEBUG, tuple.message, null)
        }

        override fun debug(msg: String, throwable: Throwable) {
            log(LOG_LEVEL_DEBUG, msg, throwable)
        }

        override fun isInfoEnabled(): Boolean {
            return true
        }

        override fun info(msg: String) {
            log(LOG_LEVEL_INFO, msg, null)
        }

        override fun info(format: String, o: Any) {
            val tuple = MessageFormatter.format(format, o)
            log(LOG_LEVEL_INFO, tuple.message, null)
        }

        override fun info(format: String, o: Any, o1: Any) {
            val tuple = MessageFormatter.format(format, o, o1)
            log(LOG_LEVEL_INFO, tuple.message, null)
        }

        override fun info(format: String, objects: Array<Any?>?) {
            val tuple = MessageFormatter.arrayFormat(format, objects)
            log(LOG_LEVEL_INFO, tuple.message, null)
        }

        override fun info(msg: String, throwable: Throwable) {
            log(LOG_LEVEL_INFO, msg, throwable)
        }

        override fun isWarnEnabled(): Boolean {
            return true
        }

        override fun warn(msg: String) {
            log(LOG_LEVEL_WARN, msg, null)
        }

        override fun warn(format: String, o: Any) {
            val tuple = MessageFormatter.format(format, o)
            log(LOG_LEVEL_WARN, tuple.message, null)
        }

        override fun warn(format: String, o: Any, o1: Any) {
            val tuple = MessageFormatter.format(format, o, o1)
            log(LOG_LEVEL_WARN, tuple.message, null)
        }

        override fun warn(format: String, objects: Array<Any?>?) {
            val tuple = MessageFormatter.arrayFormat(format, objects)
            log(LOG_LEVEL_WARN, tuple.message, null)
        }

        override fun warn(msg: String, throwable: Throwable) {
            log(LOG_LEVEL_WARN, msg, throwable)
        }

        override fun isErrorEnabled(): Boolean {
            return true
        }

        override fun error(msg: String) {
            log(LOG_LEVEL_ERROR, msg, null)
        }

        override fun error(format: String, o: Any) {
            val tuple = MessageFormatter.format(format, o)
            log(LOG_LEVEL_ERROR, tuple.message, null)
        }

        override fun error(format: String, o: Any, o1: Any) {
            val tuple = MessageFormatter.format(format, o, o1)
            log(LOG_LEVEL_ERROR, tuple.message, null)
        }

        override fun error(format: String, objects: Array<Any?>?) {
            val tuple = MessageFormatter.arrayFormat(format, objects)
            log(LOG_LEVEL_ERROR, tuple.message, null)
        }

        override fun error(msg: String, throwable: Throwable) {
            log(LOG_LEVEL_ERROR, msg, throwable)
        }

        private fun log(level: Int, _message: String, t: Throwable?) {
            val tag = LOGGER_NAME
            val message = "$name$SPLIT$_message"
            when (level) {
                LOG_LEVEL_TRACE, LOG_LEVEL_DEBUG ->
                    if (t == null) Log.d(tag, message) else Log.d(tag, message, t)
                LOG_LEVEL_INFO -> if (t == null) Log.i(tag, message) else Log.i(
                    tag,
                    message,
                    t
                )
                LOG_LEVEL_WARN -> if (t == null) Log.w(tag, message) else Log.w(
                    tag,
                    message,
                    t
                )
                LOG_LEVEL_ERROR -> if (t == null) Log.e(
                    tag,
                    message
                ) else Log.e(tag, message, t)
                else -> {}
            }
        }
    }
}

