package com.example.test4_map

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
open class logGPS (
        @PrimaryKey var id:Int  = 0,
        var m_num : String = "",
        var lat : Double = 0.0,
        var lon : Double = 0.0 ,
        var recieve_time : String = ""
) : RealmObject() { }