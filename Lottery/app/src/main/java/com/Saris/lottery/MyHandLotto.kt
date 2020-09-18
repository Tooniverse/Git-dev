package com.Saris.lottery

import android.app.Application
import com.google.android.gms.ads.MobileAds
import io.realm.Realm
import io.realm.RealmConfiguration

class MyHandLotto : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)


        MobileAds.initialize(this,"ca-app-pub-6656436186511889~7529251139")

        // 테스트할때
        val realmConfiguration = RealmConfiguration.Builder().build()
        Realm.setDefaultConfiguration(realmConfiguration)

    }
}