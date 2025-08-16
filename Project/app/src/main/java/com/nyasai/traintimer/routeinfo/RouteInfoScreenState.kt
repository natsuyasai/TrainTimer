package com.nyasai.traintimer.routeinfo

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.http.HttpClient
import com.nyasai.traintimer.routeinfo.logic.DiagramTypeModel
import com.nyasai.traintimer.routeinfo.logic.TimeComparisonUtils
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import kotlinx.coroutines.Job
import java.util.Calendar

/**
 * RouteInfoScreenの状態管理クラス
 * State Hoistingパターンに基づいて状態を分離
 */

/**
 * フィルタダイアログの状態
 */
@Stable
data class FilterDialogState(
    val showFilterDialog: Boolean = false,
    val localFilterItems: List<FilterInfo> = emptyList()
)

/**
 * カウントダウンタイマーの状態
 */
@Stable
data class CountdownState(
    val countdownText: String = "--:--",
    val nextTimeInfo: String = "",
    val isActive: Boolean = false
)

/**
 * フィルタダイアログ操作用のアクション
 */
interface FilterDialogActions {
    fun showFilterDialog(filterItems: List<FilterInfo>)
    fun hideFilterDialog()
    fun updateLocalFilterItems(items: List<FilterInfo>)
}

/**
 * カウントダウン操作用のアクション
 */
interface CountdownActions {
    fun updateCountdown(text: String, info: String)
    fun startCountdown()
    fun stopCountdown()
}

/**
 * RouteInfoScreen用の状態ホルダー
 * ViewModelのロジックを完全に統合したState Holder
 */
@Stable
class RouteInfoScreenState(
    private val database: RouteDatabaseDao,
    private val application: Application,
    private val parentId: Long
) {
    var filterDialogState by mutableStateOf(FilterDialogState())
        private set
    
    var countdownState by mutableStateOf(CountdownState())
        private set
    
    var filterUpdateTrigger by mutableStateOf(0)
        private set
    
    // SnapshotStateListを使用してCompose最適化
    val displayRouteDetails: SnapshotStateList<RouteDetail> = mutableStateListOf()
    
    // ViewModel から移行したプロパティ
    private val _job = Job()
    
    // 路線情報
    val routeInfo = database.getRouteListItemWithId(parentId)
    
    // 路線詳細
    val routeItems = database.getRouteDetailItemsWithParentId(parentId)
    
    // 現在カウント中のアイテム
    private var _currentCountItem: MutableLiveData<RouteDetail?> = MutableLiveData()
    var currentCountItem: LiveData<RouteDetail?> = _currentCountItem
    
    // 現在の表示ダイア種別
    private var _currentDiagramType: MutableLiveData<YahooRouteInfoGetter.Companion.DiagramType> =
        MutableLiveData()
    var currentDiagramType: LiveData<YahooRouteInfoGetter.Companion.DiagramType> =
        _currentDiagramType
    
    // フィルタ情報
    val filterInfo = database.getFilterInfoItemWithParentId(parentId)
    
    // 表示アイテムキャッシュ
    private var _displayRouteDetailItemCache: List<RouteDetail>? = null
    
    // 親データID
    private val _parentDataId: Long = parentId
    
    // ダイア種別用モデルクラス
    private val _diagramTypeModel: DiagramTypeModel =
        DiagramTypeModel(Calendar.getInstance(), HttpClient())
    
    init {
        _currentDiagramType.value = _diagramTypeModel.getTodayDiagramType(false)
        _currentCountItem.value = getNearTimeItem()
    }
    
    // フィルタダイアログアクション
    val filterDialogActions = object : FilterDialogActions {
        override fun showFilterDialog(filterItems: List<FilterInfo>) {
            filterDialogState = filterDialogState.copy(
                showFilterDialog = true,
                localFilterItems = filterItems.toList() // 不変コピーを作成
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
    
    // ViewModel から移行したメソッド群
    
    /**
     * 初期化
     * インスタンス生成以降に初期化したいものの初期化を行う
     */
    fun initializeAsync() {
        trySetPublicHolidayDiagramTypeAsync()
    }
    
    /**
     * Jobのクリーンアップ
     */
    fun onCleared() {
        _job.cancel()
    }
    
    /**
     * 祝日ダイア設定
     */
    private fun trySetPublicHolidayDiagramTypeAsync() {
        _currentDiagramType.postValue(_diagramTypeModel.getTodayDiagramType(true))
    }
    
    /**
     * 次の表示ダイアに設定
     */
    fun setNextDiagramType() {
        _currentDiagramType.value = _diagramTypeModel.getNextDiagramType(_currentDiagramType.value)
        // 表示キャッシュをクリア
        clearDisplayCache()
        // タイマ表示用に対象データを更新しておく
        updateCurrentCountItem()
    }
    
    /**
     * 表示キャッシュクリア
     */
    fun clearDisplayCache() {
        _displayRouteDetailItemCache = null
    }
    
    /**
     * カウントダウン中アイテム設定
     */
    fun updateCurrentCountItem(useCache: Boolean = false) {
        _currentCountItem.value = getNearTimeItem(useCache)
    }
    
    /**
     * カウントダウン対象アイテムを手動で設定
     * @param routeDetail 設定する路線詳細アイテム
     */
    fun setCurrentCountItem(routeDetail: RouteDetail) {
        _currentCountItem.value = routeDetail
    }
    
    /**
     * 表示用路線詳細アイテム取得
     */
    fun getDisplayRouteDetailItems(useCache: Boolean = false): List<RouteDetail> {
        if (shouldUseCachedItems(useCache)) {
            return _displayRouteDetailItemCache!!
        }
        
        val items = routeItems.value ?: return listOf()
        val filteredItems = filterRouteItems(items)
        val sortedItems = sortRouteItemsByTime(filteredItems)
        
        _displayRouteDetailItemCache = sortedItems
        return sortedItems
    }
    
    /**
     * キャッシュを使用するかどうかの判定
     */
    private fun shouldUseCachedItems(useCache: Boolean): Boolean {
        return useCache && _displayRouteDetailItemCache != null
    }
    
    /**
     * 路線アイテムのフィルタリング
     */
    private fun filterRouteItems(items: List<RouteDetail>): List<RouteDetail> {
        return items.filter { routeItem ->
            isCurrentDiagramType(routeItem) && isFilterEnabled(routeItem)
        }
    }
    
    /**
     * 現在のダイア種別かどうかの判定
     */
    private fun isCurrentDiagramType(routeItem: RouteDetail): Boolean {
        return routeItem.diagramType == currentDiagramType.value?.ordinal
    }
    
    /**
     * フィルタが有効かどうかの判定
     */
    private fun isFilterEnabled(routeItem: RouteDetail): Boolean {
        return filterInfo.value?.any { filterItem ->
            filterItem.trainTypeAndDestination == FilterInfo.createFilterKey(
                routeItem.trainType,
                routeItem.destination
            ) && filterItem.isShow
        } ?: true
    }
    
    /**
     * 時刻順ソート
     */
    private fun sortRouteItemsByTime(items: List<RouteDetail>): List<RouteDetail> {
        return items.sortedWith { v1, v2 ->
            compareRouteItemsByTime(v1, v2)
        }
    }
    
    /**
     * 路線アイテムの時刻比較
     */
    private fun compareRouteItemsByTime(item1: RouteDetail, item2: RouteDetail): Int {
        val correctedTime1 = correctDepartureTimeForSort(item1.departureTime)
        val correctedTime2 = correctDepartureTimeForSort(item2.departureTime)
        
        val hourDiff = correctedTime1.first - correctedTime2.first
        return if (hourDiff != 0) hourDiff else correctedTime1.second - correctedTime2.second
    }
    
    /**
     * 次に表示するアイテムの現在時刻からの差分時間取得
     * 深夜0時～3時は24時～27時として扱う
     */
    fun getNextDiffTime(): Long {
        // 画面に一番近いデータへの残り時間を設定する(1秒毎)
        if (currentCountItem.value == null) {
            updateCurrentCountItem()
        }
        // データが取得できなければ，ハイフン表示とするために-1を設定
        return currentCountItem.value?.let { countItem ->
            TimeComparisonUtils.getTimeDifferenceInSeconds(countItem)
        } ?: -1L
    }
    
    /**
     * ソート用に時刻情報を補正
     * @param departureTime 補正前文字列
     * @return 補正後文字列
     */
    private fun correctDepartureTimeForSort(departureTime: String): Pair<Int, Int> {
        val hour = Integer.parseInt(departureTime.substring(0, 2))
        val minutes = Integer.parseInt(departureTime.substring(3))
        // 一番遅い終電が2時前かつ一番早い始発が4時台のため，間の3時を区切りとする
        if (hour in 0..3) {
            // 0時～3時は24時間表記の24時～27時に変換する
            return Pair(hour + 24, minutes)
        }
        return Pair(hour, minutes)
    }
    
    /**
     * 直近の時刻のアイテムを取得する
     * 深夜0時～3時は24時～27時として扱う
     * 現在時刻より先のアイテムが見つからない場合は一番先頭の要素を返す
     */
    private fun getNearTimeItem(useCache: Boolean = false): RouteDetail? {
        val displayItems = getDisplayRouteDetailItems(useCache)
        return TimeComparisonUtils.findNextTrain(displayItems)
    }
    
    /**
     * フィルタ情報取得(同期)
     */
    fun getFilterInfoItemWithParentIdSync(): List<FilterInfo> {
        return database.getFilterInfoItemWithParentIdSync(_parentDataId)
    }
    
    /**
     * フィルタ情報更新
     */
    fun updateFilterInfoListItem(data: List<FilterInfo>) {
        database.updateFilterInfoListItem(data)
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