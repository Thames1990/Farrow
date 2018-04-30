package ds.mathematik.uni_marburg.de.farrow.fragments

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import ca.allanwang.kau.utils.bindViewResettable
import ca.allanwang.kau.utils.inflate
import ca.allanwang.kau.utils.withLinearAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import ds.mathematik.uni_marburg.de.farrow.R
import ds.mathematik.uni_marburg.de.farrow.model.event.Event
import ds.mathematik.uni_marburg.de.farrow.utils.observe
import ds.mathematik.uni_marburg.de.farrow.utils.showWithOptions

class EventsFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_events

    private val fab: FloatingActionButton by bindViewResettable(R.id.fab)
    private val recyclerView: RecyclerView by bindViewResettable(R.id.recycler_view)

    private val eventAdapter = EventAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.withLinearAdapter(eventAdapter)
        setupFab()

        observe(eventViewModel.events, eventAdapter::submitList)
    }

    private fun setupFab() = fab.showWithOptions(
        icon = GoogleMaterial.Icon.gmd_arrow_upward,
        tooltipTextRes = R.string.fab_tooltip_scroll_to_top,
        onClickListener = { recyclerView.scrollToPosition(0) }
    )

    private class EventAdapter : ListAdapter<Event, EventAdapter.ViewHolder>(diffCallback) {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder = ViewHolder(parent.inflate(R.layout.event_row))

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) = holder.bindTo(getItem(position))

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            fun bindTo(event: Event?) {
                if (event != null) {
                    setMediaImage(event)
                    setSupportingText(event)
                }
            }

            private fun setMediaImage(event: Event) = Unit

            private fun setSupportingText(event: Event) = Unit

        }

        companion object {
            private val diffCallback = object : DiffUtil.ItemCallback<Event>() {
                override fun areItemsTheSame(
                    oldItem: Event?,
                    newItem: Event?
                ): Boolean = oldItem?.id == newItem?.id

                override fun areContentsTheSame(
                    oldItem: Event?,
                    newItem: Event?
                ): Boolean = oldItem == newItem
            }
        }

    }

}