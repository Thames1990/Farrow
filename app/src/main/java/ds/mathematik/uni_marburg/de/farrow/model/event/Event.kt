package ds.mathematik.uni_marburg.de.farrow.model.event

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.FloatRange

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @FloatRange(from = -90.0, to = 90.0) val latitude: Double,
    @FloatRange(from = -180.0, to = 180.0) val longitude: Double
)