package com.example.chargeev
import android.Manifest
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager


import com.google.android.gms.maps.model.Polyline
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.gson.Gson
import com.google.maps.android.SphericalUtil
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var mGoogleMap: GoogleMap? = null







    private val locations = listOf(

        LatLng(22.765208695455254, 75.88113906279146),
        LatLng(22.728994743882918, 75.86340480365216),
        LatLng(22.727087510617746, 75.86740206608323),
        LatLng(22.65581289676637, 75.81995285178748),
        LatLng(22.723160020724, 75.86926451858919),
        LatLng(22.735024755300635, 75.87396794647482),
        LatLng(22.718857486301516, 75.87141338512374),
        LatLng(22.727224991583977, 75.86757711779106),
        LatLng(22.74495504957366, 75.88367558630804),
        LatLng(22.758531900609633, 75.90025945474241),
        LatLng(22.756659311719222, 75.88689082610654),
        LatLng(22.754474592233166, 75.882491024277),
        LatLng(22.74589142744026, 75.88418325574992),
        LatLng(22.73168900733087, 75.89315208255626),
        LatLng(22.732781553564706, 75.8892599501686),
        LatLng(22.732313320533294, 75.8951827603237),
        LatLng(22.731064691278387, 75.86286113919144),
        LatLng(22.71284453623902, 75.88022620871433),
        LatLng(22.71549265177447, 75.88373492084955),
        LatLng(22.720715173908165, 75.87990723488385),
        LatLng(22.72152427922375, 75.847451647633),
        LatLng(22.718582055142996, 75.84067345373539)
    )
    private var tappedLatLng: LatLng? = null


    private lateinit var showLocationButton: Button
    private lateinit var showDirection: Button
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var lastLocation: Location
    private lateinit var nearest : Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var nearbyButton: Button
    lateinit var googleMap: GoogleMap

    private lateinit var spCity: Spinner
    private lateinit var spLocation: Spinner
    private val cityList = ArrayList<String>()
    private val locationList = ArrayList<String>()
    private val maxList = ArrayList<String>()
    private lateinit var cityAdapter: ArrayAdapter<String>
    private lateinit var locationAdapter: ArrayAdapter<String>

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
        const val CHANNEL_ID="channelID"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spCity=findViewById(R.id.sp_city)
        spLocation=findViewById(R.id.sp_location)

        val cityList=listOf("Indore","Ujjain","Dewas")
        val IndoreLocations=listOf("Vijay Nagar","LIG","Airport")
        val UjjainLocations=listOf("A","B","C")
        val DewasLocations=listOf("X","Y","Z")

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
                            "Vijay Nagar" -> zoomOnMap(LatLng(22.7196, 75.8577)) // Example coordinates for Vijay Nagar
                            "LIG" -> zoomOnMap(LatLng(22.7088, 75.8752))
                            "Airport" -> zoomOnMap(LatLng(22.7217, 75.8016))
                            "A" -> zoomOnMap(LatLng(23.1745, 75.7859))
                            "B" -> zoomOnMap(LatLng(23.1907, 75.7671))
                            "C" -> zoomOnMap(LatLng(23.1815, 75.7780))
                            "X" -> zoomOnMap(LatLng(22.9676, 76.0554))
                            "Y" -> zoomOnMap(LatLng(22.9752, 76.0345))
                            "Z" -> zoomOnMap(LatLng(22.9851, 76.0157))
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



        createNotificationChannel()




        Places.initialize(applicationContext, "AIzaSyAsdQoVAJfGztpgM8S4ztHy1gH_1oqSxoA")



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        showLocationButton = findViewById(R.id.showLocationButton)
        nearbyButton = findViewById(R.id.nearbyButton)




        showLocationButton.setOnClickListener {
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(22.732781553564706, 75.8892599501686), 13f))
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

    private fun zoomOnMap(latLng: LatLng) {
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 12f)
        mGoogleMap?.animateCamera(newLatLngZoom)
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


        val theMap = HashMap<LatLng,String>()
        theMap[locations[0]]="Sech.No-94, Plot No 172 , Ground Floor, 172, Main Rd, opposite Brilliant Convention Centre, near HDFC Bank, Scheme No 113, Indore, Madhya Pradesh 452010"
        theMap[locations[1]]="Jail Rd, Indore GPO, Indore, Madhya Pradesh 452001"
        theMap[locations[2]]="Pather Godown, Near Rajkumar Bridge, Snehlataganj, Indore, Madhya Pradesh 452001"
        theMap[locations[3]]="Near Rajkumar Bridge, Snehlataganj, Indore, Madhya Pradesh 452001"
        theMap[locations[4]]="33, Nehru Park Rd, Saste Nagar, Nehru Park 2, Shivaji Nagar, Indore, Madhya Pradesh 452003"
        theMap[locations[5]]="Address6"
        theMap[locations[6]]="Address7"
        theMap[locations[7]]="Address8"
        theMap[locations[8]]="Address9"
        theMap[locations[9]]="Address10"
        theMap[locations[10]]="Address11"
        theMap[locations[11]]="Address12"
        theMap[locations[12]]="Address13"
        theMap[locations[13]]="Address14"
        theMap[locations[14]]="Address15"
        theMap[locations[15]]="Address16"
        theMap[locations[16]]="Address17"
        theMap[locations[17]]="Address18"
        theMap[locations[18]]="Address19"
        theMap[locations[19]]="Address20"
        theMap[locations[20]]="Address21"









        // Using the lastLocation
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location

                val boundsBuilder = LatLngBounds.Builder()
                locations.forEach { boundsBuilder.include(it) }
                val bounds = boundsBuilder.build()
                mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

//                for (location in locations) {
//                    val marker = mGoogleMap!!.addMarker(
//                        MarkerOptions().position(location)
//                            .title("Marker at ${location.latitude}, ${location.longitude}")
//                    )
//                    if (marker != null) {
//                        markerLatLngMap[marker] = location
//                    }
//                }

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
                    " Estmd Time ${time.roundToInt()} Min"
        }
        else{
            dist="${distance.roundToInt()}m"+"Estmd Time ${(distance/125)*18*60}Sec"
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
            channel.description = "vriovjrojrotprtr"
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun onMarkerClick(p0: com.google.android.gms.maps.model.Marker) = false



}
