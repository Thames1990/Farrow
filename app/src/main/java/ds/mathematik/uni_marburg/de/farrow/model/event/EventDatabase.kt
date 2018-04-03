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

        private fun generatePosition(): LatLng {
            val random = Random()

            fun randomLatitude(from: Int = -85, to: Int = 85): Double {
                return from + (to - from) * random.nextDouble()
            }

            fun randomLongitude(from: Int = -180, to: Int = 180): Double {
                return from + (to - from) * random.nextDouble()
            }

            return LatLng(randomLatitude(), randomLongitude())
        }

        @Synchronized
        fun get(context: Context): EventDatabase =
                database ?: context.applicationContext.roomDb<EventDatabase>(
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