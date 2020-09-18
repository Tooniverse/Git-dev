package com.Saris.lottery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val soundSettingKey ="soundSetting"
    val preference by lazy {
        getSharedPreferences("MainActivity", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ab = supportActionBar
        ab!!.hide()

        switch_sound.setOnClickListener {
                preference.edit().putBoolean(soundSettingKey,switch_sound.isChecked).apply()
        }

        switch_sound.isChecked = preference.getBoolean(soundSettingKey,false)

        ChooseCard.setOnClickListener{
            startActivity(Intent(this,ChooseActivity::class.java))
        }

        PriceCard.setOnClickListener {
            startActivity(Intent(this,WebActivity::class.java))
        }

        DataCard.setOnClickListener {
            startActivity(Intent(this,SaveActivity::class.java))
        }
    }

    override fun onBackPressed() {
        val dlg = MyDialog(this)
        dlg.start("종료하시겠습니까?")
    }
}