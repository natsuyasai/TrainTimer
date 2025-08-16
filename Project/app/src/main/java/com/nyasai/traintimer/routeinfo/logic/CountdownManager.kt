package com.nyasai.traintimer.routeinfo.logic

import com.nyasai.traintimer.database.RouteDetail
import java.util.Locale

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
                "00:${String.Companion.format(Locale.JAPAN,"%02d", seconds)}"
            }
            else -> {
                val minutes = diffSeconds / 60
                val seconds = diffSeconds % 60
                "${String.Companion.format(Locale.JAPAN,"%02d", minutes)}:${
                    String.Companion.format(
                        Locale.JAPAN,"%02d", seconds)}"
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
     * 現在時刻より先のアイテムが見つからない場合は一番先頭の要素（インデックス0）を返す
     */
    fun findNextTrainIndex(routeDetails: List<RouteDetail>): Int {
        return TimeComparisonUtils.findNextTrainIndex(routeDetails)
    }
}