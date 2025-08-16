package com.nyasai.traintimer.routelist

import androidx.compose.runtime.*
import com.nyasai.traintimer.database.RouteListItem

/**
 * RouteListScreenの状態管理クラス
 * State Hoistingパターンに基づいて状態を分離
 */

/**
 * ダイアログの表示状態
 */
@Stable
data class DialogState(
    val showSearchDialog: Boolean = false,
    val showStationSelectDialog: Boolean = false,
    val showDestinationSelectDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val showColorSelectDialog: Boolean = false,
    val selectedItem: RouteListItem? = null
)

/**
 * 検索関連の状態
 */
@Stable
data class SearchState(
    val stationOptions: Map<String, String> = emptyMap(),
    val destinationOptions: Map<String, String> = emptyMap(),
    val currentStationName: String = "",
    val searchStationName: String = "",
    val selectedStationItem: String = "",
    val selectedDestinationItem: String = ""
)

/**
 * ドラッグ&ドロップ関連の状態
 */
@Stable
data class DragDropState(
    val isDragging: Boolean = false,
    val draggedDistance: Float = 0f,
    val initialDraggedIndex: Int? = null,
    val currentDragOverIndex: Int? = null
)

/**
 * ダイアログ操作用のアクションインターフェース
 */
interface DialogActions {
    fun showSearchDialog()
    fun hideSearchDialog()
    fun showStationSelectDialog()
    fun hideStationSelectDialog()
    fun showDestinationSelectDialog()
    fun hideDestinationSelectDialog()
    fun showEditDialog(item: RouteListItem)
    fun hideEditDialog()
    fun showDeleteConfirmDialog()
    fun hideDeleteConfirmDialog()
    fun showColorSelectDialog()
    fun hideColorSelectDialog()
    fun clearSelectedItem()
}

/**
 * 検索操作用のアクションインターフェース
 */
interface SearchActions {
    fun updateStationOptions(options: Map<String, String>)
    fun updateDestinationOptions(options: Map<String, String>)
    fun updateCurrentStationName(name: String)
    fun updateSearchStationName(name: String)
    fun updateSelectedStationItem(item: String)
    fun updateSelectedDestinationItem(item: String)
    fun clearSearchState()
}

/**
 * ドラッグ&ドロップ操作用のアクションインターフェース
 */
interface DragDropActions {
    fun startDrag(index: Int)
    fun updateDrag(distance: Float, dragOverIndex: Int?)
    fun endDrag()
    fun resetDragState()
}

/**
 * RouteListScreen用の状態ホルダー
 */
@Stable
class RouteListScreenState {
    var dialogState by mutableStateOf(DialogState())
        private set
    
    var searchState by mutableStateOf(SearchState())
        private set
    
    var dragDropState by mutableStateOf(DragDropState())
        private set
    
    var colorUpdateTrigger by mutableStateOf(0)
        private set
    
    var localRouteList by mutableStateOf<List<RouteListItem>>(emptyList())
    
    // ダイアログアクション
    val dialogActions = object : DialogActions {
        override fun showSearchDialog() {
            dialogState = dialogState.copy(showSearchDialog = true)
        }
        
        override fun hideSearchDialog() {
            dialogState = dialogState.copy(showSearchDialog = false)
        }
        
        override fun showStationSelectDialog() {
            dialogState = dialogState.copy(showStationSelectDialog = true)
        }
        
        override fun hideStationSelectDialog() {
            dialogState = dialogState.copy(showStationSelectDialog = false)
        }
        
        override fun showDestinationSelectDialog() {
            dialogState = dialogState.copy(showDestinationSelectDialog = true)
        }
        
        override fun hideDestinationSelectDialog() {
            dialogState = dialogState.copy(showDestinationSelectDialog = false)
        }
        
        override fun showEditDialog(item: RouteListItem) {
            dialogState = dialogState.copy(showEditDialog = true, selectedItem = item)
        }
        
        override fun hideEditDialog() {
            dialogState = dialogState.copy(showEditDialog = false)
        }
        
        override fun showDeleteConfirmDialog() {
            dialogState = dialogState.copy(showDeleteConfirmDialog = true)
        }
        
        override fun hideDeleteConfirmDialog() {
            dialogState = dialogState.copy(showDeleteConfirmDialog = false)
        }
        
        override fun showColorSelectDialog() {
            dialogState = dialogState.copy(showColorSelectDialog = true)
        }
        
        override fun hideColorSelectDialog() {
            dialogState = dialogState.copy(showColorSelectDialog = false)
        }
        
        override fun clearSelectedItem() {
            dialogState = dialogState.copy(selectedItem = null)
        }
    }
    
    // 検索アクション
    val searchActions = object : SearchActions {
        override fun updateStationOptions(options: Map<String, String>) {
            searchState = searchState.copy(stationOptions = options)
        }
        
        override fun updateDestinationOptions(options: Map<String, String>) {
            searchState = searchState.copy(destinationOptions = options)
        }
        
        override fun updateCurrentStationName(name: String) {
            searchState = searchState.copy(currentStationName = name)
        }
        
        override fun updateSearchStationName(name: String) {
            searchState = searchState.copy(searchStationName = name)
        }
        
        override fun updateSelectedStationItem(item: String) {
            searchState = searchState.copy(selectedStationItem = item)
        }
        
        override fun updateSelectedDestinationItem(item: String) {
            searchState = searchState.copy(selectedDestinationItem = item)
        }
        
        override fun clearSearchState() {
            searchState = SearchState()
        }
    }
    
    // ドラッグ&ドロップアクション
    val dragDropActions = object : DragDropActions {
        override fun startDrag(index: Int) {
            dragDropState = dragDropState.copy(
                isDragging = true,
                initialDraggedIndex = index,
                currentDragOverIndex = index,
                draggedDistance = 0f
            )
        }
        
        override fun updateDrag(distance: Float, dragOverIndex: Int?) {
            dragDropState = dragDropState.copy(
                draggedDistance = distance,
                currentDragOverIndex = dragOverIndex
            )
        }
        
        override fun endDrag() {
            // ドラッグ終了時の処理は呼び出し元で実行
        }
        
        override fun resetDragState() {
            dragDropState = DragDropState()
        }
    }
    
    /**
     * 色更新トリガーをインクリメント
     */
    fun incrementColorUpdateTrigger() {
        colorUpdateTrigger++
    }
    
    /**
     * 選択アイテムを更新
     */
    fun updateSelectedItem(item: RouteListItem) {
        dialogState = dialogState.copy(selectedItem = item)
    }
    
    /**
     * ローカルリスト内の特定アイテムを更新（色変更等で即座に反映）
     */
    fun updateLocalRouteListItem(updatedItem: RouteListItem) {
        localRouteList = localRouteList.map { item ->
            if (item.dataId == updatedItem.dataId) {
                updatedItem
            } else {
                item
            }
        }
    }
}

/**
 * RouteListScreenStateを作成するComposable関数
 */
@Composable
fun rememberRouteListScreenState(): RouteListScreenState {
    return remember { RouteListScreenState() }
}