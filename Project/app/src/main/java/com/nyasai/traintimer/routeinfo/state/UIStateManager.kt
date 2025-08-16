package com.nyasai.traintimer.routeinfo.state

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.routeinfo.CountdownState
import com.nyasai.traintimer.routeinfo.FilterDialogState
import com.nyasai.traintimer.routeinfo.CountdownActions
import com.nyasai.traintimer.routeinfo.FilterDialogActions

/**
 * UI状態管理を担当するクラス
 */
@Stable
class UIStateManager {
    var filterDialogState by mutableStateOf(FilterDialogState())
        private set
    
    var countdownState by mutableStateOf(CountdownState())
        private set
    
    var filterUpdateTrigger by mutableStateOf(0)
        private set
    
    // SnapshotStateListを使用してCompose最適化
    val displayRouteDetails: SnapshotStateList<RouteDetail> = mutableStateListOf()
    
    // フィルタダイアログアクション
    val filterDialogActions = object : FilterDialogActions {
        override fun showFilterDialog(filterItems: List<FilterInfo>) {
            filterDialogState = filterDialogState.copy(
                showFilterDialog = true,
                localFilterItems = filterItems.toList()
            )
        }
        
        override fun hideFilterDialog() {
            filterDialogState = filterDialogState.copy(showFilterDialog = false)
        }
        
        override fun updateLocalFilterItems(items: List<FilterInfo>) {
            filterDialogState = filterDialogState.copy(localFilterItems = items)
        }
    }
    
    // カウントダウンアクション
    val countdownActions = object : CountdownActions {
        override fun updateCountdown(text: String, info: String) {
            countdownState = countdownState.copy(
                countdownText = text,
                nextTimeInfo = info
            )
        }
        
        override fun startCountdown() {
            countdownState = countdownState.copy(isActive = true)
        }
        
        override fun stopCountdown() {
            countdownState = countdownState.copy(
                isActive = false,
                countdownText = "--:--",
                nextTimeInfo = ""
            )
        }
    }
    
    /**
     * フィルタ更新トリガーをインクリメント
     */
    fun incrementFilterUpdateTrigger() {
        filterUpdateTrigger++
    }
    
    /**
     * 表示用路線詳細リストを更新
     */
    fun updateDisplayRouteDetails(newDetails: List<RouteDetail>) {
        displayRouteDetails.clear()
        displayRouteDetails.addAll(newDetails)
    }
    
    /**
     * 表示用路線詳細リストをクリア
     */
    fun clearDisplayRouteDetails() {
        displayRouteDetails.clear()
    }
}