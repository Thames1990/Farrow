package ds.mathematik.uni_marburg.de.farrow.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.View
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.cluster.clustering.ClusterManagerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import ds.mathematik.uni_marburg.de.farrow.R
import ds.mathematik.uni_marburg.de.farrow.model.event.Event
import ds.mathematik.uni_marburg.de.farrow.utils.observe
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_map

    private lateinit var clusterManagerPlugin: ClusterManagerPlugin<Event>
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

            enableLocationPlugin()
            enableClusterPlugin()

            observe(
                liveData = eventViewModel.events,
                onChanged = { events -> clusterManagerPlugin.addItems(events) }
            )

            fab.setOnClickListener {
                val lastLocation: Location? = locationEngine.lastLocation
                if (lastLocation != null) setCameraPosition(lastLocation)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        locationEngine.removeLocationUpdates()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) = permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)

    @SuppressLint("MissingPermission")
    private fun enableLocationPlugin() {
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            initializeLocationEngine()

            locationPlugin = LocationLayerPlugin(mapView, mapboxMap, locationEngine)
            locationPlugin.setLocationLayerEnabled(true)
        } else {
            permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) = Unit
                override fun onPermissionResult(granted: Boolean) {
                    if (granted) enableLocationPlugin()
                }
            })
            permissionsManager.requestLocationPermissions(requireActivity())
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationEngine() {
        locationEngine = LocationEngineProvider(context).obtainBestLocationEngineAvailable()
        locationEngine.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine.activate()
        locationEngine.lastLocation?.let { setCameraPosition(it) }
    }

    private fun enableClusterPlugin() {
        clusterManagerPlugin = ClusterManagerPlugin(requireContext(), mapboxMap)
        clusterManagerPlugin.setOnClusterClickListener {
            val markers: MutableCollection<Marker>? = clusterManagerPlugin.markerCollection.markers

            if (markers != null) {
                val builder = LatLngBounds.Builder()
                markers.forEach { builder.include(it.position) }
                val bounds: LatLngBounds = builder.build()
                val update: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 16)
                mapboxMap.animateCamera(update)
                return@setOnClusterClickListener true
            }
            return@setOnClusterClickListener false
        }

        mapboxMap.addOnCameraIdleListener(clusterManagerPlugin)
    }

    private fun setCameraPosition(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val update: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0)
        mapboxMap.animateCamera(update)
    }

}