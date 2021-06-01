package it.niccolo.citytour.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import it.niccolo.citytour.common.PermissionCode
import it.niccolo.citytour.R
import it.niccolo.citytour.activity.MainActivity
import it.niccolo.citytour.handler.DatabaseHandler
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class MapsFragment :
    Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnCameraMoveStartedListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mLastLocation: Location
    private lateinit var mFusedLocationClient : FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback
    private lateinit var locationRequest : LocationRequest
    private var locationUpdateState = false
    private lateinit var context : AppCompatActivity
    private lateinit var markers : MutableList<Marker>
    var nearestSpot : Marker? = null
    private lateinit var db : DatabaseHandler
    private var spotSelected = false
    private var exploringMap = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            this.context = context as AppCompatActivity
            db = DatabaseHandler(context)
        }
        val locationManager : LocationManager = this.context.getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(this.context)
                .setTitle(R.string.dialog_maps_connection_title)
                .setMessage(R.string.dialog_maps_connection)
                .setNegativeButton(R.string.close, null)
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                mLastLocation = p0.lastLocation
                if(!exploringMap)
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(mLastLocation.latitude, mLastLocation.longitude),
                            18.5F))

                // D = sqrt((lat1^2 - lat2^2) + (lgt1^2 - lgt2^2)
                /*
                if(abs(latCurr - latSpot) < 0.0004000
                        && abs(lgtCurr - lgtSpot) < 0.0004000 &&
                        (abs((latCurr - latSpot) + (lgtCurr - lgtSpot)) <
                                abs((latCurr - latNSpot) + (lgtCurr - lgtNSpot)))) {
                 */
                var closeSpot = false
                var latNSpot = 0.0
                var lgtNSpot = 0.0
                for(i in 0 until markers.size) {
                    val latCurr = mLastLocation.latitude
                    val lgtCurr = mLastLocation.longitude
                    val latSpot = markers[i].position.latitude
                    val lgtSpot = markers[i].position.longitude
                    Log.d("dev-map", "${i+1} di ${markers.size}: ${sqrt((abs(latCurr - latSpot).pow(2)) + (abs(lgtCurr - lgtSpot).pow(2)))}")
                    //Log.d("dev-map", "$i di ${markers.size}: ${abs((latCurr - latNSpot) + (lgtCurr - lgtNSpot))}")
                    if(abs(latCurr - latSpot) < 0.0004500 &&
                            abs(lgtCurr - lgtSpot) < 0.0004500 &&
                                sqrt((latCurr - latSpot).pow(2) + (lgtCurr - lgtSpot).pow(2)) <
                                    sqrt((latCurr - latNSpot).pow(2) + (lgtCurr - lgtNSpot).pow(2))) {
                                        nearestSpot = markers[i]
                                        latNSpot = latSpot
                                        lgtNSpot = lgtSpot
                                        closeSpot = true
                    }
                }
                if(!closeSpot)
                    nearestSpot = null
                if(context is MainActivity) {
                    if (nearestSpot != null) {
                        (context as MainActivity).showDetailsButton(true, nearestSpot!!.title)
                        if(!spotSelected)
                            nearestSpot!!.showInfoWindow()
                        Log.d("dev-map", "Next to ${nearestSpot!!.title}")
                    }
                    else
                        (context as MainActivity).showDetailsButton(false, null)
                }
                Log.d("dev-map", "Curr. position: ${mLastLocation.latitude}, ${mLastLocation.longitude}")
            }
        }
        createLocationRequest()
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        mMap.setMinZoomPreference(9f)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.setOnInfoWindowClickListener(this)
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnCameraMoveStartedListener(this)

        setUpMap()
        markers = db.getMarkers(mMap)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        spotSelected = true
        p0!!.showInfoWindow()
        return false
    }

    override fun onInfoWindowClick(p0: Marker?) {
        AlertDialog.Builder(context)
            .setTitle(p0!!.title)
            .setMessage(R.string.dialog_spot)
            .setPositiveButton(android.R.string.yes
            ) { dialog, _ ->
                (context as MainActivity).goToInfoActivity(p0.title)
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
        Log.d("dev-map", "InfoWindow click")
    }

    override fun onMyLocationButtonClick(): Boolean {
        createLocationRequest()
        spotSelected = false
        exploringMap = false
        nearestSpot?.showInfoWindow()
        return false
    }

    override fun onCameraMoveStarted(p0: Int) {
        if(p0 == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            exploringMap = true
            Log.d("dev-map", "Map moved")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PermissionCode.FINE_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
        createLocationRequest()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PermissionCode.FINE_LOCATION
            )
            return
        }

        mMap.isMyLocationEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mFusedLocationClient.lastLocation.addOnSuccessListener(context) { location ->
            if (location != null) {
                mLastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.5f))
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 2500
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(context, PermissionCode.FINE_LOCATION)
                } catch (sendEx: IntentSender.SendIntentException) { }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PermissionCode.FINE_LOCATION
            )
        }
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

}