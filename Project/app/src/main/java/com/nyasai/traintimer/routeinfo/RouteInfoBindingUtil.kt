package com.nyasai.traintimer.routeinfo

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.nyasai.traintimer.database.RouteDetails
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.define.Define

/**
 * 時刻
 */
@BindingAdapter("departureTime")
fun TextView.setDepartureTime(item: RouteDetails?) {
    item?.let {
        text = item.departureTime
    }
}

/**
 * 電車種別
 */
@BindingAdapter("trainType")
fun TextView.setTrainType(item: RouteDetails?) {
    item?.let {
        text = item.trainType
    }
}

/**
 * 方面
 */
@BindingAdapter("destination")
fun TextView.setDestination(item: RouteDetails?) {
    item?.let {
        text = item.destination
    }
}

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
@BindingAdapter("direction")
fun TextView.setDirection(item: RouteListItem?) {
    item?.let {
        text = item.direction
    }
}

/**
 * ダイヤ種別
 */
@BindingAdapter("diagramType")
fun TextView.setDiagramType(currentDiagramType: Define.DiagramType) {
    text = when(currentDiagramType){
        Define.DiagramType.Weekday -> "平日"
        Define.DiagramType.Saturday -> "土曜"
        Define.DiagramType.Sunday -> "日曜・祝日"
    }
}
