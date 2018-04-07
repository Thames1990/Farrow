package ds.mathematik.uni_marburg.de.farrow.fragments

import android.location.Address
import android.os.Bundle
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import ca.allanwang.kau.utils.inflate
import ca.allanwang.kau.utils.withLinearAdapter
import com.mapbox.api.staticmap.v1.MapboxStaticMap
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation
import com.mapbox.geocoder.android.AndroidGeocoder
import com.mapbox.geojson.Point
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.squareup.picasso.Picasso
import ds.mathematik.uni_marburg.de.farrow.BuildConfig
import ds.mathematik.uni_marburg.de.farrow.R
import ds.mathematik.uni_marburg.de.farrow.model.event.Event
import ds.mathematik.uni_marburg.de.farrow.utils.observe
import ds.mathematik.uni_marburg.de.farrow.utils.showWithOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_row.*
import kotlinx.android.synthetic.main.fragment_events.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EventsFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_events

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

        class ViewHolder(
            override val containerView: View
        ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

            fun bindTo(event: Event?) {
                if (event != null) {
                    setMediaImage(event)
                    setSupportingText(event)
                }
            }

            private fun setMediaImage(event: Event) {
                val cameraPoint = Point.fromLngLat(event.longitude, event.latitude)
                val mapStaticImage = MapboxStaticMap
                    .builder()
                    .accessToken(BuildConfig.MAPBOX_KEY)
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

            private fun setSupportingText(event: Event) = doAsync {
                val addresses: List<Address> = AndroidGeocoder(containerView.context).apply {
                    setAccessToken(BuildConfig.MAPBOX_KEY)
                }.getFromLocation(event.latitude, event.longitude, 1)

                uiThread {
                    supporting_text.text =
                            if (addresses.isNotEmpty()) addresses
                                .first()
                                .getAddressLine(0)
                                .replace(
                                    oldValue = "unnamed road, ",
                                    newValue = "",
                                    ignoreCase = true
                                )
                                .replace(oldValue = ", ", newValue = "\n")
                            else "${event.latitude}\n${event.longitude}"
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