package com.example.test4_map

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.test4_map.utils.Logs
import io.realm.Realm
import io.realm.RealmConfiguration


class SplashActivity : Activity() {

    companion object {
        val TAG = "SplashActivity"
        val REQUEST_BLUETOOTH_PERMISSIONS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Realm.init(this)
        Realm.setDefaultConfiguration(getRealmConfig())
        Logs.d(TAG, "# SplashActivity - onCreate()")

        // since Marshmallow, you need these permissions for BT scan
        val P1 = Manifest.permission.ACCESS_FINE_LOCATION
        val P2 = Manifest.permission.ACCESS_COARSE_LOCATION
        if(ContextCompat.checkSelfPermission(this, P1) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, P2) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(P1, P2),
                REQUEST_BLUETOOTH_PERMISSIONS)
            return
        }

        // move to main
        moveToMain()
    }

    private fun getRealmConfig() : RealmConfiguration {
        return RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
    }
    fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSIONS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Logs.d("Permission granted all!!")
                    moveToMain()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Logs.d("Permission denied!!")
                }
                return
            }
        }
    }
}
