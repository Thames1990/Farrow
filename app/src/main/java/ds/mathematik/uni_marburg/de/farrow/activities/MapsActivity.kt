package ds.mathematik.uni_marburg.de.farrow.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import ds.mathematik.uni_marburg.de.farrow.R

class MapsActivity : AppCompatActivity() {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
        }
    }
}
