package ds.mathematik.uni_marburg.de.farrow

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import ds.mathematik.uni_marburg.de.farrow.utils.string

class Farrow: Application() {

    override fun onCreate() {
        super.onCreate()
        Mapbox.getInstance(applicationContext, string(R.string.mapbox_key))
    }

}