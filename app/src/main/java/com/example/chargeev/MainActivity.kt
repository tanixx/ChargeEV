package com.example.chargeev
import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
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
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var mGoogleMap: GoogleMap? = null
    private val locations = listOf(
        LatLng(22.72931744966455, 75.86334920016742),
        LatLng(22.727773721130557, 75.86742615778944),
        LatLng(22.714731712192407, 75.88433771465213),
        LatLng(22.717086037558957, 75.84087599897448),
        LatLng(22.737158803471615, 75.87081697282667),
    LatLng(22.75513595140915, 75.88604515378142),
    LatLng(22.73088976610597, 75.8893107844682),
    LatLng(22.7205593367236, 75.84741272679648),
    LatLng(22.711486302110444, 75.85601326912477),
    LatLng(22.69548195427431, 75.85512854213988),
    LatLng(22.690094360601666, 75.86696208817003),
    LatLng(22.711007455102123, 75.90449012679649),
    LatLng(22.72039655198061, 75.8800028998116),
    LatLng(22.723207068112245, 75.88146202148684),
    LatLng(22.696231621852835, 75.84200461145309),
    LatLng(22.743668384624144, 75.88414601515495),
    LatLng(22.727523497769177, 75.867223438438),
    LatLng(22.756419935867573, 75.90040646912479),
    LatLng(22.71303350485125, 75.83813201515494),
    LatLng(22.718785906734414, 75.87159025748326),
    LatLng(22.71157173860962, 75.88057112679651),
    LatLng(22.75439736584422, 75.86603583843801),
    LatLng(22.76457976899035, 75.88175504213989),
    LatLng(22.68978571991704, 75.82854837282666),
    LatLng(22.774216556794965, 75.93819999981156),
    LatLng(22.72872389360065, 75.81970038446818),
    LatLng(22.75300466652904, 75.88371783049836),
    LatLng(22.680861661295364, 75.86466691515494),
    LatLng(22.822233834614817, 75.85060808817003),
    LatLng(22.773387258718525, 75.89806049981156),
    LatLng(22.69715523123308, 75.85490979610971),
    LatLng(22.743467281721745, 75.88398642679648),
    LatLng(22.75623113622311, 75.88586149981158),
    LatLng(22.73491968583458, 75.8739990691248),
    LatLng(22.821418303153184, 75.92934711515494),
    LatLng(22.770962579049286, 75.91027849981157),
    LatLng(22.75047031804727, 75.90484347282667),
    LatLng(22.72244592288266, 75.87864999981157),
    LatLng(22.72080315428281, 75.8793151876341),
    LatLng(22.718051967515425, 75.88189010823747),
    LatLng(22.775836614708965, 75.89040629981157),
    LatLng(22.76088120096437, 75.89073504213982),
    //ujjain
    LatLng(23.1685565635321, 75.80026434498272),
    LatLng(23.15653483338341, 75.74303271977631),
    LatLng(23.120544467834325, 75.79950931167687),
        LatLng(23.178476892263614, 75.79260127674304),
        LatLng(23.19126132267846, 75.78753903736273),
        LatLng(23.18119647057517, 75.79007271337125),
        LatLng(22.72670354973183, 75.87425373547487)
    )


    private lateinit var showLocationButton: Button
    private lateinit var lastLocation: Location
    private lateinit var nearest : Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var nearbyButton: Button
    private lateinit var spCity: Spinner
    private lateinit var spLocation: Spinner

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

        Places.initialize(applicationContext, "AIzaSyAsdQoVAJfGztpgM8S4ztHy1gH_1oqSxoA")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        showLocationButton = findViewById(R.id.showLocationButton)
        nearbyButton = findViewById(R.id.nearbyButton)
        val twoWheelerButton: Button = findViewById(R.id.twoWheelerButton)
        val fourWheelerButton: Button = findViewById(R.id.fourWheelerButton)


        twoWheelerButton.setOnClickListener {
            val locations = listOf(
                LatLng(22.72931744966455, 75.86334920016742),
                LatLng(22.727773721130557, 75.86742615778944),
                LatLng(22.714731712192407, 75.88433771465213),
                LatLng(22.717086037558957, 75.84087599897448),
                LatLng(22.737158803471615, 75.87081697282667)
            )
            val theMap = HashMap<LatLng,String>()
            theMap[locations[0]]="Jail Rd, Indore GPO, Indore, Madhya Pradesh 452001"
            theMap[locations[1]]="PVG9+V2P, Near Rajkumar Bridge, Snehlataganj, Indore, Madhya Pradesh 452001"
            theMap[locations[2]]="EV Urjaa 2nd Floor, ICCC Building, Indore Smart Seed Incubation Centre AICTSL Campus, Chartered Bus Stand, near Geeta Bhawan, Indore, Madhya Pradesh 452001"
            theMap[locations[3]]="6/6, EV Urjaa Charging station in front of Gurudwara Old Rajmohalla, North Rajmohalla, Ward 23, opposite Gurudwara Guru Nanak Darbar, Rajmohalla, Indore, Madhya Pradesh 452002"
            theMap[locations[4]]="PVPC+P82, Malwa Mill Rd, Patni Pura, Indore, Madhya Pradesh 452001"
            for (location in locations) {
                val markerOption = MarkerOptions()
                location.let { markerOption.position(it) }
                markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
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
            val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
            var nearestLocation: LatLng? = null
            var minDistance = Double.MAX_VALUE

            locations.forEach { location ->
                val distance = FloatArray(1)
                Location.distanceBetween(currentLatLng.latitude, currentLatLng.longitude,
                    location.latitude, location.longitude, distance)
                if (distance[0] < minDistance) {
                    minDistance = distance[0].toDouble()
                    nearestLocation = location
                }
            }
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom( LatLng(nearestLocation!!.latitude, nearestLocation!!.longitude), 15f))

        }

        fourWheelerButton.setOnClickListener {
            val locations = listOf(LatLng(22.75513595140915, 75.88604515378142),
                LatLng(22.73088976610597, 75.8893107844682),
                LatLng(22.7205593367236, 75.84741272679648),
                LatLng(22.711486302110444, 75.85601326912477),
                LatLng(22.69548195427431, 75.85512854213988))
            val theMap = HashMap<LatLng,String>()
            theMap[locations[0]]="FH-228, Sceme No 54, Vijay Nagar, Indore, Madhya Pradesh 452010"
            theMap[locations[1]]="2, AB Rd, Shree Nagar Main Colony, Anoop Nagar, Indore, Madhya Pradesh 452001"
            theMap[locations[2]]="Laxmi Building, Police Station, 22, North, Yashwant Ganj, Malharganj, Indore, Madhya Pradesh 452002"
            theMap[locations[3]]="Priyanka Enterprises, 5/2, Moti Tabela, Indore, Madhya Pradesh 452007"
            theMap[locations[4]]="Sakaar Apat, 24-26, Manik Bagh Rd, Saifee Nagar, Indore, Madhya Pradesh 452014"


            for (location in locations) {
                val markerOption = MarkerOptions()
                location.let { markerOption.position(it) }
                markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
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
            val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
            var nearestLocation: LatLng? = null
            var minDistance = Double.MAX_VALUE

            locations.forEach { location ->
                val distance = FloatArray(1)
                Location.distanceBetween(currentLatLng.latitude, currentLatLng.longitude,
                    location.latitude, location.longitude, distance)
                if (distance[0] < minDistance) {
                    minDistance = distance[0].toDouble()
                    nearestLocation = location
                }
            }
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom( LatLng(nearestLocation!!.latitude, nearestLocation!!.longitude), 15f))

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


        val theMap = HashMap<LatLng,String>()
        theMap[locations[0]]="Jail Rd, Indore GPO, Indore, Madhya Pradesh 452001"
        theMap[locations[1]]="PVG9+V2P, Near Rajkumar Bridge, Snehlataganj, Indore, Madhya Pradesh 452001"
        theMap[locations[2]]="EV Urjaa 2nd Floor, ICCC Building, Indore Smart Seed Incubation Centre AICTSL Campus, Chartered Bus Stand, near Geeta Bhawan, Indore, Madhya Pradesh 452001"
        theMap[locations[3]]="6/6, EV Urjaa Charging station in front of Gurudwara Old Rajmohalla, North Rajmohalla, Ward 23, opposite Gurudwara Guru Nanak Darbar, Rajmohalla, Indore, Madhya Pradesh 452002"
        theMap[locations[4]]="PVPC+P82, Malwa Mill Rd, Patni Pura, Indore, Madhya Pradesh 452001"
        theMap[locations[5]]="FH-228, Sceme No 54, Vijay Nagar, Indore, Madhya Pradesh 452010"
        theMap[locations[6]]="2, AB Rd, Shree Nagar Main Colony, Anoop Nagar, Indore, Madhya Pradesh 452001"
        theMap[locations[7]]="Laxmi Building, Police Station, 22, North, Yashwant Ganj, Malharganj, Indore, Madhya Pradesh 452002"
        theMap[locations[8]]="Priyanka Enterprises, 5/2, Moti Tabela, Indore, Madhya Pradesh 452007"
        theMap[locations[9]]="Sakaar Apat, 24-26, Manik Bagh Rd, Saifee Nagar, Indore, Madhya Pradesh 452014"
        theMap[locations[10]]="236, Bhawarkua Main Rd, near Anand Super 100, Indrapuri Colony, Bhanwar Kuwa, Indore, Madhya Pradesh 452014"
        theMap[locations[11]]="Hotel Essentia, near world cup square, Vandana Nagar, Indore, Madhya Pradesh 452015"
        theMap[locations[12]]="No 11, Nexus Treasure Island, Mahatma Gandhi Rd, Near Treasure Island, South Tukoganj, Indore, Madhya Pradesh 452001"
        theMap[locations[13]]="Revolt service center, Mahatma Gandhi Rd, Near Indraprastha Tower, Race Course Road, Indore, Madhya Pradesh 452001"
        theMap[locations[14]]="Dosa Magic, 147 B, Annapurna Rd, near Annapurna Police Station, Moon Palace Colony, Indore, Madhya Pradesh 452009"
        theMap[locations[15]]="Aastha Talkies, Near 452010, Patni Pura Rd, Nanda Nagar, Indore, Madhya Pradesh 452011"
        theMap[locations[16]]="PVG8+RX6 Pather Godown, Near Rajkumar Bridge, Snehlataganj, Indore, Madhya Pradesh 452001"
        theMap[locations[17]]="Shop No. 17, Shekhar Planet, Nom Nom Chinese, Vijay Nagar, Indore, Madhya Pradesh 452001"
        theMap[locations[18]]="PR7Q+36W, Dhar Rd, Labriya Bheru, Raj Mohalla, Indore, Madhya Pradesh 452002"
        theMap[locations[19]]="Nexus Treasure Island, opposite Ravindra Natya Grah, Flim Colony, South Tukoganj, Indore, Madhya Pradesh 452001"
        theMap[locations[20]]="PV6J+JCP, M G Road, Opposite M Y H, M G Marg, MY Hospital Rd, Opposite Indrasan Building, Jaora Compound, Ushaganj, Murai Mohalla, Indore, Madhya Pradesh 452001 1, PV6J+JCP, Jaora Compound Main Rd, Murai Mohalla, Indore, Madhya Pradesh 452001"
        theMap[locations[21]]="Electronic Complex, 2-A, opp. IDEA CELLUAR LTD, Pardesipura, Indore, Madhya Pradesh 452010"
        theMap[locations[22]]="Sech.No-94, Plot No 172 , Ground Floor, 172, Main Rd, opposite Brilliant Convention Centre, near HDFC Bank, Scheme No 113, Indore, Madhya Pradesh 452010"
        theMap[locations[23]]="2563-E, Ring Rd, Sector E, Sudama Nagar, Indore, Madhya Pradesh 452009"
        theMap[locations[24]]="Sheraton Grand Palace, Bypass Road, Omaxe City 1, Mayakhedi, Indore, Madhya Pradesh 452016"
        theMap[locations[25]]="158, Sangam Nagar, Kanyakubj Nagar, Indore, Madhya Pradesh 452006"
        theMap[locations[26]]="H-2, Marriott Hotel, Maguda Nagar, Scheme No 54, Indore, Madhya Pradesh 452010"
        theMap[locations[27]]="Wah Wah Chap IT Park, 308, Pipliya Rao Ring Rd, Ambicapuri Colony, Ambikapuri, Indore, Madhya Pradesh 452014"
        theMap[locations[28]]="RVC2+P6C, INDORE UJJAIN STATE HIGHWAY VILLAGE BAROLI, DIST, near TOLL PLAZA, Indore, 453555"
        theMap[locations[29]]="93/94 New Loha Mandi, Niranjanpur Cir, New Loha Mandi, Dewas Naka, Lasudia Mori, Indore, Madhya Pradesh 453771"
        theMap[locations[30]]="MVW4+P2F, Manik Bagh Rd, Nai Duniya, Saifee Nagar, Indore, Madhya Pradesh 452014"
        theMap[locations[31]]="Near 452010, Patni Pura Rd, Nanda Nagar, LIG Main Rd, LIG Colony, Indore, Madhya Pradesh 452010"
        theMap[locations[32]]="FH 228, Scheme no. 54, Vijay Nagar, Indore, Madhya Pradesh 452010"
        theMap[locations[33]]="50, Malwa Mill Rd, Malwa Mill Square, Malwa Mill, Indore, Madhya Pradesh 452001"
        theMap[locations[34]]="RWCH+9PM, Baans Balli walo ke pass, AB Rd, Manglaya Sadak, Indore, Madhya Pradesh 453771"
        theMap[locations[35]]="Ground Floor, B Zone, Nipania, Indore, Madhya Pradesh 452012"
        theMap[locations[36]]="PWX3+XWR, Pushp Vihar Colony, Scheme No 131, Indore, Madhya Pradesh 452010"
        theMap[locations[37]]="Infront of, 580, Mahatma Gandhi Rd, near Indraprastha Tower, Race Course Road, Indore, Madhya Pradesh 452001"
        theMap[locations[38]]="PVCH+5P3, South Tukoganj, Indore, Madhya Pradesh 452001"
        theMap[locations[39]]="Treebo Trend, omni palace hotel, Ratlam Kothi Main Rd, Ratlam Kothi, Indore, Madhya Pradesh 452001"
        theMap[locations[40]]="1, Khalsa Chowk Rd, Bulandshahr, Vijay Nagar, Indore, Madhya Pradesh 452010"
        theMap[locations[41]]="Type-C, Plot No . 08, Scheme No 78 - III, Sector D, Slice 4, Araniya, Scheme 78, Vijay Nagar, Indore, Madhya Pradesh 452010"
        theMap[locations[42]]="GB-2 & 4 , Divine Valley , Dewas Road , Ujjain , M.P 456010, Ujjain, 456001"
        theMap[locations[43]]="Ujjain, Madhya Pradesh 456006"
        theMap[locations[44]]="Ground Floor, Indore Rd, near Dhaba, Ujjain, Gothada, Madhya Pradesh 456010"
        theMap[locations[45]]="Malay Travels, 74, Ujjain Dewas Rd, Sant Nagar, Ujjain, Madhya Pradesh 456010"
        theMap[locations[46]]="58 arvind nagar agar road, Ujjain, Madhya Pradesh 456001"
        theMap[locations[47]]="62, Amar Singh Marg, opposite Bank of Baroda, Freeganj, Madhav Nagar, Ujjain, Madhya Pradesh 456010"
        theMap[locations[48]]="AICTSL charging Station S.G.S.I.T.S. Near Golden Gate"
   



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
