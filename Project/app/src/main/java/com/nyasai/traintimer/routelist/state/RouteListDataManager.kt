package com.nyasai.traintimer.routelist.state

import androidx.compose.runtime.Stable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import kotlinx.coroutines.*

/**
 * 路線リストデータとCRUD操作を管理するクラス
 */
@Stable
class RouteListDataManager(
    private val database: RouteDatabaseDao,
    private val coroutineScope: CoroutineScope
) {
    // 路線一覧
    val routeList = database.getAllRouteListItems()
    
    // 手動ソートモード中か
    private var _isEditMode: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEditMode: LiveData<Boolean> = _isEditMode
    
    /**
     * リストアイテム削除
     */
    fun deleteListItem(dataId: Long) {
        coroutineScope.launch {
            database.deleteRouteListItem(dataId)
            database.deleteRouteDetailItemWithParentId(dataId)
            database.deleteFilterInfoItemWithParentId(dataId)
        }
    }
    
    /**
     * データ追加
     */
    fun insertRouteListItem(item: RouteListItem) {
        val maxIndex = database.getMaxSortIndex()
        item.sortIndex = maxIndex + 1
        database.insertRouteListItem(item)
    }
    
    /**
     * 路線リストアイテム登録
     * @param routeInfo 検索した路線情報
     * @return 登録したID
     */
    fun registerRouteListItem(
        routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>,
        searchRouteListItem: RouteListItem
    ): Long {
        var parentDataId = -1L
        if (!isValidRouteInfoStructure(routeInfo)) {
            return parentDataId
        }
        
        insertRouteListItem(searchRouteListItem)
        
        // 追加したアイテムのIDを取得
        for (item in getListItemsSync()) {
            if (item.stationName == searchRouteListItem.stationName
                && item.routeName == searchRouteListItem.routeName
                && item.destination == searchRouteListItem.destination
            ) {
                parentDataId = item.dataId
                break
            }
        }
        return parentDataId
    }
    
    /**
     * 時刻表情報登録
     */
    fun registerRouteInfoDetailItems(
        routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>,
        parentDataId: Long
    ) {
        if (parentDataId == -1L) {
            return
        }
        
        if (!isValidRouteInfoData(routeInfo)) {
            deleteListItem(parentDataId)
            return
        }
        
        val registerItem = createRegisterRouteInfoDetailItemsAndFilterInfo(routeInfo, parentDataId)
        insertRouteDetailItems(registerItem.first)
        insertFilterInfoItems(registerItem.second.distinctBy { it.trainTypeAndDestination })
    }
    
    /**
     * 路線アイテムの色を更新
     */
    fun updateRouteListItemColor(dataId: Long, color: Int?) {
        // 即座にメモリ内のアイテムを更新
        routeList.value?.find { it.dataId == dataId }?.displayColor = color
        
        // データベースも更新
        coroutineScope.launch {
            database.updateRouteListItemColor(dataId, color)
        }
    }
    
    /**
     * 編集モード切り替え
     */
    fun switchEditMode() {
        val current = (isEditMode.value ?: true)
        _isEditMode.value = !current
    }
    
    /**
     * 路線データを更新
     */
    fun updateRouteData(item: RouteListItem, routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>) {
        database.deleteRouteDetailItemWithParentId(item.dataId)
        val registerItem = createRegisterRouteInfoDetailItemsAndFilterInfo(routeInfo, item.dataId)
        insertRouteDetailItems(registerItem.first)
        database.updateFilterInfoListItem(registerItem.second.distinctBy { it.trainTypeAndDestination })
    }
    
    /**
     * 路線情報構造が有効かチェック
     */
    private fun isValidRouteInfoStructure(routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>): Boolean {
        return routeInfo.size == YahooRouteInfoGetter.Companion.DiagramType.Max.ordinal &&
                routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Weekday.ordinal].isNotEmpty() &&
                routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Saturday.ordinal].isNotEmpty() &&
                routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Holiday.ordinal].isNotEmpty()
    }
    
    /**
     * 路線情報データが有効かチェック
     */
    private fun isValidRouteInfoData(routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>): Boolean {
        return routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Weekday.ordinal].isNotEmpty() &&
                routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Saturday.ordinal].isNotEmpty() &&
                routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Holiday.ordinal].isNotEmpty()
    }
    
    /**
     * 登録する路線情報詳細とフィルタ情報を生成
     */
    private fun createRegisterRouteInfoDetailItemsAndFilterInfo(
        routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>,
        parentDataId: Long
    ): Pair<List<RouteDetail>, List<FilterInfo>> {
        val addDataList = mutableListOf<RouteDetail>()
        val filterInfoList = mutableListOf<FilterInfo>()
        
        for ((index, diagramType) in routeInfo.withIndex()) {
            for (timeInfo in diagramType) {
                addDataList.add(
                    RouteDetail(
                        parentDataId = parentDataId,
                        diagramType = index,
                        departureTime = timeInfo.time,
                        trainType = timeInfo.type,
                        destination = timeInfo.destination
                    )
                )
                
                filterInfoList.add(
                    FilterInfo(
                        parentDataId = parentDataId,
                        trainTypeAndDestination = FilterInfo.createFilterKey(
                            timeInfo.type,
                            timeInfo.destination
                        )
                    )
                )
            }
        }
        return Pair(addDataList, filterInfoList)
    }
    
    /**
     * リストアイテム取得(同期)
     */
    private fun getListItemsSync() = database.getDestAllRouteListItemsSync()
    
    /**
     * 路線詳細情報追加
     */
    private fun insertRouteDetailItems(datum: List<RouteDetail>) {
        database.insertRouteDetailItems(datum)
    }
    
    /**
     * フィルタ情報追加
     */
    private fun insertFilterInfoItems(data: List<FilterInfo>) {
        database.insertFilterInfoItems(data)
    }
}