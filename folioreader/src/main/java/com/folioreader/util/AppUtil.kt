package com.folioreader.util

import android.content.Context
import android.util.Log
import com.folioreader.Config
import com.folioreader.util.SharedPreferenceUtil.getSharedPreferencesString
import org.json.JSONException
import org.json.JSONObject
import java.net.ServerSocket
import java.net.URLConnection

class AppUtil {

    companion object {

        private val LOG_TAG = AppUtil::class.java.simpleName

        @JvmStatic
        fun charsetNameForURLConnection(connection: URLConnection): String {
            // see https://stackoverflow.com/a/3934280/1027646
            val contentType = connection.contentType
            val values = contentType.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var charset: String? = null

            for (_value in values) {
                val value = _value.trim { it <= ' ' }

                if (value.toLowerCase().startsWith("charset=")) {
                    charset = value.substring("charset=".length)
                    break
                }
            }

            if (charset == null || charset.isEmpty()) {
                charset = "UTF-8" //Assumption
            }

            return charset
        }

        fun saveConfig(context: Context?, config: Config) {
            val obj = JSONObject()
            try {
                obj.put(Config.CONFIG_FONT, config.font)
                obj.put(Config.CONFIG_FONT_SIZE, config.fontSize)
                obj.put(Config.CONFIG_THEME_COLOR_INT, config.themeColor)
                obj.put(Config.CONFIG_ALLOWED_DIRECTION, config.allowedDirection.toString())
                obj.put(Config.CONFIG_DIRECTION, config.direction.toString())
                SharedPreferenceUtil.putSharedPreferencesString(
                    context, Config.INTENT_CONFIG,
                    obj.toString()
                )
            } catch (e: JSONException) {
                Log.e(LOG_TAG, e.message)
            }

        }

        @JvmStatic
        fun getSavedConfig(context: Context?): Config? {
            val json = getSharedPreferencesString(context, Config.INTENT_CONFIG, null)
            if (json != null) {
                try {
                    val jsonObject = JSONObject(json)
                    return Config(jsonObject)
                } catch (e: JSONException) {
                    Log.e(LOG_TAG, e.message)
                    return null
                }

            }
            return null
        }

        fun getAvailablePortNumber(portNumber: Int): Int {
            var serverSocket: ServerSocket? = null
            var portNumberAvailable: Int

            try {
                serverSocket = ServerSocket(portNumber)
                Log.d(LOG_TAG, "-> getAvailablePortNumber -> portNumber $portNumber available")
                portNumberAvailable = portNumber
            } catch (e: Exception) {
                serverSocket = ServerSocket(0)
                portNumberAvailable = serverSocket.localPort
                Log.w(
                    LOG_TAG, "-> getAvailablePortNumber -> portNumber $portNumber not available, " +
                            "$portNumberAvailable is available"
                )
            } finally {
                serverSocket?.close()
            }

            return portNumberAvailable
        }
    }
}
