package com.nyasai.traintimer.routeinfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.http.HttpClient
import com.nyasai.traintimer.routeinfo.logic.DiagramTypeModel
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import kotlinx.coroutines.Job
import java.time.LocalTime
import java.util.Calendar

/**
 * 路線詳細情報表示用ViewModel
 */
class RouteInfoViewModel(
    val database: RouteDatabaseDao,
    application: Application,
    parentId: Long
) : AndroidViewModel(application) {

    // 本VM用job
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

    /**
     * 初期化
     * インスタンス生成以降に初期化したいものの初期化を行う
     */
    fun initializeAsync() {
        trySetPublicHolidayDiagramTypeAsync()
    }

    /**
     * onClearedフック
     */
    override fun onCleared() {
        _job.cancel()
        super.onCleared()
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
        return when {
            currentCountItem.value != null -> {
                try {
                    val now = LocalTime.now()
                    val trainTime = LocalTime.parse(currentCountItem.value?.departureTime)
                    
                    // 分単位で時刻を計算（深夜0時～3時59分は24時～27時59分として扱う）
                    val nowMinutes = if (now.hour < 4) {
                        (now.hour + 24) * 60 + now.minute
                    } else {
                        now.hour * 60 + now.minute
                    }
                    
                    val trainMinutes = if (trainTime.hour < 4) {
                        (trainTime.hour + 24) * 60 + trainTime.minute
                    } else {
                        trainTime.hour * 60 + trainTime.minute
                    }
                    
                    // 秒も考慮した差分計算
                    val diffMinutes = trainMinutes - nowMinutes
                    val diffSeconds = diffMinutes * 60 - now.second + trainTime.second
                    
                    diffSeconds.toLong()
                } catch (e: Exception) {
                    -1L
                }
            }
            else -> -1L
        }
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
     */
    private fun getNearTimeItem(useCache: Boolean = false): RouteDetail? {
        val now = LocalTime.now()
        
        // 現在時刻を分単位で計算（深夜0時～3時59分は24時～27時59分として扱う）
        val nowMinutes = if (now.hour < 4) {
            (now.hour + 24) * 60 + now.minute
        } else {
            now.hour * 60 + now.minute
        }
        
        for (item in getDisplayRouteDetailItems(useCache)) {
            try {
                val trainTime = LocalTime.parse(item.departureTime)
                val trainMinutes = if (trainTime.hour < 4) {
                    (trainTime.hour + 24) * 60 + trainTime.minute
                } else {
                    trainTime.hour * 60 + trainTime.minute
                }
                
                if (trainMinutes > nowMinutes) {
                    return item
                }
            } catch (e: Exception) {
                // 時刻解析エラーの場合はスキップ
                continue
            }
        }
        return null
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