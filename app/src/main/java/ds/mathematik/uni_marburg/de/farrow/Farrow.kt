package ds.mathematik.uni_marburg.de.farrow

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import ds.mathematik.uni_marburg.de.farrow.model.event.EventDao
import ds.mathematik.uni_marburg.de.farrow.model.event.EventDatabase

class Farrow : Application() {

    companion object {
        lateinit var eventDao: EventDao
    }

    override fun onCreate() {
        super.onCreate()
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_key))
        eventDao = EventDatabase.get(this).dao()
    }

}