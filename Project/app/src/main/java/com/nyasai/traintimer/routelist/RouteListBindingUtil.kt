package com.nyasai.traintimer.routelist

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.nyasai.traintimer.database.RouteListItem

/**
 * 路線名
 */
@BindingAdapter("routeName")
fun TextView.setRouteName(item: RouteListItem?) {
    item?.let {
        text = item.routeName
    }
}

/**
 * 駅名
 */
@BindingAdapter("stationName")
fun TextView.setStationName(item: RouteListItem?) {
    item?.let {
        text = item.stationName
    }
}

/**
 * 行先
 */
@BindingAdapter("destination")
fun TextView.setDestination(item: RouteListItem?) {
    item?.let {
        text = item.destination
    }
}