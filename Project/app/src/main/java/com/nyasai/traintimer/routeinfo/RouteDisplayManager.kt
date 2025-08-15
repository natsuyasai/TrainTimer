package com.nyasai.traintimer.routeinfo

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nyasai.traintimer.database.RouteDetail

/**
 * 路線表示リスト管理機能を管理するクラス
 */
class RouteDisplayManager(
    private val countdownManager: CountdownManager
) {
    
    /**
     * 表示用路線詳細の更新処理
     */
    suspend fun updateDisplayRouteDetails(
        routeItems: List<RouteDetail>,
        viewModel: RouteInfoViewModel,
        displayRouteDetails: SnapshotStateList<RouteDetail>,
        listState: LazyListState
    ) {
        if (routeItems.isNotEmpty()) {
            refreshDisplayRouteDetails(viewModel, displayRouteDetails)
            performAutoScroll(displayRouteDetails, listState)
        }
    }
    
    /**
     * 表示リストの更新
     */
    fun refreshDisplayRouteDetails(
        viewModel: RouteInfoViewModel,
        displayRouteDetails: SnapshotStateList<RouteDetail>
    ) {
        viewModel.clearDisplayCache()
        val newDisplayItems = viewModel.getDisplayRouteDetailItems(false)
        displayRouteDetails.clear()
        displayRouteDetails.addAll(newDisplayItems)
        viewModel.updateCurrentCountItem(false)
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