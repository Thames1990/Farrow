package ds.mathematik.uni_marburg.de.farrow.model.event

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.mapbox.mapboxsdk.geometry.LatLng
import ds.mathematik.uni_marburg.de.farrow.utils.roomDb
import java.util.*

@Database(entities = [Event::class], version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {

    abstract fun dao(): EventDao

    companion object {
        private var database: EventDatabase? = null

        private val random = Random()

        private fun generatePosition() = LatLng(
            (random.nextDouble() * -180.0) + 90.0,
            (random.nextDouble() * -360.0) + 180.0
        )

        @Synchronized
        fun get(context: Context): EventDatabase =
            database ?: context.applicationContext.roomDb<EventDatabase>(
                name = "events.db",
                onFirstCreate = {
                    val eventDatabase: EventDatabase = get(context)
                    val dao: EventDao = eventDatabase.dao()

                    (0 until 10000L).map {
                        val position: LatLng = generatePosition()
                        val event = Event(
                            id = it,
                            latitude = position.latitude,
                            longitude = position.longitude
                        )
                        dao.insert(event)
                    }
                }
            ).also { database = it }
    }

}