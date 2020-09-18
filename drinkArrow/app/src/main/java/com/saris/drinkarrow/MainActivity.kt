package com.saris.drinkarrow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val ab = supportActionBar
        ab!!.hide()

        spinCard.setOnClickListener {
            startActivity(Intent(this,SpinActivity::class.java))
        }
    }
}