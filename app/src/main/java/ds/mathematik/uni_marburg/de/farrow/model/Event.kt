package ds.mathematik.uni_marburg.de.farrow.model

import android.arch.persistence.room.PrimaryKey

data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long
)