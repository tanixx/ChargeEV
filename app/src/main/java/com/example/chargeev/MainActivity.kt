package com.example.chargeev
import LocationItem
import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location

import com.example.chargeev.BuildConfig
import android.os.Build
import android.os.Bundle
import android.view.View
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.chargeev.ApiResponse
import com.example.chargeev.RetrofitClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var mGoogleMap: GoogleMap? = null
    private lateinit var showLocationButton: Button
    private lateinit var lastLocation: Location
    private lateinit var nearest : Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var nearbyButton: Button
    private lateinit var spCity: Spinner
    private lateinit var spLocation: Spinner
    private val locations: MutableList<LatLng> = mutableListOf(
        LatLng(22.751118634027794, 75.89545214757646)
    )
    companion object {
        private const val LOCATION_REQUEST_CODE = 1
        const val CHANNEL_ID="channelID"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        spCity=findViewById(R.id.sp_city)
        spLocation=findViewById(R.id.sp_location)

        fetchData()

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getAllData()
                val newLocations = response.stations.mapNotNull { map ->
                    val lat = map["latitude"].toDoubleOrNull()
                    val lon = map["longitude"].toDoubleOrNull()
                    if (lat != null && lon != null) LatLng(lat, lon) else null
                }

                locations.addAll(newLocations)

            }

            catch (e:Exception){
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Failed to fetch locations", Toast.LENGTH_SHORT).show()
            }
        }


        val cityList=listOf("Indore","Ujjain","Dewas")
        val IndoreLocations=listOf("Vijay Nagar","LIG","Airport","Rajendra Nagar","Sukhliya","Indraprasth","Banganga","Manik Bagh","Chhota Bangarda","Khajrana")
        val UjjainLocations=listOf("NanaKheda","Nagziri","Freeganj","Rishi Nagar","Sindhi Colony","Indira Nagar")
        val DewasLocations=listOf("Gomti Nagar","Saket Nagar","Old Town","Bhonsley Colony","Ram Nagar")
        val cityAdapter= ArrayAdapter(this,android.R.layout.simple_spinner_item,cityList)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCity.adapter=cityAdapter

        spCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = cityList[position]
                val childOptions = when (selectedItem) {
                    "Indore" -> IndoreLocations
                    "Ujjain" -> UjjainLocations
                    "Dewas" -> DewasLocations
                    else -> emptyList()
                }

                val locationAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_item,
                    childOptions
                )
                locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spLocation.adapter = locationAdapter

                // Set listener for spLocation
                spLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedLocation = childOptions[position]
                        Toast.makeText(this@MainActivity, "Selected location: $selectedLocation", Toast.LENGTH_SHORT).show()

                        // Perform action for each selected location
                        // Example: Zoom to the selected location on the map
                        when (selectedLocation) {
                            //Indore Locations
                            "Vijay Nagar" -> zoomOnMap(LatLng(22.751118634027794, 75.895452147576467),"Vijay Nagar") // Example coordinates for Vijay Nagar
                            "LIG" -> zoomOnMap(LatLng(22.73362065044108, 75.88999353811086),"LIG")
                            "Airport" -> zoomOnMap(LatLng(22.73003082950402, 75.8053365967357),"Airport")
                            "Rajendra Nagar" -> zoomOnMap(LatLng(22.671353098417796, 75.82970457072918),"Rajendra Nagar")
                            "Sukhliya" -> zoomOnMap(LatLng(22.765380259352614, 75.87094312702231),"Sukhliya")
                            "Indraprasth" -> zoomOnMap(LatLng(22.722633822610852, 75.88228846249103),"Indraprasth")
                            "Banganga" -> zoomOnMap(LatLng(22.73439723917333, 75.84940318036827),"Banganga")
                            "Manik Bagh" -> zoomOnMap(LatLng(22.68377623134783, 75.8553332529431),"Manik Bagh")
                            "Chhota Bangarda" -> zoomOnMap(LatLng(22.7430, 75.8687),"Chhota Bangarda")
                            "Khajrana" -> zoomOnMap(LatLng(22.736795721660172, 75.90919034438909),"Khajrana")

//                            Ujjain Locations
                            "NanaKheda" -> zoomOnMap(LatLng(23.15661996280091, 75.78818241017863),"NanaKheda")
                            "Nagziri" -> zoomOnMap(LatLng(23.143941356647627, 75.81260079405234),"Nagziri")
                            "Freeganj" -> zoomOnMap(LatLng(23.178820580154916, 75.79147246053424),"Freeganj")
                            "Rishi Nagar" -> zoomOnMap(LatLng(23.16471909702668, 75.79370864060614),"Rishi Nagar")
                            "Sindhi Colony" -> zoomOnMap(LatLng(23.177404195328688, 75.782989681767),"Sindhi Colony")
                            "Indira Nagar" -> zoomOnMap(LatLng(23.20440401889087, 75.786102269621420),"Indira Nagar")
                             //Dewas Locations
                            "Gomti Nagar" -> zoomOnMap(LatLng(22.964080447873815, 76.0545449856301),"Gomti Nagar")
                            "Saket Nagar" -> zoomOnMap(LatLng(22.974216457466696, 76.05979440493289),"Saket Nagar")
                            "Old Town" -> zoomOnMap(LatLng(22.962075920574282, 76.06113582864599),"Old Town")
                            "Bhonsley Colony" -> zoomOnMap(LatLng(22.979210506636615, 76.05534606624637),"Bhonsley Colony")
                            "Ram Nagar" -> zoomOnMap(LatLng(22.96228119037829, 76.04094390933624),"Ram Nagar")
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // No action needed
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed
            }
        }

        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        showLocationButton = findViewById(R.id.showLocationButton)
        nearbyButton = findViewById(R.id.nearbyButton)
        val twoWheelerButton: Button = findViewById(R.id.twoWheelerButton)
        val fourWheelerButton: Button = findViewById(R.id.fourWheelerButton)


        twoWheelerButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.getAllData()

                    val twoWheelerLocations = response.twoWheeler.mapNotNull { map ->
                        val lat = map["latitude"].toDoubleOrNull()
                        val lon = map["longitude"].toDoubleOrNull()
                        val address = map["address"] as? String
                        if (lat != null && lon != null && address != null) {
                            LocationItem(lat, lon, address)
                        } else null
                    }

                    if (twoWheelerLocations.isEmpty()) {
                        Toast.makeText(this@MainActivity, "No Two Wheeler locations found", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // Map LatLng to address
                    val theMap = HashMap<LatLng, String>()
                    twoWheelerLocations.forEach {
                        val latLng = LatLng(it.latitude, it.longitude)
                        theMap[latLng] = it.address
                    }

                    mGoogleMap?.clear()

                    // Add orange markers with info window
                    for (location in theMap.keys) {
                        val markerOptions = MarkerOptions()
                            .position(location)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

                        val marker = mGoogleMap?.addMarker(markerOptions)
                        marker?.tag = theMap[location]?.let {
                            info_data(
                                it,
                                distanceBetween(
                                    lastLocation.latitude,
                                    lastLocation.longitude,
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        }
                    }

                    mGoogleMap?.setInfoWindowAdapter(info_window_adaptor(this@MainActivity))

                    // Animate camera to nearest location
                    val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                    var nearestLocation: LatLng? = null
                    var minDistance = Double.MAX_VALUE

                    theMap.keys.forEach { location ->
                        val distance = FloatArray(1)
                        Location.distanceBetween(
                            currentLatLng.latitude,
                            currentLatLng.longitude,
                            location.latitude,
                            location.longitude,
                            distance
                        )
                        if (distance[0] < minDistance) {
                            minDistance = distance[0].toDouble()
                            nearestLocation = location
                        }
                    }

                    nearestLocation?.let {
                        mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Failed to fetch two wheeler locations", Toast.LENGTH_SHORT).show()
                }
            }
        }


        fourWheelerButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.getAllData()

                    val fourWheelerLocations = response.fourWheeler.mapNotNull { map ->
                        val lat = map["latitude"].toDoubleOrNull()
                        val lon = map["longitude"].toDoubleOrNull()
                        val address = map["address"] as? String

                        if (lat != null && lon != null && address != null) {
                            LocationItem(lat, lon, address)
                        } else {
                            null // skip invalid entries
                        }
                    }

                    // Prepare map LatLng -> address
                    val theMap = HashMap<LatLng, String>()
                    fourWheelerLocations.forEach {
                        val latLng = LatLng(it.latitude, it.longitude)
                        theMap[latLng] = it.address
                    }

                    // Clear old markers
                    mGoogleMap?.clear()

                    // Add markers with yellow icon and info windows
                    for (location in theMap.keys) {
                        val markerOptions = MarkerOptions()
                            .position(location)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))

                        val marker = mGoogleMap?.addMarker(markerOptions)
                        marker?.tag = theMap[location]?.let { address ->
                            info_data(
                                address,
                                distanceBetween(
                                    lastLocation.latitude,
                                    lastLocation.longitude,
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        }
                    }

                    // Set your custom info window adapter (adjust context)
                    mGoogleMap?.setInfoWindowAdapter(info_window_adaptor(this@MainActivity))

                    // Find nearest location to current location and move camera
                    val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                    var nearestLocation: LatLng? = null
                    var minDistance = Double.MAX_VALUE

                    for (location in theMap.keys) {
                        val distance = FloatArray(1)
                        Location.distanceBetween(
                            currentLatLng.latitude,
                            currentLatLng.longitude,
                            location.latitude,
                            location.longitude,
                            distance
                        )
                        if (distance[0] < minDistance) {
                            minDistance = distance[0].toDouble()
                            nearestLocation = location
                        }
                    }

                    nearestLocation?.let {
                        mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to fetch locations",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }




        showLocationButton.setOnClickListener {
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom( LatLng(lastLocation.latitude, lastLocation.longitude), 13f))
        }


        nearbyButton.setOnClickListener {
            findNearestLocation()
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val mapOptionButton: ImageButton = findViewById(R.id.mapOptionMenu)
        val popupMenu = PopupMenu(this, mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.map_option, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMap(menuItem.itemId)
            true
        }
        mapOptionButton.setOnClickListener {
            popupMenu.show()
        }
    }





    private fun fetchData() {
        lifecycleScope.launch {
            try {
                val data = RetrofitClient.instance.getAllData()
                Log.d("API", "Stations: ${data.stations}")
                Log.d("API", "Four Wheeler: ${data.fourWheeler}")
                Log.d("API", "Two Wheeler: ${data.twoWheeler}")
            } catch (e: Exception) {
                Log.e("API", "Failed to fetch data", e)
            }
        }
    }


    private fun zoomOnMap(latLng: LatLng, place : String) {
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
        mGoogleMap?.animateCamera(newLatLngZoom)
        val markerOption = MarkerOptions()
        latLng.let { markerOption.position(it)}
        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        val marker1 = mGoogleMap?.addMarker(markerOption)
        if (marker1 != null) {
            marker1.tag = info_data(
                place,""
            )
        }

    }

    private fun changeMap(itemId: Int) {
        when (itemId) {
            R.id.normal_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap!!.uiSettings.isZoomControlsEnabled = true
        mGoogleMap!!.setOnMarkerClickListener(this)
        setUpMap()

        var notification =""


        val theMap = HashMap<LatLng, String>()
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getAllData()  // suspend function call

                val theMap = HashMap<LatLng, String>()
                response.stations.forEach { map ->
                    val lat = map["latitude"].toDoubleOrNull()
                    val lon = map["longitude"].toDoubleOrNull()
                    val address = map["address"] as? String
                    if (lat != null && lon != null && address != null) {
                        val latLng = LatLng(lat, lon)
                        theMap[latLng] = address
                    }
                }
                Log.d("API_RESPONSE", "theMap contents:")
                theMap.forEach { (latLng, address) ->
                    Log.d("API_RESPONSE", "Location: $latLng -> Address: $address")
                }

                // Now you can use theMap here, e.g., add markers on the map

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        }


        // Using the lastLocation
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location

                val boundsBuilder = LatLngBounds.Builder()
                locations.forEach { boundsBuilder.include(it) }
                val bounds = boundsBuilder.build()
                mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

                // Using lastLocation to find the nearest location
                val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                val nearestLocation = getNearestLocation(currentLatLng)



                if (nearestLocation != null) {
                    placeMarkerOnMap(nearestLocation)
                    mGoogleMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            nearestLocation,
                            15f
                        )
                    )
                    Toast.makeText(this, "Nearest Location Highlighted", Toast.LENGTH_SHORT).show()
                }

                // Add markers
                for (location in locations) {
                    val markerOption = MarkerOptions()
                    location.let { markerOption.position(it) }
                    markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    val marker1 = mGoogleMap?.addMarker(markerOption)
                    if (marker1 != null) {
                        marker1.tag = theMap[location]?.let {
                            info_data(
                                it,
                                distanceBetween(
                                    lastLocation.latitude,
                                    lastLocation.longitude,
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        }
                        mGoogleMap?.setInfoWindowAdapter(info_window_adaptor(this))
                    }
                    else {
                        Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()


                    }
                }

            }
            //notification

            notification=theMap[getNearestLocation(LatLng(lastLocation.latitude, lastLocation.longitude))].toString()+" "+getNearestLocation(LatLng(lastLocation.latitude, lastLocation.longitude))?.let {
                getNearestLocation(LatLng(lastLocation.latitude, lastLocation.longitude))?.let { it1 ->
                    distanceBetween(lastLocation.latitude,lastLocation.longitude,
                        it.latitude, it1.longitude)
                }
            }
            val builder=NotificationCompat.Builder(this, CHANNEL_ID)
            builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Nearest Station")
                .setContentText(notification)
                .setPriority(NotificationCompat.PRIORITY_MAX)
            with(NotificationManagerCompat.from(this)){
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@addOnSuccessListener
                }
                notify(1,builder.build())
            }
        }
    }


    fun distanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
        val distance= SphericalUtil.computeDistanceBetween(LatLng(lat1,lon1),LatLng(lat2,lon2))
        var dist=""
        if(distance.roundToInt()>1000){
            val speed=20
            val kilometers=(((distance/1000)*100).roundToInt())/100.0
            val time= kilometers*60/speed/1.0
            dist="${kilometers}km" +
                    " Estmd : Time ${time.roundToInt()} Min"
        }
        else{
            dist="${distance.roundToInt()}m "+"Estmd : Time ${((distance/125)*18).roundToInt()} Sec"
        }

        return dist

    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        mGoogleMap?.addMarker(markerOptions)
    }

    private fun getNearestLocation(currentLocation: LatLng): LatLng? {
        var nearestLocation: LatLng? = null
        var minDistance = Double.MAX_VALUE

        locations.forEach { location ->
            val distance = FloatArray(1)
            Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                location.latitude, location.longitude, distance)
            if (distance[0] < minDistance) {
                minDistance = distance[0].toDouble()
                nearestLocation = location
            }
        }
        return nearestLocation
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }

        mGoogleMap?.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                nearest=location
                val currentLatLng = LatLng(location.latitude, location.longitude)

                // Find and highlight the nearest location
                val nearestLocation = getNearestLocation(currentLatLng)
                if (nearestLocation != null) {
                    placeMarkerOnMap(nearestLocation)
                    mGoogleMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            nearestLocation,
                            15f
                        )
                    )
                    Toast.makeText(this, "Nearest Location Highlighted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun findNearestLocation() {
        if (::lastLocation.isInitialized) {
            val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
            val nearestLocation = getNearestLocation(currentLatLng)

            if (nearestLocation != null) {
                mGoogleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        nearestLocation,
                        15f
                    )
                )
                Toast.makeText(this, "Nearest Location Highlighted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No locations found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "First Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = ""
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun onMarkerClick(p0: com.google.android.gms.maps.model.Marker) = false
}

fun Any?.toDoubleOrNull(): Double? {
    return when (this) {
        is Double -> this
        is Float -> this.toDouble()
        is Number -> this.toDouble()
        is String -> this.toDoubleOrNull()
        else -> null
    }
}

