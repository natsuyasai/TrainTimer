package com.nyasai.traintimer.routelist

import android.app.Application
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
     * リストアイテム取得(同期)
     */
    fun getListItemsAsync() = database.getDestAllRouteListItemsSync()

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
     * 路線詳細情報追加
     */
    fun insertRouteDetailItems(datum: List<RouteDetail>) {
        database.insertRouteDetailItems(datum)
    }

    /**
     * フィルタ情報追加
     */
    fun insertFilterInfoItems(data: List<FilterInfo>) {
        database.insertFilterInfoItems(data)
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

}