package ds.mathematik.uni_marburg.de.farrow.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.cluster.clustering.ClusterManagerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import ds.mathematik.uni_marburg.de.farrow.R
import ds.mathematik.uni_marburg.de.farrow.model.event.Event
import ds.mathematik.uni_marburg.de.farrow.utils.createOptionsMenu
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            mapboxMap = it

            mapboxMap.setStyleUrl(Style.DARK)

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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) = createOptionsMenu(
        inflater = inflater,
        menuRes = R.menu.menu_map,
        menu = menu,
        icons = *arrayOf(
            R.id.menu_map_style to ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_layers_black_24dp
            )!!
        )
    )

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        mapboxMap.setStyleUrl(
            when (item.itemId) {
                R.id.menu_streets -> Style.MAPBOX_STREETS
                R.id.menu_dark -> Style.DARK
                R.id.menu_light -> Style.LIGHT
                R.id.menu_outdoors -> Style.OUTDOORS
                R.id.menu_satellite -> Style.SATELLITE
                R.id.menu_satellite_streets -> Style.SATELLITE_STREETS
                else -> return super.onOptionsItemSelected(item)
            }
        )
        return true
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
        clusterManagerPlugin = ClusterManagerPlugin(context, mapboxMap)
        clusterManagerPlugin.setOnClusterClickListener { cluster ->
            val events: MutableCollection<Event>? = cluster.items

            if (events != null && events.isNotEmpty()) {
                val builder = LatLngBounds.Builder()
                events.forEach { builder.include(it.position) }
                val bounds: LatLngBounds = builder.build()
                moveToBounds(bounds)

                return@setOnClusterClickListener true
            }
            return@setOnClusterClickListener false
        }

        mapboxMap.addOnCameraIdleListener(clusterManagerPlugin)
        mapboxMap.setOnMarkerClickListener(clusterManagerPlugin)
    }

    private fun setCameraPosition(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val update: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0)
        mapboxMap.animateCamera(update)
    }

    private fun moveToBounds(bounds: LatLngBounds) {
        val update: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 16)
        mapboxMap.animateCamera(update)
    }

}