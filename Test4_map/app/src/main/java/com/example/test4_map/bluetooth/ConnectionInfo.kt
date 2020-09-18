package com.example.test4_map.bluetooth

import android.content.Context
import com.example.test4_map.ApplicationClass
import com.example.test4_map.utils.Const

/**
 * Remember connection informations for future use
 */
object ConnectionInfo {
    //Login URL
    // Device MAC address
    var deviceAddress: String? = null
    // Name of the connected device
    var deviceName: String? = null
        set(name) {
            val context = ApplicationClass.getAppContext()
            val prefs = context.getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE) ?: return
            val editor = prefs.edit()
            editor.putString(Const.PREFERENCE_CONN_INFO_ADDRESS, deviceAddress)
            editor.putString(Const.PREFERENCE_CONN_INFO_NAME, name)
            editor.commit()
            field = name
        }

    init {
        val context = ApplicationClass.getAppContext()
        val prefs = context.getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE)
        deviceAddress = prefs?.getString(Const.PREFERENCE_CONN_INFO_ADDRESS, null)
        deviceName = prefs?.getString(Const.PREFERENCE_CONN_INFO_NAME, null)
    }

    /**
     * Reset connection info
     */
    fun resetConnectionInfo() {
        deviceAddress = null
        deviceName = null
    }

}