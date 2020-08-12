package com.nyasai.traintimer.routeinfo

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.nyasai.traintimer.database.RouteDetails

@BindingAdapter("departureTime")
fun TextView.setDepartureTime(item: RouteDetails?) {
    item?.let {
        text = item.departureTime
    }
}

@BindingAdapter("stationName")
fun TextView.setStationName(item: RouteDetails?) {
    item?.let {
        text = item.trainType
    }
}

@BindingAdapter("destination")
fun TextView.setDestination(item: RouteDetails?) {
    item?.let {
        text = item.destination
    }
}