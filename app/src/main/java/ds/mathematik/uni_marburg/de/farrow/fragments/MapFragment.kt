package ds.mathematik.uni_marburg.de.farrow.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import ds.mathematik.uni_marburg.de.farrow.R
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_map

    private lateinit var locationEngine: LocationEngine
    private lateinit var locationPlugin: LocationLayerPlugin
    private lateinit var mapboxMap: MapboxMap
    private lateinit var permissionsManager: PermissionsManager

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            mapboxMap = it
            enableLocationPlugin(requireContext())
            fab.setOnClickListener {
                val lastLocation: Location? = locationEngine.lastLocation
                if (lastLocation != null) setCameraPosition(lastLocation)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        if (::locationPlugin.isInitialized) locationPlugin.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (::locationEngine.isInitialized) locationEngine.removeLocationUpdates()
        if (::locationPlugin.isInitialized) locationPlugin.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::locationEngine.isInitialized) locationEngine.deactivate()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) = permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)

    @SuppressLint("MissingPermission")
    private fun enableLocationPlugin(context: Context) {
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            initializeLocationEngine(context)

            if (!::locationPlugin.isInitialized) locationPlugin = LocationLayerPlugin(
                mapView,
                mapboxMap,
                locationEngine
            ).apply { setLocationLayerEnabled(true) }
        } else {
            permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) = Unit
                override fun onPermissionResult(granted: Boolean) =
                    if (granted) enableLocationPlugin(context)
                    else {
                        Toast.makeText(
                            context,
                            R.string.user_location_permission_not_granted,
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().finish()
                    }
            })
            permissionsManager.requestLocationPermissions(requireActivity())
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationEngine(context: Context) {
        locationEngine = LocationEngineProvider(context).obtainBestLocationEngineAvailable().apply {
            priority = LocationEnginePriority.HIGH_ACCURACY
            activate()
        }

        val lastLocation: Location? = locationEngine.lastLocation
        if (lastLocation != null) setCameraPosition(lastLocation)
        else locationEngine.addLocationEngineListener(object : LocationEngineListener {
            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    setCameraPosition(location)
                    locationEngine.removeLocationEngineListener(this)
                }
            }

            override fun onConnected() = locationEngine.requestLocationUpdates()
        })
    }

    private fun setCameraPosition(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val update: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0)
        mapboxMap.animateCamera(update)
    }

}