package com.nyasai.traintimer.routelist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * 路線一覧Viewmodel
 */
class RouteListViewModel(
    val database: RouteDatabaseDao,
    application: Application
): AndroidViewModel(application),CoroutineScope {

    // 本VM用job
    private val _job = Job()
    // 本スコープ用のコンテキスト
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + _job
    
    // 路線一覧
    val routeList = database.getAllRouteListItems()

    // Yahoo路線情報取得用
    private val _yahooRouteInfoGetter = YahooRouteInfoGetter()

    /**
     * onClearedフック
     */
    override fun onCleared() {
        _yahooRouteInfoGetter.dispose()
        _job.cancel()
        super.onCleared()
    }

    /**
     * リストアイテム削除
     */
    fun deleteListItem(dataId: Long) {
        launch(coroutineContext) {
            database.deleteRouteListItem(dataId)
            database.deleteRouteDetailItemWithParentId(dataId)
            database.deleteFilterInfoItemWithParentId(dataId)
        }
    }

    /**
     * データ追加
     */
    fun insert(item: RouteListItem) {
        database.insertRouteListItem(item)
    }

    /**
     * 駅リスト取得
     */
    fun getStationList(stationName: String) = _yahooRouteInfoGetter.getStationList(stationName)

    /**
     * 行先取得(駅名)
     */
    fun getDestinationFromStationName(stationName: String) = _yahooRouteInfoGetter.getDestinationFromStationName(stationName)

    /**
     * 行先取得(URL)
     */
    fun getDestinationFromUrl(stationUrl: String) = _yahooRouteInfoGetter.getDestinationFromUrl(stationUrl)

    /**
     * 行先キー分割
     */
    fun splitDestinationKey(keyString: String) = _yahooRouteInfoGetter.splitDestinationKey(keyString)

    /**
     * 時刻表情報取得
     */
    suspend fun getTimeTableInfo(timeTableUrl: String) = _yahooRouteInfoGetter.getTimeTableInfo(timeTableUrl)

    /**
     * 路線リストアイテム登録
     * @param routeInfo 検索した路線情報
     * @return 登録したID
     */
    fun registRouteListItem(routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>, searchRouteListItem: RouteListItem): Long {
        Log.d("Debug", "データ登録開始")
        var parentDataId = -1L
        if(routeInfo.size == 3 && routeInfo[0].isNotEmpty() && routeInfo[1].isNotEmpty() && routeInfo[2].isNotEmpty()) {
            Log.d("Debug", "一覧データ登録")
            insert(searchRouteListItem)
            // 追加したアイテムのIDを取得
            for (item in getListItemsAsync()) {
                if(item.stationName == searchRouteListItem.stationName
                    && item.routeName == searchRouteListItem.routeName
                    && item.destination == searchRouteListItem.destination){
                    parentDataId = item.dataId
                    break
                }
            }
        }
        return parentDataId
    }

    /**
     * 時刻表情報
     * @param routeInfo 検索した路線情報
     * @param parentDataId 親データID
     */
    fun registRouteInfoDetailItems(routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>, parentDataId: Long) {
        Log.d("Debug", "詳細データ作成")
        if(parentDataId == -1L) {
            Log.d("Debug", "親データ未設定")
            return
        }
        if(routeInfo[0].isEmpty() || routeInfo[1].isEmpty() || routeInfo[2].isEmpty()) {
            Log.d("Debug", "データのいずれかが取得失敗")
            deleteListItem(parentDataId)
            return
        }

        val max = routeInfo.size - 1
        val addDataList = mutableListOf<RouteDetail>()
        val filterInfoList = mutableListOf<FilterInfo>()
        for (diagramType in 0..max) {
            // ダイヤ種別毎のアイテム
            for (timeInfo in routeInfo[diagramType]) {
                // 時刻情報追加
                addDataList.add(RouteDetail(
                    parentDataId = parentDataId,
                    diagramType = diagramType,
                    departureTime = timeInfo.time,
                    trainType = timeInfo.type,
                    destination = timeInfo.destination
                ))
                // フィルタ用情報生成
                filterInfoList.add(FilterInfo(
                    parentDataId = parentDataId,
                    trainTypeAndDestination = FilterInfo.createFilterKey(timeInfo.type, timeInfo.destination)
                ))
            }
        }

        Log.d("Debug", "詳細データ登録")
        insertRouteDetailItems(addDataList)
        // フィルタ情報から重複削除したデータを登録
        insertFilterInfoItems(filterInfoList.distinctBy{ it.trainTypeAndDestination })
    }

    /**
     * リストアイテム取得(同期)
     */
    private fun getListItemsAsync() = database.getDestAllRouteListItemsSync()

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