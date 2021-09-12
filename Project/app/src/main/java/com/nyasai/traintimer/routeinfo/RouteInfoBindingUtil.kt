package com.nyasai.traintimer.routeinfo

import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.define.Define
import java.time.LocalTime

/**
 * 時刻
 */
@BindingAdapter("departureTime")
fun TextView.setDepartureTime(item: RouteDetail?) {
    item?.let {
        text = item.departureTime
        setTextColor(ContextCompat.getColor(context, getTimeTextColor(item.departureTime)))
    }
}

/**
 * 電車種別
 */
@BindingAdapter("trainType")
fun TextView.setTrainType(item: RouteDetail?) {
    item?.let {
        text = item.trainType
        setTextColor(
            ContextCompat.getColor(
                context,
                getTimeTextColor(item.departureTime, R.color.textRed)
            )
        )
    }
}

/**
 * 方面
 */
@BindingAdapter("destination")
fun TextView.setDestination(item: RouteDetail?) {
    item?.let {
        text = item.destination
        setTextColor(ContextCompat.getColor(context, getTimeTextColor(item.departureTime)))
    }
}

/**
 * ダイヤ種別
 */
@BindingAdapter("diagramType")
fun TextView.setDiagramType(currentDiagramType: Define.DiagramType) {
    text = when (currentDiagramType) {
        Define.DiagramType.Weekday -> "[平日]"
        Define.DiagramType.Saturday -> "[土曜]"
        Define.DiagramType.Sunday -> "[日曜・祝日]"
    }
    val color = when (currentDiagramType) {
        Define.DiagramType.Weekday -> R.color.weekday
        Define.DiagramType.Saturday -> R.color.saturday
        Define.DiagramType.Sunday -> R.color.sunday
    }
    setTextColor(ContextCompat.getColor(context, color))
}

/**
 * 時刻情報テキストカラー取得
 */
fun getTimeTextColor(departureTimeStr: String, defaultColor: Int = R.color.textColor): Int {
    val departureTime = LocalTime.parse(departureTimeStr)
    val now = LocalTime.now()
    // 0～3時以外は現在時刻未満を無効に設定
    if (departureTime.hour !in 0..3 && departureTime < now) {
        return R.color.textGray
    } else if (departureTime.hour in 0..3) {
        // 0～3時なら日付変更前と後で比較方法変更
        return if (now.hour in 0..3) {
            if (departureTime < now) {
                R.color.textGray
            } else {
                defaultColor
            }
        } else {
            defaultColor
        }
    } else {
        return defaultColor
    }
}