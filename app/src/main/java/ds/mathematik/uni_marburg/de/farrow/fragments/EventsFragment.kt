package ds.mathematik.uni_marburg.de.farrow.fragments

import android.os.Bundle
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mapbox.api.staticmap.v1.MapboxStaticMap
import com.mapbox.api.staticmap.v1.StaticMapCriteria
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation
import com.mapbox.geojson.Point
import com.squareup.picasso.Picasso
import ds.mathematik.uni_marburg.de.farrow.BuildConfig
import ds.mathematik.uni_marburg.de.farrow.R
import ds.mathematik.uni_marburg.de.farrow.model.event.Event
import ds.mathematik.uni_marburg.de.farrow.utils.inflate
import ds.mathematik.uni_marburg.de.farrow.utils.observe
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_row.*
import kotlinx.android.synthetic.main.fragment_events.*

class EventsFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_events

    private val eventAdapter = EventAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(eventViewModel.events, eventAdapter::submitList)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    private class EventAdapter : ListAdapter<Event, EventAdapter.ViewHolder>(diffCallback) {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder = ViewHolder(parent.inflate(R.layout.event_row))

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) = holder.bindTo(getItem(position))

        class ViewHolder(
            override val containerView: View
        ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

            fun bindTo(event: Event?) {
                if (event != null) {
                    val latitude: Double = event.latitude
                    val longitude: Double = event.longitude

                    title_text.text = event.id.toString()
                    subtitle_text.text = "$latitude\n$longitude"

                    val cameraPoint = Point.fromLngLat(latitude, longitude)

                    val mapStaticImage = MapboxStaticMap
                        .builder()
                        .accessToken(BuildConfig.MAPBOX_KEY)
                        .styleId(StaticMapCriteria.OUTDOORS_STYLE)
                        .cameraPoint(cameraPoint)
                        .cameraZoom(12.0)
                        .staticMarkerAnnotations(
                            listOf(
                                StaticMarkerAnnotation.builder()
                                    .lnglat(cameraPoint)
                                    .build()
                            )
                        )
                        .width(320)
                        .height(180)
                        .retina(true)
                        .build()

                    Picasso.get()
                        .load(mapStaticImage.url().toString())
                        .placeholder(R.color.cardview_dark_background)
                        .error(android.R.color.holo_red_dark)
                        .into(media_image)
                }
            }

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