package com.example.test4_map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter

class ViewHolder(view : View ) {
    val data1TextView : TextView = view.findViewById(R.id.gps_data1)
    val data2TextView : TextView = view.findViewById(R.id.gps_data2)

}

class toSaveAdapter(realmResult : OrderedRealmCollection<logGPS>) : RealmBaseAdapter<logGPS>(realmResult) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vh : ViewHolder
        val view : View

        if(convertView == null) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.item_tosave,parent,false)

            vh = ViewHolder(view)
            view.tag = vh
        }else {
            view = convertView
            vh = view.tag as ViewHolder
        }

        adapterData?.let {
            val item = adapterData!![position]
            vh.data1TextView.text = "위치 : " + item.lat.toString() + " / " + item.lon.toString()
            vh.data2TextView.text = "일자 : " + item.recieve_time
        }
        return view
    }

    override fun getItemId(position: Int): Long {
        if(adapterData != null) {
            return adapterData!![position].id.toLong()
        }
        return super.getItemId(position)
    }

}
