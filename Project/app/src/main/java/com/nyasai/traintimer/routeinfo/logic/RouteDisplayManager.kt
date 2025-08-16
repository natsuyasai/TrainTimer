package com.nyasai.traintimer.routeinfo.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.routeinfo.RouteInfoScreenState
import com.nyasai.traintimer.routeinfo.logic.CountdownManager

/**
 * 路線表示リスト管理機能を管理するクラス
 */
class RouteDisplayManager(
    private val countdownManager: CountdownManager
) {
    
    /**
     * 表示用路線詳細の更新処理（State Holder版）
     */
    suspend fun updateDisplayRouteDetailsWithState(
        routeItems: List<RouteDetail>,
        screenState: RouteInfoScreenState,
        displayRouteDetails: SnapshotStateList<RouteDetail>,
        listState: LazyListState
    ) {
        if (routeItems.isNotEmpty()) {
            refreshDisplayRouteDetailsWithState(screenState, displayRouteDetails)
            performAutoScroll(displayRouteDetails, listState)
        }
    }
    
    /**
     * 表示リストの更新（State Holder版）
     */
    fun refreshDisplayRouteDetailsWithState(
        screenState: RouteInfoScreenState,
        displayRouteDetails: SnapshotStateList<RouteDetail>
    ) {
        screenState.clearDisplayCache()
        val newDisplayItems = screenState.getDisplayRouteDetailItems(false)
        displayRouteDetails.clear()
        displayRouteDetails.addAll(newDisplayItems)
        screenState.updateCurrentCountItem(false)
    }
    
    /**
     * 自動スクロール処理
     */
    suspend fun performAutoScroll(
        displayRouteDetails: List<RouteDetail>,
        listState: LazyListState
    ) {
        if (displayRouteDetails.isNotEmpty()) {
            val nextTrainIndex = countdownManager.findNextTrainIndex(displayRouteDetails)
            if (nextTrainIndex >= 0) {
                listState.animateScrollToItem(nextTrainIndex)
            }
        }
    }
}