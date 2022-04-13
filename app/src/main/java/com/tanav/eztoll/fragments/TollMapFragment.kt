package com.tanav.eztoll.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.tanav.eztoll.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.tanav.eztoll.BuildConfig
import com.tanav.eztoll.utilities.Utility
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TollMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var myContext: Context

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mMap: GoogleMap

    //weather url to get JSON
    var weather_url1 = ""

    //weather api id for url
    var api_id1 = "030314b750cc43e7b39e503dfe37150c"

    private lateinit var txtTemperature: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myContext = requireActivity().applicationContext
        val myView =  inflater.inflate(R.layout.fragment_toll_map, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        txtTemperature = myView.findViewById(R.id.txt_temperature)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Log.d("sch", "TollMapFragment, weather_url1=$weather_url1")

        return myView
    }

    override fun onResume() {
        super.onResume()
        getLocation()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TollMapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        var checkPointJsonArray = Utility.readCheckPointData(requireContext())
        var checkPoints: ArrayList<LatLng> = ArrayList()

        for (i in 0 until checkPointJsonArray.length()) {
            val checkPoint = checkPointJsonArray.getJSONObject(i)
            val lat = checkPoint.getDouble("lat")
            val lng = checkPoint.getDouble("lng")
            val position = LatLng(lat, lng)
            checkPoints.add(position)
        }
        var lineOptions = PolylineOptions()
        lineOptions.addAll(checkPoints)
        lineOptions.width(10F)
        lineOptions.color(Color.BLUE)

        //draw a line on Google map
        mMap.addPolyline(lineOptions)

        //show the middle track point of the toll road
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(checkPoints[checkPointJsonArray.length()/2], 10.0f))
    }

    private fun getLocation() {
        // get the last location
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Build intent that displays the App settings screen.
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts(
                "package",
                BuildConfig.APPLICATION_ID,
                null
            )
            intent.data = uri
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // get the latitude and longitude and create the http URL
                weather_url1 = "https://api.weatherbit.io/v2.0/current?" + "lat=" + location?.latitude + "&lon=" + location?.longitude + "&key=" + api_id1
                Log.d("sch", "TollMapFragment, getLocation(), weather_url1=$weather_url1")
                // this function fetch data from URL
                getTemperature()
            }
    }

    private fun getTemperature() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(requireActivity())
        val url: String = weather_url1
        Log.d("sch", "TollMapFragment, getTemperature(), url=$url")

        // Request a string response
        // from the provided URL.
        val stringReq = StringRequest(Request.Method.GET, url,
            { response ->
                Log.d("sch", "TollMapFragment, getTemperature(), response=$response")

                // get the JSON object
                val obj = JSONObject(response)

                // get the Array from obj of name - "data"
                val arr = obj.getJSONArray("data")
                Log.d("sch", "TollMapFragment, getTemperature(), obj1=${arr.toString()}")

                // get the JSON object from the
                // array at index position 0
                val obj2 = arr.getJSONObject(0)
                Log.d("sch", "TollMapFragment, getTemperature(), obj2=${obj2.toString()}")

                // set the temperature and the city
                // name using getString() function
                (obj2.getString("temp") + " deg Celsius in " + obj2.getString("city_name")).also { txtTemperature.text = it }
            },
            // In case of any error
            { txtTemperature.text = getString(R.string.txt_temperature_error) })
        queue.add(stringReq)
    }
}