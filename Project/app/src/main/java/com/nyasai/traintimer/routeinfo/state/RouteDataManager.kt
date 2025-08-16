package com.nyasai.traintimer.routeinfo.state

import androidx.compose.runtime.Stable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routeinfo.logic.TimeComparisonUtils
import com.nyasai.traintimer.util.YahooRouteInfoGetter

/**
 * 路線データとフィルタリング処理を管理するクラス
 */
@Stable
class RouteDataManager(
    private val database: RouteDatabaseDao,
    private val parentId: Long
) {
    // 路線情報
    val routeInfo: LiveData<RouteListItem?> = database.getRouteListItemWithId(parentId) as LiveData<RouteListItem?>
    
    // 路線詳細
    val routeItems: LiveData<List<RouteDetail>> = database.getRouteDetailItemsWithParentId(parentId)
    
    // フィルタ情報
    val filterInfo: LiveData<List<FilterInfo>> = database.getFilterInfoItemWithParentId(parentId)
    
    // 現在カウント中のアイテム
    private var _currentCountItem: MutableLiveData<RouteDetail?> = MutableLiveData()
    val currentCountItem: LiveData<RouteDetail?> = _currentCountItem
    
    // 表示アイテムキャッシュ
    private var _displayRouteDetailItemCache: List<RouteDetail>? = null
    
    /**
     * カウントダウン対象アイテムを手動で設定
     */
    fun setCurrentCountItem(routeDetail: RouteDetail) {
        _currentCountItem.value = routeDetail
    }
    
    /**
     * カウントダウン中アイテム設定
     */
    fun updateCurrentCountItem(
        currentDiagramType: YahooRouteInfoGetter.Companion.DiagramType?,
        useCache: Boolean = false
    ) {
        _currentCountItem.value = getNearTimeItem(currentDiagramType, useCache)
    }
    
    /**
     * 表示キャッシュクリア
     */
    fun clearDisplayCache() {
        _displayRouteDetailItemCache = null
    }
    
    /**
     * 表示用路線詳細アイテム取得
     */
    fun getDisplayRouteDetailItems(
        currentDiagramType: YahooRouteInfoGetter.Companion.DiagramType?,
        useCache: Boolean = false
    ): List<RouteDetail> {
        if (shouldUseCachedItems(useCache)) {
            return _displayRouteDetailItemCache!!
        }
        
        val items = routeItems.value ?: return listOf()
        val filteredItems = filterRouteItems(items, currentDiagramType)
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
    private fun filterRouteItems(
        items: List<RouteDetail>,
        currentDiagramType: YahooRouteInfoGetter.Companion.DiagramType?
    ): List<RouteDetail> {
        return items.filter { routeItem ->
            isCurrentDiagramType(routeItem, currentDiagramType) && isFilterEnabled(routeItem)
        }
    }
    
    /**
     * 現在のダイア種別かどうかの判定
     */
    private fun isCurrentDiagramType(
        routeItem: RouteDetail,
        currentDiagramType: YahooRouteInfoGetter.Companion.DiagramType?
    ): Boolean {
        return routeItem.diagramType == currentDiagramType?.ordinal
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
     * ソート用に時刻情報を補正
     */
    private fun correctDepartureTimeForSort(departureTime: String): Pair<Int, Int> {
        val hour = Integer.parseInt(departureTime.substring(0, 2))
        val minutes = Integer.parseInt(departureTime.substring(3))
        if (hour in 0..3) {
            return Pair(hour + 24, minutes)
        }
        return Pair(hour, minutes)
    }
    
    /**
     * 直近の時刻のアイテムを取得する
     */
    private fun getNearTimeItem(
        currentDiagramType: YahooRouteInfoGetter.Companion.DiagramType?,
        useCache: Boolean = false
    ): RouteDetail? {
        val displayItems = getDisplayRouteDetailItems(currentDiagramType, useCache)
        return TimeComparisonUtils.findNextTrain(displayItems)
    }
    
    /**
     * フィルタ情報取得(同期)
     */
    fun getFilterInfoItemWithParentIdSync(): List<FilterInfo> {
        return database.getFilterInfoItemWithParentIdSync(parentId)
    }
    
    /**
     * フィルタ情報更新
     */
    fun updateFilterInfoListItem(data: List<FilterInfo>) {
        database.updateFilterInfoListItem(data)
    }
}