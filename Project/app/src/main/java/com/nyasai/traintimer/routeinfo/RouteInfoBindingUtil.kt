package com.nyasai.traintimer.routeinfo

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import java.time.LocalTime

/**
 * 時刻情報のスタイル設定
 */
@BindingAdapter("departureTime")
fun TextView.setDepartureTime(item: RouteDetail?) {
    item?.let {
        text = item.departureTime
        setTextColor(ContextCompat.getColor(context, getTimeTextColor(item.departureTime)))
    }
}

/**
 * 電車種別のスタイル設定
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
 * 方面のスタイル設定
 */
@BindingAdapter("destination")
fun TextView.setDestination(item: RouteDetail?) {
    item?.let {
        text = item.destination
        setTextColor(ContextCompat.getColor(context, getTimeTextColor(item.departureTime)))
    }
}

/**
 * ダイヤ種別のスタイル設定
 */
@BindingAdapter("diagramType")
fun TextView.setDiagramType(currentDiagramType: YahooRouteInfoGetter.Companion.DiagramType) {
    text = when (currentDiagramType) {
        YahooRouteInfoGetter.Companion.DiagramType.Weekday -> "[平日]"
        YahooRouteInfoGetter.Companion.DiagramType.Saturday -> "[土曜]"
        YahooRouteInfoGetter.Companion.DiagramType.Holiday -> "[日曜・祝日]"
        else -> ""
    }
    val color = when (currentDiagramType) {
        YahooRouteInfoGetter.Companion.DiagramType.Weekday -> R.color.weekday
        YahooRouteInfoGetter.Companion.DiagramType.Saturday -> R.color.saturday
        YahooRouteInfoGetter.Companion.DiagramType.Holiday -> R.color.sunday
        else -> R.color.weekday
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
        // 0～3時なら日付変更前と後で比較方法変更(日付は変わっているが終電がまだの場合をケア)
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