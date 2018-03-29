package ds.mathematik.uni_marburg.de.farrow.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ds.mathematik.uni_marburg.de.farrow.R
import ds.mathematik.uni_marburg.de.farrow.utils.startActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> Unit
                R.id.navigation_events -> Unit
                R.id.navigation_map -> startActivity<MapsActivity>()
            }
            true
        }
    }
}
