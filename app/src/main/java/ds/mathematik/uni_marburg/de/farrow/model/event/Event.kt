package ds.mathematik.uni_marburg.de.farrow.model.event

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.FloatRange
import com.mapbox.mapboxsdk.geometry.LatLng

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @FloatRange(from = -90.0, to = 90.0) val latitude: Double,
    @FloatRange(from = -180.0, to = 180.0) val longitude: Double
) {
    val position: LatLng
        get() = LatLng(latitude, longitude)
}