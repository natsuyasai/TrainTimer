package com.nyasai.traintimer.routelist.state

import androidx.compose.runtime.Stable
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.util.YahooRouteInfoGetter

/**
 * 路線情報の更新処理を担当するクラス
 */
@Stable
class RouteUpdateManager(
    private val routeSearchManager: RouteSearchManager,
    private val routeListDataManager: RouteListDataManager
) {
    
    /**
     * 路線情報更新
     * @param item 更新対象アイテム
     * @param notifyMaxCountCallback 最大カウント値通知コールバック関数
     * @param notifyCountCallback カウント通知コールバック関数
     * @return 処理結果
     */
    suspend fun updateRouteInfo(
        item: RouteListItem,
        notifyMaxCountCallback: ((Int) -> Unit),
        notifyCountCallback: (() -> Unit)
    ): Boolean {
        val destinationListMap = getDestinationListForItem(item) ?: return false
        val destinationKey = createDestinationKey(item)
        
        if (!destinationListMap.containsKey(destinationKey)) {
            return false
        }
        
        val routeInfo = fetchRouteInfo(destinationListMap, destinationKey, notifyMaxCountCallback, notifyCountCallback)
        if (!isValidRouteInfo(routeInfo)) {
            return false
        }
        
        routeListDataManager.updateRouteData(item, routeInfo)
        return true
    }
    
    /**
     * アイテムに対応する目的地リストマップを取得
     */
    private fun getDestinationListForItem(item: RouteListItem): Map<String, String>? {
        val stationListMap = routeSearchManager.getStationList(item.stationName)
        
        return if (stationListMap?.isNotEmpty() == true && stationListMap.containsKey(item.stationName)) {
            routeSearchManager.getDestinationFromUrl(stationListMap.getValue(item.stationName))
        } else {
            routeSearchManager.getDestinationFromStationName(item.stationName)
        }
    }
    
    /**
     * 目的地キーを作成
     */
    private fun createDestinationKey(item: RouteListItem): String {
        return item.routeName + YahooRouteInfoGetter.KeyDelimiterSir + item.destination
    }
    
    /**
     * 路線情報を取得
     */
    private suspend fun fetchRouteInfo(
        destinationListMap: Map<String, String>,
        destinationKey: String,
        notifyMaxCountCallback: ((Int) -> Unit),
        notifyCountCallback: (() -> Unit)
    ): List<List<YahooRouteInfoGetter.TimeInfo>> {
        return routeSearchManager.getTimeTableInfo(
            destinationListMap.getValue(destinationKey),
            notifyMaxCountCallback,
            notifyCountCallback
        )
    }
    
    /**
     * 路線情報が有効かチェック
     */
    private fun isValidRouteInfo(routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>): Boolean {
        return routeInfo.isNotEmpty() &&
                routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Weekday.ordinal].isNotEmpty() &&
                routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Saturday.ordinal].isNotEmpty() &&
                routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Holiday.ordinal].isNotEmpty()
    }
}