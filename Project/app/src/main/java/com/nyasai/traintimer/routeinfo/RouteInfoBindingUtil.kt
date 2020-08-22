package com.nyasai.traintimer.routeinfo

import android.graphics.Color
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.nyasai.traintimer.database.RouteDetails
import com.nyasai.traintimer.define.Define
import java.time.LocalTime

/**
 * 時刻
 */
@BindingAdapter("departureTime")
fun TextView.setDepartureTime(item: RouteDetails?) {
    item?.let {
        text = item.departureTime
        if(LocalTime.parse(item.departureTime) < LocalTime.now()){
            setTextColor(Color.GRAY)
        }
        else{
            setTextColor(Color.BLACK)
        }
    }
}

/**
 * 電車種別
 */
@BindingAdapter("trainType")
fun TextView.setTrainType(item: RouteDetails?) {
    item?.let {
        text = item.trainType
        if(LocalTime.parse(item.departureTime) < LocalTime.now()){
            setTextColor(Color.GRAY)
        }
        else{
            setTextColor(Color.RED)
        }
    }
}

/**
 * 方面
 */
@BindingAdapter("destination")
fun TextView.setDestination(item: RouteDetails?) {
    item?.let {
        text = item.destination
        if(LocalTime.parse(item.departureTime) < LocalTime.now()){
            setTextColor(Color.GRAY)
        }
        else{
            setTextColor(Color.BLACK)
        }
    }
}

/**
 * ダイヤ種別
 */
@BindingAdapter("diagramType")
fun TextView.setDiagramType(currentDiagramType: Define.DiagramType) {
    text = when(currentDiagramType){
        Define.DiagramType.Weekday -> "[平日]"
        Define.DiagramType.Saturday -> "[土曜]"
        Define.DiagramType.Sunday -> "[日曜・祝日]"
    }
    val color = when(currentDiagramType){
        Define.DiagramType.Weekday -> Color.BLACK
        Define.DiagramType.Saturday -> Color.BLUE
        Define.DiagramType.Sunday -> Color.RED
    }
    setTextColor(color)
}
