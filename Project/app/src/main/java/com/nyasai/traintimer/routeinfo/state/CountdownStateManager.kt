package com.nyasai.traintimer.routeinfo.state

import androidx.compose.runtime.Stable
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.routeinfo.logic.TimeComparisonUtils

/**
 * カウントダウン状態管理を担当するクラス
 */
@Stable
class CountdownStateManager {
    
    /**
     * 次に表示するアイテムの現在時刻からの差分時間取得
     */
    fun getNextDiffTime(currentCountItem: RouteDetail?): Long {
        return currentCountItem?.let { countItem ->
            TimeComparisonUtils.getTimeDifferenceInSeconds(countItem)
        } ?: -1L
    }
}