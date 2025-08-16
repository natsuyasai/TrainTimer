package com.nyasai.traintimer.routeinfo

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routeinfo.state.*
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import kotlinx.coroutines.Job

/**
 * RouteInfoScreen用の状態ホルダー（リファクタリング版）
 * 分割されたマネージャーを組み合わせた設計
 */
@Stable
class RouteInfoScreenState(
    private val database: RouteDatabaseDao,
    private val application: Application,
    private val parentId: Long
) {
    // Job管理
    private val _job = Job()
    
    // 分割されたマネージャー
    private val routeDataManager = RouteDataManager(database, parentId)
    private val diagramTypeManager = DiagramTypeManager()
    private val countdownStateManager = CountdownStateManager()
    private val uiStateManager = UIStateManager()
    
    // 各マネージャーへのアクセス委譲（プロパティ）
    val routeInfo: LiveData<RouteListItem?> = routeDataManager.routeInfo
    val routeItems: LiveData<List<RouteDetail>> = routeDataManager.routeItems
    val filterInfo: LiveData<List<FilterInfo>> = routeDataManager.filterInfo
    val currentCountItem: LiveData<RouteDetail?> = routeDataManager.currentCountItem
    val currentDiagramType: LiveData<YahooRouteInfoGetter.Companion.DiagramType> = diagramTypeManager.currentDiagramType
    
    // UI状態管理への委譲（プロパティ）
    val filterDialogState: FilterDialogState get() = uiStateManager.filterDialogState
    val countdownState: CountdownState get() = uiStateManager.countdownState
    val filterUpdateTrigger: Int get() = uiStateManager.filterUpdateTrigger
    val displayRouteDetails: SnapshotStateList<RouteDetail> = uiStateManager.displayRouteDetails
    
    // アクション委譲
    val filterDialogActions = uiStateManager.filterDialogActions
    val countdownActions = uiStateManager.countdownActions
    
    init {
        // 初期化時にカウントダウンアイテムを設定
        routeDataManager.updateCurrentCountItem(diagramTypeManager.getCurrentDiagramType())
    }
    
    // メソッドの委譲
    
    /**
     * 初期化
     */
    fun initializeAsync() {
        diagramTypeManager.initializeAsync()
    }
    
    /**
     * Jobのクリーンアップ
     */
    fun onCleared() {
        _job.cancel()
    }
    
    /**
     * 次の表示ダイアに設定
     */
    fun setNextDiagramType() {
        diagramTypeManager.setNextDiagramType()
        routeDataManager.clearDisplayCache()
        routeDataManager.updateCurrentCountItem(diagramTypeManager.getCurrentDiagramType())
    }
    
    /**
     * 表示キャッシュクリア
     */
    fun clearDisplayCache() {
        routeDataManager.clearDisplayCache()
    }
    
    /**
     * カウントダウン中アイテム設定
     */
    fun updateCurrentCountItem(useCache: Boolean = false) {
        routeDataManager.updateCurrentCountItem(diagramTypeManager.getCurrentDiagramType(), useCache)
    }
    
    /**
     * カウントダウン対象アイテムを手動で設定
     */
    fun setCurrentCountItem(routeDetail: RouteDetail) {
        routeDataManager.setCurrentCountItem(routeDetail)
    }
    
    /**
     * 表示用路線詳細アイテム取得
     */
    fun getDisplayRouteDetailItems(useCache: Boolean = false): List<RouteDetail> {
        return routeDataManager.getDisplayRouteDetailItems(diagramTypeManager.getCurrentDiagramType(), useCache)
    }
    
    /**
     * 次に表示するアイテムの現在時刻からの差分時間取得
     */
    fun getNextDiffTime(): Long {
        if (currentCountItem.value == null) {
            updateCurrentCountItem()
        }
        return countdownStateManager.getNextDiffTime(currentCountItem.value)
    }
    
    /**
     * フィルタ情報取得(同期)
     */
    fun getFilterInfoItemWithParentIdSync(): List<FilterInfo> {
        return routeDataManager.getFilterInfoItemWithParentIdSync()
    }
    
    /**
     * フィルタ情報更新
     */
    fun updateFilterInfoListItem(data: List<FilterInfo>) {
        routeDataManager.updateFilterInfoListItem(data)
    }
    
    /**
     * フィルタ更新トリガーをインクリメント
     */
    fun incrementFilterUpdateTrigger() {
        uiStateManager.incrementFilterUpdateTrigger()
    }
    
    /**
     * 表示用路線詳細リストを更新
     */
    fun updateDisplayRouteDetails(newDetails: List<RouteDetail>) {
        uiStateManager.updateDisplayRouteDetails(newDetails)
    }
    
    /**
     * 表示用路線詳細リストをクリア
     */
    fun clearDisplayRouteDetails() {
        uiStateManager.clearDisplayRouteDetails()
    }
}

/**
 * 改善されたカウントダウン効果
 * 無限ループを適切に管理し、メモリリークを防ぐ
 */
@Composable
fun CountdownEffect(
    currentCountItem: RouteDetail?,
    onCountdownUpdate: (String, String) -> Unit,
    getDiffTimeSeconds: () -> Long,
    formatCountdownTime: (Long) -> String,
    buildNextTimeInfo: (RouteDetail) -> String
) {
    LaunchedEffect(currentCountItem) {
        if (currentCountItem != null) {
            // アクティブな状態でのみタイマーを実行
            while (currentCountItem != null) {
                val diffSeconds = getDiffTimeSeconds()
                val countdownText = formatCountdownTime(diffSeconds)
                val nextTimeInfo = buildNextTimeInfo(currentCountItem)
                onCountdownUpdate(countdownText, nextTimeInfo)
                
                kotlinx.coroutines.delay(1000) // 1秒待機
            }
        } else {
            // アイテムがnullの場合はクリア
            onCountdownUpdate("--:--", "")
        }
    }
}

/**
 * 改善された状態同期効果
 * 条件付きで状態を同期する
 */
@Composable
fun StateSyncEffect(
    sourceList: List<RouteDetail>,
    isDragging: Boolean,
    onLocalListUpdate: (List<RouteDetail>) -> Unit
) {
    LaunchedEffect(sourceList) {
        if (!isDragging) {
            onLocalListUpdate(sourceList)
        }
    }
}

/**
 * RouteInfoScreenStateを作成するComposable関数
 */
@Composable
fun rememberRouteInfoScreenState(
    database: RouteDatabaseDao,
    application: Application,
    parentId: Long
): RouteInfoScreenState {
    return remember(parentId) { 
        RouteInfoScreenState(database, application, parentId) 
    }
}