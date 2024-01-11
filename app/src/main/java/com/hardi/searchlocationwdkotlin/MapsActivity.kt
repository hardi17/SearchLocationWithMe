package com.hardi.searchlocationwdkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hardi.searchlocationwdkotlin.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private val MY_PERMISSION_FINE_LOCATION = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocationUser()
    }

    /*check location permission and get current location*/
    private fun getCurrentLocationUser() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Toast.makeText(
                    applicationContext,
                    "Your current location's lat and long :" +
                            currentLocation.latitude.toString() + " " +
                            currentLocation.longitude.toString(), Toast.LENGTH_LONG
                ).show()

                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)

            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocationUser()
                }
        }
    }

    /*Using this function we set lattitude and longitude in our map
      also we are getting area name using geocoder and
      set map marker to our location*/
    override fun onMapReady(googleMap: GoogleMap) {
        val latlang = LatLng(currentLocation.latitude, currentLocation.longitude)
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address =
            geoCoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
        var addressArea = address!![0].adminArea
        if (addressArea == null) {
            addressArea = address[0].locality
            if (addressArea == null) {
                addressArea = address[0].subAdminArea
            }
        }
        val markerOptions = MarkerOptions().position(latlang).title(addressArea.toString())

        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latlang))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlang, 10f))
        googleMap?.addMarker(markerOptions)
    }

}