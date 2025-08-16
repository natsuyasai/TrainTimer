package com.nyasai.traintimer.routeinfo.logic

import com.nyasai.traintimer.database.RouteDetail
import java.time.LocalTime
import java.time.format.DateTimeParseException

/**
 * 時刻比較の共通ユーティリティクラス
 * 深夜0時～3時は24時～27時として扱う統一されたロジックを提供
 */
object TimeComparisonUtils {

    /**
     * 時刻の状態
     */
    enum class TimeStatus {
        PAST, FUTURE, CURRENT, INVALID, NEXT_DAY
    }

    /**
     * 現在時刻を正規化された秒数で取得
     * 深夜0時～3時は24時～27時として扱う
     */
    fun getCurrentTimeInSeconds(): Long {
        val now = LocalTime.now()
        return if (now.hour < 4) {
            ((now.hour + 24) * 60 + now.minute) * 60L + now.second
        } else {
            (now.hour * 60 + now.minute) * 60L + now.second
        }
    }

    /**
     * 現在時刻を正規化された分数で取得
     * 深夜0時～3時は24時～27時として扱う
     */
    fun getCurrentTimeInMinutes(): Int {
        val now = LocalTime.now()
        return if (now.hour < 4) {
            (now.hour + 24) * 60 + now.minute
        } else {
            now.hour * 60 + now.minute
        }
    }

    /**
     * 電車の出発時刻を正規化された秒数で取得
     * 深夜0時～3時は24時～27時として扱う
     */
    fun getTrainTimeInSeconds(departureTime: String): Long? {
        return try {
            if (departureTime.isEmpty()) {
                null
            } else {
                val trainTime = LocalTime.parse(departureTime)
                if (trainTime.hour < 4) {
                    ((trainTime.hour + 24) * 60 + trainTime.minute) * 60L + trainTime.second
                } else {
                    (trainTime.hour * 60 + trainTime.minute) * 60L + trainTime.second
                }
            }
        } catch (e: DateTimeParseException) {
            null
        }
    }

    /**
     * 電車の出発時刻を正規化された分数で取得
     * 深夜0時～3時は24時～27時として扱う
     */
    fun getTrainTimeInMinutes(departureTime: String): Int? {
        return try {
            if (departureTime.isEmpty()) {
                null
            } else {
                val trainTime = LocalTime.parse(departureTime)
                if (trainTime.hour < 4) {
                    (trainTime.hour + 24) * 60 + trainTime.minute
                } else {
                    trainTime.hour * 60 + trainTime.minute
                }
            }
        } catch (e: DateTimeParseException) {
            null
        }
    }

    /**
     * 電車の時刻状態を取得
     */
    fun getTimeStatus(routeDetail: RouteDetail): TimeStatus {
        return getTimeStatusWithContext(routeDetail, false)
    }

    /**
     * 電車の時刻状態を取得（コンテキスト付き）
     * @param routeDetail 路線詳細
     * @param isNextDayFallback 終電後のフォールバック選択かどうか
     */
    fun getTimeStatusWithContext(routeDetail: RouteDetail, isNextDayFallback: Boolean): TimeStatus {
        val departureTime = routeDetail.departureTime
        if (departureTime.isNullOrEmpty()) {
            return TimeStatus.INVALID
        }

        val nowSeconds = getCurrentTimeInSeconds()
        val trainSeconds = getTrainTimeInSeconds(departureTime)

        return if (trainSeconds != null) {
            when {
                trainSeconds < nowSeconds -> {
                    // 終電後のフォールバック選択の場合は、翌日の始発として扱う
                    if (isNextDayFallback) TimeStatus.NEXT_DAY else TimeStatus.PAST
                }
                trainSeconds > nowSeconds -> TimeStatus.FUTURE
                else -> TimeStatus.CURRENT
            }
        } else {
            TimeStatus.INVALID
        }
    }

    /**
     * 現在時刻より先で最も近い電車のインデックスを取得
     * 現在時刻より先のアイテムが見つからない場合は一番先頭の要素（インデックス0）を返す
     */
    fun findNextTrainIndex(routeDetails: List<RouteDetail>): Int {
        if (routeDetails.isEmpty()) {
            return -1
        }

        val nowMinutes = getCurrentTimeInMinutes()

        val nextTrainIndex = routeDetails.indexOfFirst { routeDetail ->
            val trainMinutes = getTrainTimeInMinutes(routeDetail.departureTime ?: "")
            trainMinutes != null && trainMinutes > nowMinutes
        }

        // 現在時刻より先のアイテムが見つからない場合は一番先頭の要素を返す
        return if (nextTrainIndex == -1) 0 else nextTrainIndex
    }

    /**
     * 指定した電車との時間差を秒単位で取得
     */
    fun getTimeDifferenceInSeconds(routeDetail: RouteDetail): Long {
        return try {
            val departureTime = routeDetail.departureTime
            if (departureTime.isNullOrEmpty()) {
                -1L
            } else {
                val now = LocalTime.now()
                val trainTime = LocalTime.parse(departureTime)
                
                // 分単位で時刻を計算（深夜0時～3時59分は24時～27時59分として扱う）
                val nowMinutes = getCurrentTimeInMinutes()
                val trainMinutes = getTrainTimeInMinutes(departureTime) ?: return -1L
                
                // 秒も考慮した差分計算
                val diffMinutes = trainMinutes - nowMinutes
                val diffSeconds = diffMinutes * 60 - now.second + trainTime.second
                
                diffSeconds.toLong()
            }
        } catch (e: Exception) {
            -1L
        }
    }

    /**
     * 現在時刻より先の最初の電車を検索
     * 見つからない場合はリストの最初の要素を返す
     */
    fun findNextTrain(routeDetails: List<RouteDetail>): RouteDetail? {
        if (routeDetails.isEmpty()) {
            return null
        }

        val nowMinutes = getCurrentTimeInMinutes()

        for (item in routeDetails) {
            val trainMinutes = getTrainTimeInMinutes(item.departureTime ?: "")
            if (trainMinutes != null && trainMinutes > nowMinutes) {
                return item
            }
        }

        // 現在時刻より先のアイテムが見つからない場合は一番先頭の要素を返す
        return routeDetails.firstOrNull()
    }

    /**
     * 指定された電車が終電後のフォールバック選択かどうかを判定
     */
    fun isNextDayFallback(routeDetails: List<RouteDetail>, selectedTrain: RouteDetail?): Boolean {
        if (routeDetails.isEmpty() || selectedTrain == null) {
            return false
        }

        val nowMinutes = getCurrentTimeInMinutes()

        // 現在時刻より先の電車があるかチェック
        val hasTrainAfterNow = routeDetails.any { item ->
            val trainMinutes = getTrainTimeInMinutes(item.departureTime ?: "")
            trainMinutes != null && trainMinutes > nowMinutes
        }

        // 現在時刻より先の電車がない場合、選択された電車がリストの最初の要素で、かつその時刻が現在時刻より前の場合
        return !hasTrainAfterNow && 
               routeDetails.firstOrNull() == selectedTrain &&
               run {
                   val selectedTrainMinutes = getTrainTimeInMinutes(selectedTrain.departureTime ?: "")
                   selectedTrainMinutes != null && selectedTrainMinutes <= nowMinutes
               }
    }
}