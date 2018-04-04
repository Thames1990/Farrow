package ds.mathematik.uni_marburg.de.farrow.activities

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import ca.allanwang.kau.utils.toDrawable
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import ds.mathematik.uni_marburg.de.farrow.R
import ds.mathematik.uni_marburg.de.farrow.fragments.EventsFragment
import ds.mathematik.uni_marburg.de.farrow.fragments.MapFragment
import ds.mathematik.uni_marburg.de.farrow.model.event.EventViewModel
import ds.mathematik.uni_marburg.de.farrow.utils.getViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.itemsSequence

class MainActivity : AppCompatActivity() {

    private lateinit var eventsFragment: EventsFragment
    private lateinit var eventViewModel: EventViewModel
    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eventViewModel = getViewModel()

        setupNavigation()
    }

    private fun setupNavigation() = with(navigation) {
        menu.itemsSequence().forEach { item ->
            item.icon = when (item.itemId) {
                R.id.navigation_dashboard -> GoogleMaterial.Icon.gmd_dashboard.toDrawable(context)
                R.id.navigation_events -> GoogleMaterial.Icon.gmd_view_list.toDrawable(context)
                R.id.navigation_map -> GoogleMaterial.Icon.gmd_map.toDrawable(context)
                else -> TODO()
            }
        }

        setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> selectDashboard()
                R.id.navigation_events -> selectEvents()
                R.id.navigation_map -> selectMap()
            }
            true
        }
    }

    private fun selectDashboard() = Unit

    private fun selectEvents() {
        val tag = EVENTS_FRAGMENT_TAG
        val fragment = supportFragmentManager.findFragmentByTag(tag) as EventsFragment?

        if (fragment == null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            eventsFragment = EventsFragment()
            transaction.replace(R.id.container, eventsFragment, EVENTS_FRAGMENT_TAG)
            transaction.commit()
        } else {
            eventsFragment = fragment
        }
    }

    private fun selectMap() {
        val tag = MAP_FRAGMENT_TAG
        val fragment = supportFragmentManager.findFragmentByTag(tag) as MapFragment?

        if (fragment == null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            mapFragment = MapFragment()
            transaction.replace(R.id.container, mapFragment, MAP_FRAGMENT_TAG)
            transaction.commit()
        } else {
            mapFragment = fragment
        }
    }

    companion object {
        const val EVENTS_FRAGMENT_TAG = "EVENTS_FRAGMENT_TAG"
        const val MAP_FRAGMENT_TAG = "MAP_FRAGMENT_TAG"
    }

}
