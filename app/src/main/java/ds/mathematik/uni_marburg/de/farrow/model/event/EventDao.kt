package ds.mathematik.uni_marburg.de.farrow.model.event

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import ds.mathematik.uni_marburg.de.farrow.model.BaseDao

@Dao
abstract class EventDao : BaseDao<Event> {

    @Query("SELECT * FROM events")
    abstract fun getEvents(): LiveData<List<Event>>

}