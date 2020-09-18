package com.example.test4_map

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

import kotlinx.android.synthetic.main.activity_status.*
import kotlinx.android.synthetic.main.content_status.*

class StatusActivity : AppCompatActivity() {
    val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        setSupportActionBar(toolbar)

        val ab = supportActionBar
        ab!!.hide()

        getListdata()
/*
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

 */
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun getListdata() {
        val realmResult1 = realm.where<logGPS>().equalTo("m_num","0").findAll().sort("id", Sort.DESCENDING)
        val adapter1 = toSaveAdapter(realmResult1)
        gps_list_1.adapter = adapter1
        realmResult1.addChangeListener { _ -> adapter1.notifyDataSetChanged() }

    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked
            var num  = "0"


            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_guest1 ->
                    if (checked) {
                        num = "1"
                    }
                R.id.radio_guest2 ->
                    if (checked) {
                        num = "2"
                    }
                R.id.radio_guest3 ->
                    if (checked) {
                        num = "3"
                    }
                R.id.radio_guest4 ->
                    if (checked) {
                        num = "4"
                    }
                R.id.radio_guest5 ->
                    if (checked) {
                        num = "5"
                    }
            }
            gps_txt2.text = "고객 " + num
            val realmResult2 = realm.where<logGPS>().equalTo("m_num",num).findAll().sort("id", Sort.DESCENDING)

            val adapter2 = toSaveAdapter(realmResult2)
            gps_list_2.adapter = adapter2
            realmResult2.addChangeListener { _ -> adapter2.notifyDataSetChanged() }
        }
    }

}
