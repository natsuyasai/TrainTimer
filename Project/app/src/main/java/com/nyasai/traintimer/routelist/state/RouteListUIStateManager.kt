package com.nyasai.traintimer.routelist.state

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nyasai.traintimer.database.RouteListItem

/**
 * RouteList画面のUI状態管理を担当するクラス
 */
@Stable
class RouteListUIStateManager {
    
    // ドラッグ&ドロップ中の状態
    var isDragging by mutableStateOf(false)
        private set
    
    // 検索ダイアログの状態
    var showSearchDialog by mutableStateOf(false)
        private set
    
    // ステーションセレクトダイアログの状態
    var showStationSelectDialog by mutableStateOf(false)
        private set
    
    // 検索中のローディング状態
    var isLoading by mutableStateOf(false)
        private set
    
    // ローカルでのルートリストアイテム（ドラッグ&ドロップ用）
    val localRouteList: SnapshotStateList<RouteListItem> = mutableStateListOf()
    
    /**
     * ドラッグ開始
     */
    fun startDragging() {
        isDragging = true
    }
    
    /**
     * ドラッグ終了
     */
    fun stopDragging() {
        isDragging = false
    }
    
    /**
     * 検索ダイアログを表示
     */
    fun showSearchDialog() {
        showSearchDialog = true
    }
    
    /**
     * 検索ダイアログを非表示
     */
    fun hideSearchDialog() {
        showSearchDialog = false
    }
    
    /**
     * ステーションセレクトダイアログを表示
     */
    fun showStationSelectDialog() {
        showStationSelectDialog = true
    }
    
    /**
     * ステーションセレクトダイアログを非表示
     */
    fun hideStationSelectDialog() {
        showStationSelectDialog = false
    }
    
    /**
     * ローディング開始
     */
    fun startLoading() {
        isLoading = true
    }
    
    /**
     * ローディング終了
     */
    fun stopLoading() {
        isLoading = false
    }
    
    /**
     * ローカルルートリストを更新
     */
    fun updateLocalRouteList(items: List<RouteListItem>) {
        localRouteList.clear()
        localRouteList.addAll(items)
    }
    
    /**
     * ローカルルートリストをクリア
     */
    fun clearLocalRouteList() {
        localRouteList.clear()
    }
}