package com.example.chargeev


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class info_window_adaptor(private val context : Context) : GoogleMap.InfoWindowAdapter {
    override fun getInfoWindow(marker: Marker): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.info_window,null)
        val title : TextView = view.findViewById(R.id.infoWindowTitle)
        val desc : TextView = view.findViewById(R.id.infoWindowDesc)


        val data = marker?.tag as? info_data
        title.text = data?.title
        desc.text=data?.desc

        return view
    }

    override fun getInfoContents(p0: Marker): View? {

        return null
    }
}