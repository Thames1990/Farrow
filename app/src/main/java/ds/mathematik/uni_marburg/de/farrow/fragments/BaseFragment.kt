package ds.mathematik.uni_marburg.de.farrow.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ds.mathematik.uni_marburg.de.farrow.model.event.EventViewModel
import ds.mathematik.uni_marburg.de.farrow.utils.getViewModel

abstract class BaseFragment : Fragment() {

    protected abstract val layout: Int

    protected lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = getViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layout, container, false)

}