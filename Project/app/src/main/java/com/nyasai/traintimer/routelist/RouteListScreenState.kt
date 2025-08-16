package com.nyasai.traintimer.routelist

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.state.*
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import kotlinx.coroutines.*

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
 * 分割されたマネージャーを組み合わせた設計
 */
@Stable
class RouteListScreenState(
    private val database: RouteDatabaseDao,
    private val application: Application
) {
    // Job管理
    private val _job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + _job)
    
    // 分割されたマネージャー
    private val routeListDataManager = RouteListDataManager(database, coroutineScope)
    private val routeSearchManager = RouteSearchManager()
    private val routeSortManager = RouteSortManager(database, coroutineScope)
    private val routeUpdateManager = RouteUpdateManager(routeSearchManager, routeListDataManager)
    private val uiStateManager = RouteListUIStateManager()
    
    // 各マネージャーへのアクセス委譲（プロパティ）
    val routeList: LiveData<List<RouteListItem>> = routeListDataManager.routeList
    val isEditMode: LiveData<Boolean> = routeListDataManager.isEditMode
    
    // UI状態管理への委譲（プロパティ）
    val isDragging: Boolean get() = uiStateManager.isDragging
    val showSearchDialog: Boolean get() = uiStateManager.showSearchDialog
    val showStationSelectDialog: Boolean get() = uiStateManager.showStationSelectDialog
    val isLoading: Boolean get() = uiStateManager.isLoading
    val localRouteList: SnapshotStateList<RouteListItem> = uiStateManager.localRouteList
    
    // 旧UIコンポーネント用のプロパティ（アダプタ）
    var dialogState by mutableStateOf(DialogState())
        private set
    
    var searchState by mutableStateOf(SearchState())
        private set
    
    var dragDropState by mutableStateOf(DragDropState())
        private set
    
    var colorUpdateTrigger by mutableStateOf(0)
        private set
    
    // 旧UIコンポーネント用のアクション（アダプタ）
    val dialogActions = object : DialogActions {
        override fun showSearchDialog() {
            dialogState = dialogState.copy(showSearchDialog = true)
            uiStateManager.showSearchDialog()
        }
        
        override fun hideSearchDialog() {
            dialogState = dialogState.copy(showSearchDialog = false)
            uiStateManager.hideSearchDialog()
        }
        
        override fun showStationSelectDialog() {
            dialogState = dialogState.copy(showStationSelectDialog = true)
            uiStateManager.showStationSelectDialog()
        }
        
        override fun hideStationSelectDialog() {
            dialogState = dialogState.copy(showStationSelectDialog = false)
            uiStateManager.hideStationSelectDialog()
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
    
    val dragDropActions = object : DragDropActions {
        override fun startDrag(index: Int) {
            uiStateManager.startDragging()
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
            uiStateManager.stopDragging()
            dragDropState = dragDropState.copy(isDragging = false)
        }
        
        override fun resetDragState() {
            dragDropState = DragDropState()
            uiStateManager.stopDragging()
        }
    }
    
    // メソッドの委譲
    
    /**
     * クリーンアップ処理
     */
    fun onCleared() {
        routeSearchManager.dispose()
        _job.cancel()
    }
    
    /**
     * リストアイテム削除
     */
    fun deleteListItem(dataId: Long) {
        routeListDataManager.deleteListItem(dataId)
    }
    
    /**
     * 駅リスト取得
     */
    fun getStationList(stationName: String) = routeSearchManager.getStationList(stationName)
    
    /**
     * 行先取得(駅名)
     */
    fun getDestinationFromStationName(stationName: String) =
        routeSearchManager.getDestinationFromStationName(stationName)
    
    /**
     * 行先取得(URL)
     */
    fun getDestinationFromUrl(stationUrl: String) =
        routeSearchManager.getDestinationFromUrl(stationUrl)
    
    /**
     * 行先キー分割
     */
    fun splitDestinationKey(keyString: String) =
        routeSearchManager.splitDestinationKey(keyString)
    
    /**
     * 時刻表情報取得
     */
    suspend fun getTimeTableInfo(
        timeTableUrl: String,
        notifyMaxCountCallback: ((Int) -> Unit),
        notifyCountCallback: (() -> Unit)
    ) = routeSearchManager.getTimeTableInfo(
        timeTableUrl,
        notifyMaxCountCallback,
        notifyCountCallback
    )
    
    /**
     * 路線リストアイテム登録
     */
    fun registerRouteListItem(
        routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>,
        searchRouteListItem: RouteListItem
    ): Long {
        return routeListDataManager.registerRouteListItem(routeInfo, searchRouteListItem)
    }
    
    /**
     * 時刻表情報登録
     */
    fun registerRouteInfoDetailItems(
        routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>,
        parentDataId: Long
    ) {
        routeListDataManager.registerRouteInfoDetailItems(routeInfo, parentDataId)
    }
    
    /**
     * 路線情報更新
     */
    suspend fun updateRouteInfo(
        item: RouteListItem,
        notifyMaxCountCallback: ((Int) -> Unit),
        notifyCountCallback: (() -> Unit)
    ): Boolean {
        return routeUpdateManager.updateRouteInfo(item, notifyMaxCountCallback, notifyCountCallback)
    }
    
    /**
     * ソート情報更新
     */
    fun updateSortIndex(from: Int, to: Int) {
        routeSortManager.updateSortIndex(routeList, from, to)
    }
    
    /**
     * 編集モード切り替え
     */
    fun switchEditMode() {
        routeListDataManager.switchEditMode()
    }
    
    /**
     * 路線アイテムの色を更新
     */
    fun updateRouteListItemColor(dataId: Long, color: Int?) {
        routeListDataManager.updateRouteListItemColor(dataId, color)
    }
    
    // UI状態管理メソッド
    
    /**
     * ドラッグ開始
     */
    fun startDragging() {
        uiStateManager.startDragging()
    }
    
    /**
     * ドラッグ終了
     */
    fun stopDragging() {
        uiStateManager.stopDragging()
    }
    
    /**
     * 検索ダイアログを表示
     */
    fun showSearchDialog() {
        uiStateManager.showSearchDialog()
    }
    
    /**
     * 検索ダイアログを非表示
     */
    fun hideSearchDialog() {
        uiStateManager.hideSearchDialog()
    }
    
    /**
     * ステーションセレクトダイアログを表示
     */
    fun showStationSelectDialog() {
        uiStateManager.showStationSelectDialog()
    }
    
    /**
     * ステーションセレクトダイアログを非表示
     */
    fun hideStationSelectDialog() {
        uiStateManager.hideStationSelectDialog()
    }
    
    /**
     * ローディング開始
     */
    fun startLoading() {
        uiStateManager.startLoading()
    }
    
    /**
     * ローディング終了
     */
    fun stopLoading() {
        uiStateManager.stopLoading()
    }
    
    /**
     * ローカルルートリストを更新
     */
    fun updateLocalRouteList(items: List<RouteListItem>) {
        uiStateManager.updateLocalRouteList(items)
    }
    
    /**
     * ローカルルートリストをクリア
     */
    fun clearLocalRouteList() {
        uiStateManager.clearLocalRouteList()
    }
    
    // 旧UIコンポーネント用のアダプタメソッド
    
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
        val updatedList = localRouteList.map { item ->
            if (item.dataId == updatedItem.dataId) {
                updatedItem
            } else {
                item
            }
        }
        updateLocalRouteList(updatedList)
    }
}

/**
 * RouteListScreenStateを作成するComposable関数
 */
@Composable
fun rememberRouteListScreenState(
    database: RouteDatabaseDao,
    application: Application
): RouteListScreenState {
    return remember { 
        RouteListScreenState(database, application) 
    }
}