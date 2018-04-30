package ds.mathematik.uni_marburg.de.farrow.activities

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.toDrawable
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import ds.mathematik.uni_marburg.de.farrow.R
import ds.mathematik.uni_marburg.de.farrow.fragments.BaseFragment
import ds.mathematik.uni_marburg.de.farrow.fragments.DashboardFragment
import ds.mathematik.uni_marburg.de.farrow.fragments.EventsFragment
import ds.mathematik.uni_marburg.de.farrow.fragments.MapFragment
import ds.mathematik.uni_marburg.de.farrow.model.event.EventViewModel
import ds.mathematik.uni_marburg.de.farrow.utils.getViewModel
import org.jetbrains.anko.itemsSequence

class MainActivity : AppCompatActivity() {

    private val bottomNavigationView: BottomNavigationView by bindView(R.id.bottom_navigation)
    private val viewPager: ViewPager by bindView(R.id.view_pager)

    private lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        eventViewModel = getViewModel()
        setupNavigation()
    }

    private fun setupNavigation() {
        with(viewPager) {
            adapter = BottomNavigationAdapter(
                DashboardFragment(),
                EventsFragment(),
                MapFragment()
            )
            offscreenPageLimit = 2
        }

        with(bottomNavigationView) {
            menu.itemsSequence().forEach { item ->
                item.icon = when (item.itemId) {
                    R.id.action_dashboard -> GoogleMaterial.Icon.gmd_dashboard.toDrawable(context)
                    R.id.action_events -> GoogleMaterial.Icon.gmd_view_list.toDrawable(context)
                    R.id.action_map -> GoogleMaterial.Icon.gmd_map.toDrawable(context)
                    else -> TODO()
                }
            }

            setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_dashboard -> viewPager.currentItem = 0
                    R.id.action_events -> viewPager.currentItem = 1
                    R.id.action_map -> viewPager.currentItem = 2
                }
                true
            }
        }
    }

    private inner class BottomNavigationAdapter(
        vararg val fragments: BaseFragment
    ) : FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.count()

    }

}
