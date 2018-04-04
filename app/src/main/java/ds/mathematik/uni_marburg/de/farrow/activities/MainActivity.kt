package ds.mathematik.uni_marburg.de.farrow.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import ca.allanwang.kau.utils.toDrawable
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import ds.mathematik.uni_marburg.de.farrow.R
import ds.mathematik.uni_marburg.de.farrow.fragments.DashboardFragment
import ds.mathematik.uni_marburg.de.farrow.fragments.EventsFragment
import ds.mathematik.uni_marburg.de.farrow.fragments.MapFragment
import ds.mathematik.uni_marburg.de.farrow.model.event.EventViewModel
import ds.mathematik.uni_marburg.de.farrow.utils.getViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.itemsSequence

class MainActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        eventViewModel = getViewModel()
        setupNavigation()
    }

    private fun setupNavigation() = with(bottom_navigation) {
        menu.itemsSequence().forEach { item ->
            item.icon = when (item.itemId) {
                R.id.action_dashboard -> GoogleMaterial.Icon.gmd_dashboard.toDrawable(context)
                R.id.action_events -> GoogleMaterial.Icon.gmd_view_list.toDrawable(context)
                R.id.action_map -> GoogleMaterial.Icon.gmd_map.toDrawable(context)
                else -> TODO()
            }
        }

        viewpager.adapter = BottomNavigationAdapter()

        setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_dashboard -> viewpager.currentItem = 0
                R.id.action_events -> viewpager.currentItem = 1
                R.id.action_map -> viewpager.currentItem = 2
            }
            true
        }
    }

    private inner class BottomNavigationAdapter : FragmentPagerAdapter(supportFragmentManager) {

        private val fragments: List<Fragment> =
            listOf(DashboardFragment(), EventsFragment(), MapFragment())

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.count()

    }

}
