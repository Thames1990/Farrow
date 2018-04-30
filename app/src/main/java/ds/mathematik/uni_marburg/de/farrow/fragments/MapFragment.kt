package ds.mathematik.uni_marburg.de.farrow.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import ca.allanwang.kau.utils.bindViewResettable
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
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.cluster.clustering.ClusterManagerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import ds.mathematik.uni_marburg.de.farrow.R
import ds.mathematik.uni_marburg.de.farrow.model.event.Event
import ds.mathematik.uni_marburg.de.farrow.utils.createOptionsMenu
import ds.mathematik.uni_marburg.de.farrow.utils.observe
import ds.mathematik.uni_marburg.de.farrow.utils.showWithOptions

class MapFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_map

    private val fab: FloatingActionButton by bindViewResettable(R.id.fab)
    private val mapView: MapView by bindViewResettable(R.id.map_view)

    private lateinit var clusterManagerPlugin: ClusterManagerPlugin<Event>
    private lateinit var locationEngine: LocationEngine
    private lateinit var locationPlugin: LocationLayerPlugin
    private lateinit var mapboxMap: MapboxMap
    private lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            mapboxMap = it

            enableLocationPlugin()
            enableClusterPlugin()

            setupFab()

            observe(
                liveData = eventViewModel.events,
                onChanged = { events ->
                    clusterManagerPlugin.clearItems()
                    clusterManagerPlugin.addItems(events)
                    zoomToAllMarkers(events)
                }
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupFab() = fab.showWithOptions(
        icon = GoogleMaterial.Icon.gmd_my_location,
        tooltipTextRes = R.string.fab_tooltip_my_location,
        onClickListener = {
            if (PermissionsManager.areLocationPermissionsGranted(context)) {
                locationEngine.lastLocation?.let { lastLocation ->
                    setCameraPosition(lastLocation)
                }
            }
        }
    )

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) = createOptionsMenu(
        inflater = inflater,
        menuRes = R.menu.menu_map,
        menu = menu,
        iicons = *arrayOf(
            R.id.menu_zoom_to_all_markers to GoogleMaterial.Icon.gmd_zoom_out_map,
            R.id.menu_map_style to GoogleMaterial.Icon.gmd_layers
        )
    )

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        when (item.itemId) {
            R.id.menu_zoom_to_all_markers -> {
                val events: MutableCollection<Event> = clusterManagerPlugin.algorithm.items
                zoomToAllMarkers(events.toList())
            }
            R.id.menu_streets -> mapboxMap.setStyleUrl(Style.MAPBOX_STREETS)
            R.id.menu_dark -> mapboxMap.setStyleUrl(Style.DARK)
            R.id.menu_light -> mapboxMap.setStyleUrl(Style.LIGHT)
            R.id.menu_outdoors -> mapboxMap.setStyleUrl(Style.OUTDOORS)
            R.id.menu_satellite -> mapboxMap.setStyleUrl(Style.SATELLITE)
            R.id.menu_satellite_streets -> mapboxMap.setStyleUrl(Style.SATELLITE_STREETS)
            else -> return super.onOptionsItemSelected(item)
        }
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

            locationPlugin = LocationLayerPlugin(mapView, mapboxMap, locationEngine).apply {
                setLocationLayerEnabled(true)
            }
        } else {
            permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) = Unit
                override fun onPermissionResult(granted: Boolean) {
                    if (granted) enableLocationPlugin()
                }
            }).apply {
                requestLocationPermissions(requireActivity())
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationEngine() {
        locationEngine = LocationEngineProvider(context).obtainBestLocationEngineAvailable().apply {
            priority = LocationEnginePriority.HIGH_ACCURACY
            activate()
        }
    }

    private fun enableClusterPlugin() {
        clusterManagerPlugin = ClusterManagerPlugin(context, mapboxMap)
        clusterManagerPlugin.setOnClusterClickListener { cluster ->
            val events: MutableCollection<Event>? = cluster.items
            zoomToAllMarkers(events?.toList())
        }

        mapboxMap.addOnCameraIdleListener(clusterManagerPlugin)
        mapboxMap.setOnMarkerClickListener(clusterManagerPlugin)
    }

    private fun zoomToAllMarkers(events: List<Event>?): Boolean {
        if (events != null && events.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            events.forEach { event -> builder.include(event.position) }
            val bounds: LatLngBounds = builder.build()
            moveToBounds(bounds)
            return true
        }
        return false
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