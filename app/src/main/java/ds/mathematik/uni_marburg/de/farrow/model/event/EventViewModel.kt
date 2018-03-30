package ds.mathematik.uni_marburg.de.farrow.model.event

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: EventDao = EventDatabase.get(application).dao()

    val liveData: LiveData<List<Event>>
        get() = dao.getEvents()

}