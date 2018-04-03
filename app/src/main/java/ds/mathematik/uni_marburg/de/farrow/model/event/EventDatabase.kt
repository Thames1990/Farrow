package ds.mathematik.uni_marburg.de.farrow.model.event

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.mapbox.mapboxsdk.geometry.LatLng
import ds.mathematik.uni_marburg.de.farrow.utils.roomDb
import java.lang.Math.PI
import java.util.*


@Database(entities = [Event::class], version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {

    abstract fun dao(): EventDao

    companion object {
        private var database: EventDatabase? = null

        private fun generatePosition(
            latitude: Double = 8.811229,
            longitude: Double = 50.809537,
            radiusInMeters: Int = 3000
        ): LatLng {
            val random = Random()

            // Convert radiusInMeters from meters to degrees
            val radiusInDegrees = (radiusInMeters / 111000f).toDouble()

            val u = random.nextDouble()
            val v = random.nextDouble()
            val w = radiusInDegrees * Math.sqrt(u)
            val t = 2.0 * PI * v
            val x = w * Math.cos(t)
            val y = w * Math.sin(t)

            // Adjust the x-coordinate for the shrinking of the east-west distances
            val newLatitude = x / Math.cos(Math.toRadians(longitude))

            val foundLongitude = newLatitude + latitude
            val foundLatitude = y + longitude

            return LatLng(foundLatitude, foundLongitude)
        }

        @Synchronized
        fun get(context: Context): EventDatabase = database ?: context.roomDb<EventDatabase>(
            name = "events.db",
            onFirstCreate = {
                val eventDatabase: EventDatabase = get(context)
                val dao: EventDao = eventDatabase.dao()

                repeat(
                    times = 10000,
                    action = {
                        val position: LatLng = generatePosition()
                        val event = Event(
                            latitude = position.latitude,
                            longitude = position.longitude
                        )
                        dao.insert(event)
                    }
                )
            }
        ).also { database = it }
    }

}