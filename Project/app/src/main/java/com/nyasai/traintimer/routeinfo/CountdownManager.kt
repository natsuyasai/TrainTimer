package com.nyasai.traintimer.routeinfo

import com.nyasai.traintimer.database.RouteDetail
import java.time.LocalTime
import java.time.format.DateTimeParseException
import java.util.*

/**
 * カウントダウンタイマー関連機能を管理するクラス
 */
class CountdownManager {
    
    /**
     * カウントダウン時間をMM:SS形式でフォーマット
     */
    fun formatCountdownTime(diffSeconds: Long): String {
        return when {
            diffSeconds < 0 -> "--:--"
            diffSeconds < 60 -> {
                val seconds = diffSeconds % 60
                "00:${String.format(Locale.JAPAN,"%02d", seconds)}"
            }
            else -> {
                val minutes = diffSeconds / 60
                val seconds = diffSeconds % 60
                "${String.format(Locale.JAPAN,"%02d", minutes)}:${String.format(Locale.JAPAN,"%02d", seconds)}"
            }
        }
    }
    
    /**
     * 次の時刻情報構築
     */
    fun buildNextTimeInfo(countItem: RouteDetail): String {
        return buildString {
            append("${countItem.departureTime.ifEmpty { "--:--" }}\n")
            append("${countItem.trainType.ifEmpty { "--"}}\n")
            append(countItem.destination.ifEmpty { "--" })
        }
    }
    
    /**
     * 現在時刻より先で最も近い電車のインデックスを取得
     * 深夜0時～3時は24時～27時として扱う
     */
    fun findNextTrainIndex(routeDetails: List<RouteDetail>): Int {
        val now = LocalTime.now()
        
        // 現在時刻を分単位で計算（深夜0時～3時59分は24時～27時59分として扱う）
        val nowMinutes = if (now.hour < 4) {
            (now.hour + 24) * 60 + now.minute
        } else {
            now.hour * 60 + now.minute
        }
        
        return routeDetails.indexOfFirst { routeDetail ->
            try {
                val departureTime = routeDetail.departureTime
                if (departureTime.isNotEmpty()) {
                    val trainTime = LocalTime.parse(departureTime)
                    val trainMinutes = if (trainTime.hour < 4) {
                        (trainTime.hour + 24) * 60 + trainTime.minute
                    } else {
                        trainTime.hour * 60 + trainTime.minute
                    }
                    trainMinutes > nowMinutes
                } else {
                    false
                }
            } catch (e: DateTimeParseException) {
                false
            }
        }
    }
}