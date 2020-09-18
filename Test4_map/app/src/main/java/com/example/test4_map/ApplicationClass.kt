package com.example.test4_map

import android.app.Application
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class ApplicationClass : Application() {
    companion object {
        lateinit var context: Context
        lateinit var LoginID: String
        lateinit var Area: String
        lateinit var Device: String

        fun getAppContext(): Context {
            return context
        }


        var objFileSensor: JSONArray = JSONArray()
        fun putPhotoData(gpsData: String, file: String) {

            var inputObject = JSONObject()
            inputObject.put("Sensor", gpsData)
            inputObject.put("FileName", file)
            objFileSensor.put(inputObject)

        }
    }
}