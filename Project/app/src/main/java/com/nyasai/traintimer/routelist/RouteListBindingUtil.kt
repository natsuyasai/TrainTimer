package com.nyasai.traintimer.routelist

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.nyasai.traintimer.database.RouteListItem


@BindingAdapter("routeName")
fun TextView.setRouteName(item: RouteListItem?) {
    item?.let {
        text = item.routeName
    }
}

@BindingAdapter("stationName")
fun TextView.setStationName(item: RouteListItem?) {
    item?.let {
        text = item.stationName
    }
}

@BindingAdapter("direction")
fun TextView.setDirection(item: RouteListItem?) {
    item?.let {
        text = item.direction
    }
}