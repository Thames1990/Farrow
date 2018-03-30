package ds.mathematik.uni_marburg.de.farrow.activities

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.SupportMapFragment
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngineListener
import com.mapbox.services.android.telemetry.location.LocationEnginePriority
import com.mapbox.services.android.telemetry.location.LocationEngineProvider
import com.mapbox.services.android.telemetry.permissions.PermissionsListener
import com.mapbox.services.android.telemetry.permissions.PermissionsManager
import ds.mathematik.uni_marburg.de.farrow.R
import ds.mathematik.uni_marburg.de.farrow.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_mapbox.*

class MainActivity : AppCompatActivity() {

    private lateinit var locationEngine: LocationEngine
    private lateinit var locationPlugin: LocationLayerPlugin
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> Unit
                R.id.navigation_events -> Unit
                R.id.navigation_map -> initializeMapbox()
            }
            true
        }
    }

    private fun initializeMapbox() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as SupportMapFragment?

        if (fragment == null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            mapFragment = SupportMapFragment.newInstance()
            transaction.replace(R.id.container, mapFragment, FRAGMENT_TAG)
            transaction.commit()
        } else mapFragment = fragment

        mapFragment.getMapAsync {
            mapboxMap = it
            enableLocationPlugin()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        if (::locationPlugin.isInitialized) locationPlugin.onStart()
    }

    public override fun onStop() {
        super.onStop()
        if (::locationEngine.isInitialized) locationEngine.removeLocationUpdates()
        if (::locationPlugin.isInitialized) locationPlugin.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::locationEngine.isInitialized) locationEngine.deactivate()
        mapView.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) = permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)

    @SuppressLint("MissingPermission")
    private fun enableLocationPlugin() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine()

//            fab.setOnClickListener {
//                setCameraPosition(locationEngine.lastLocation)
//            }

            locationPlugin = LocationLayerPlugin(
                mapFragment.view as MapView,
                mapboxMap,
                locationEngine
            ).apply { setLocationLayerEnabled(LocationLayerMode.TRACKING) }
        } else {
            permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) = Unit
                override fun onPermissionResult(granted: Boolean) =
                    if (granted) enableLocationPlugin()
                    else {
                        baseContext.toast(R.string.user_location_permission_not_granted)
                        finish()
                    }
            })
            permissionsManager.requestLocationPermissions(this)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationEngine() {
        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable().apply {
            priority = LocationEnginePriority.HIGH_ACCURACY
            activate()
        }

        val lastLocation: Location? = locationEngine.lastLocation
        if (lastLocation != null) setCameraPosition(lastLocation)
        else locationEngine.addLocationEngineListener(object : LocationEngineListener {
            override fun onLocationChanged(location: Location?) = setCameraPosition(location)
            override fun onConnected() = locationEngine.requestLocationUpdates()
        })
    }

    private fun setCameraPosition(location: Location?) {
        if (location != null) {
            val latLng = LatLng(location.latitude, location.longitude)
            val update: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0)
            mapboxMap.animateCamera(update)
        }
    }

    companion object {
        const val FRAGMENT_TAG = "FRAGMENT_TAG"
    }

}
