package it.niccolo.citytour

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
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
import kotlin.math.abs

//TODO [IT] Gestire requestCodes
class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            this.context = context as AppCompatActivity
            db = DatabaseHandler(context)
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

                var latNSpot = 0.0
                var lgtNSpot = 0.0
                for(i in 0 until markers.size) {
                    val latCurr = mLastLocation.latitude
                    val lgtCurr = mLastLocation.longitude
                    val latSpot = markers[i].position.latitude
                    val lgtSpot = markers[i].position.longitude
                    if(abs(latCurr - latSpot) < 0.00090
                        && abs(lgtCurr - lgtSpot) < 0.00090) {
                        if(nearestSpot == null) {
                            nearestSpot = markers[i]
                            latNSpot = latSpot
                            lgtNSpot = lgtSpot
                        } else if(abs((latCurr - latSpot) + (lgtCurr - lgtSpot)) <
                            abs((latCurr - latNSpot) + (lgtCurr - lgtNSpot)))
                            nearestSpot = markers[i]
                            latNSpot = latSpot
                            lgtNSpot = lgtSpot
                    } else
                        nearestSpot = null
                }
                if(context is MainActivity) {
                    if (nearestSpot != null) {
                        (context as MainActivity).showDetailsButton(true, nearestSpot!!.title)
                        nearestSpot!!.showInfoWindow()
                        Log.d("dev-map", "Next to ${nearestSpot!!.title}")
                    }
                    else
                        (context as MainActivity).showDetailsButton(false, null)
                }
            }
        }
        createLocationRequest()
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        mMap.setMinZoomPreference(13f)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.setOnInfoWindowClickListener(this)

        setUpMap()
        markers = db.getMarkers(mMap)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        Log.d("dev-map", "Marker click")
        return false
    }

    override fun onInfoWindowClick(p0: Marker?) {
        Log.d("dev-map", "InfoWindow click")
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
                1
            )
            return
        }

        mMap.isMyLocationEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        mFusedLocationClient.lastLocation.addOnSuccessListener(context) { location ->
            if (location != null) {
                mLastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 2500
        locationRequest.fastestInterval = 750
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
                    e.startResolutionForResult(context, 2)
                } catch (sendEx: IntentSender.SendIntentException) { }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
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
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                2)
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

}