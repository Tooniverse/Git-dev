package com.example.test4_map.ui.dashboard

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

import android.os.Bundle
import org.jetbrains.anko.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.test4_map.MainActivity
import com.example.test4_map.MarkerItem
import com.example.test4_map.R
import com.example.test4_map.logGPS
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.jetbrains.anko.custom.style

class DashboardFragment : Fragment(),OnMapReadyCallback {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var mMap: MapView
    val realm = Realm.getDefaultInstance()
    var val_gps: String? = ""
    var gps_List: MutableList<MarkerItem> = mutableListOf(
        MarkerItem("0", 0.0, 0.0, "time"), MarkerItem("1", 0.0, 0.0, "time"),
        MarkerItem("2", 0.0, 0.0, "time"), MarkerItem("3", 0.0, 0.0, "time"),
        MarkerItem("4", 0.0, 0.0, "time"), MarkerItem("5", 0.0, 0.0, "time")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mMap = view.findViewById(R.id.mapView)
        //mMap.onCreate(savedInstanceState)

        mapView.onCreate(savedInstanceState)
        val_gps = arguments?.getString("navGPS") //?: "0/0.0/0.0/time!1/0.0/0.0/time!2/0.0/0.0/time!3/0.0/0.0/time!4/0.0/0.0/time!"
        mMap = view.findViewById(R.id.mapView) as MapView
        mMap.getMapAsync(this)

    }

    override fun onResume() {
        super.onResume()
        mMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMap.onPause()
    }

    override fun onStart() {
        super.onStart()
        mMap.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMap.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMap.onDestroy()
        realm.close()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMap.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMap.onSaveInstanceState(outState)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        loadData()
            for (item: Int in 0..5) {
                if(gps_List[item].lat != 0.0) {
                    val makeMark = LatLng(gps_List[item].lat, gps_List[item].lon)

                    val markerOptions = MarkerOptions()

                    markerOptions.position(makeMark)

                    markerOptions.title(gps_List[item].m_num.toString())

                    markerOptions.snippet(gps_List[item].recieve_time)

                    if(gps_List[item].m_num == "0")
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

                    googleMap.addMarker(markerOptions)

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(makeMark))

                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(18f))
                }
            }
        }

    private fun loadData() {

        for(item: Int in 0..5) {
            val data = realm.where<logGPS>().equalTo("m_num", item.toString()).findFirst()
            if(data != null) {
                gps_List[item].m_num = data.m_num
                gps_List[item].lat = data.lat
                gps_List[item].lon = data.lon
                gps_List[item].recieve_time = data.recieve_time
            }
            else
            {
                gps_List[item].m_num = item.toString()
                gps_List[item].lat = 0.0
                gps_List[item].lon = 0.0
                gps_List[item].recieve_time = "time"
            }

        }
    }
}