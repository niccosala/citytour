package it.niccolo.citytour

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*


class MinimapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var context : AppCompatActivity
    private lateinit var db : DatabaseHandler
    private lateinit var appContext : Context
    private lateinit var marker : Marker
    private lateinit var address : String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
        appContext = context
        db = DatabaseHandler(context)
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
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        mMap.setMinZoomPreference(15.75f)
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnMarkerClickListener(this)
        val spot : Spot = (context as InfoActivity).spot
        marker = mMap.addMarker(
            MarkerOptions()
                .title(spot.name)
                .snippet(spot.snippet)
                .position(LatLng(spot.lat, spot.lgt))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.position))
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(marker.position.latitude, marker.position.longitude, 1)
        address = addresses[0].getAddressLine(0)
        (context as InfoActivity).setPosition(
            marker.position.latitude.toString(),
            marker.position.longitude.toString(),
            address
        )
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        p0!!.showInfoWindow()
        return false
    }

}