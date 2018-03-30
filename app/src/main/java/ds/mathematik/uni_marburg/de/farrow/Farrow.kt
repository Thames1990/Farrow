package ds.mathematik.uni_marburg.de.farrow

import android.app.Application
import ca.allanwang.kau.utils.string
import com.mapbox.mapboxsdk.Mapbox

class Farrow : Application() {

    override fun onCreate() {
        super.onCreate()
        Mapbox.getInstance(applicationContext, string(R.string.mapbox_key))
    }

}