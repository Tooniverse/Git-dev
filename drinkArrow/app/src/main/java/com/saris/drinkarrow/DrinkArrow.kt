package com.saris.drinkarrow

import android.app.Application
import com.google.android.gms.ads.MobileAds

class DrinkArrow : Application() {
    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this,"ca-app-pub-6656436186511889~7728184380")
    }

}