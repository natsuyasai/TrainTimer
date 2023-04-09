package com.nyasai.traintimer.routelist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * 路線一覧ViewModel
 */
class RouteListViewModel(
    val database: RouteDatabaseDao,
    application: Application
) : AndroidViewModel(application), CoroutineScope {

    // 本VM用job
    private val _job = Job()

    // 本スコープ用のコンテキスト
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + _job

    // 路線一覧
    val routeList = database.getAllRouteListItems()

    // 手動ソートモード中か
    private var _isManualSortMode: MutableLiveData<Boolean> = MutableLiveData()
    var isManualSortMode: LiveData<Boolean> = _isManualSortMode

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
    private fun insert(item: RouteListItem) {
        database.insertRouteListItem(item)
    }

    /**
     * 駅リスト取得
     */
    fun getStationList(stationName: String) = _yahooRouteInfoGetter.getStationList(stationName)

    /**
     * 行先取得(駅名)
     */
    fun getDestinationFromStationName(stationName: String) =
        _yahooRouteInfoGetter.getDestinationFromStationName(stationName)

    /**
     * 行先取得(URL)
     */
    fun getDestinationFromUrl(stationUrl: String) =
        _yahooRouteInfoGetter.getDestinationFromUrl(stationUrl)

    /**
     * 行先キー分割
     */
    fun splitDestinationKey(keyString: String) =
        _yahooRouteInfoGetter.splitDestinationKey(keyString)

    /**
     * 時刻表情報取得
     * @param timeTableUrl 時刻表情報ページURL
     * @param notifyMaxCountCallback 最大カウント値通知コールバック関数
     * @param notifyCountCallback カウント通知コールバック関数
     */
    suspend fun getTimeTableInfo(
        timeTableUrl: String,
        notifyMaxCountCallback: ((Int) -> Unit),
        notifyCountCallback: (() -> Unit)
    ) = _yahooRouteInfoGetter.getTimeTableInfo(
        timeTableUrl,
        notifyMaxCountCallback,
        notifyCountCallback
    )

    /**
     * 路線リストアイテム登録
     * @param routeInfo 検索した路線情報
     * @return 登録したID
     */
    fun registerRouteListItem(
        routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>,
        searchRouteListItem: RouteListItem
    ): Long {
        Log.d("Debug", "データ登録開始")
        var parentDataId = -1L
        if (routeInfo.size != YahooRouteInfoGetter.Companion.DiagramType.Max.ordinal) {
            return parentDataId
        }
        if (routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Weekday.ordinal].isEmpty()) {
            return parentDataId
        }
        if (routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Saturday.ordinal].isEmpty()) {
            return parentDataId
        }
        if (routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Holiday.ordinal].isEmpty()) {
            return parentDataId
        }
        Log.d("Debug", "一覧データ登録")
        insert(searchRouteListItem)
        // 追加したアイテムのIDを取得
        for (item in getListItemsAsync()) {
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
     * 時刻表情報
     * @param routeInfo 検索した路線情報
     * @param parentDataId 親データID
     */
    fun registerRouteInfoDetailItems(
        routeInfo: List<List<YahooRouteInfoGetter.TimeInfo>>,
        parentDataId: Long
    ) {
        Log.d("Debug", "詳細データ作成")
        if (parentDataId == -1L) {
            Log.d("Debug", "親データ未設定")
            return
        }
        if (routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Weekday.ordinal].isEmpty()
            || routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Saturday.ordinal].isEmpty()
            || routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Holiday.ordinal].isEmpty()
        ) {
            Log.d("Debug", "データのいずれかが取得失敗")
            deleteListItem(parentDataId)
            return
        }
        val registerItem = createRegisterRouteInfoDetailItemsAndFilterInfo(routeInfo, parentDataId)
        Log.d("Debug", "詳細データ登録")
        insertRouteDetailItems(registerItem.first)
        // フィルタ情報から重複削除したデータを登録
        insertFilterInfoItems(registerItem.second.distinctBy { it.trainTypeAndDestination })
    }

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
        // 路線アイテム情報に一致する情報を取得する
        val stationListMap = _yahooRouteInfoGetter.getStationList(item.stationName)
        val destinationListMap: Map<String, String> = if (stationListMap?.isNotEmpty() == true) {
            if (stationListMap.containsKey(item.stationName)) {
                _yahooRouteInfoGetter.getDestinationFromUrl(stationListMap.getValue(item.stationName))
            } else {
                mapOf()
            }
        } else {
            _yahooRouteInfoGetter.getDestinationFromStationName(item.stationName)
        }
        val destinationKey =
            item.routeName + YahooRouteInfoGetter.KeyDelimiterSir + item.destination
        if (!destinationListMap.containsKey(destinationKey)) {
            // キーが見つからない
            return false
        }
        val routeInfo =
            _yahooRouteInfoGetter.getTimeTableInfo(
                destinationListMap.getValue(destinationKey),
                notifyMaxCountCallback,
                notifyCountCallback
            )
        if (routeInfo.isEmpty()
            || (routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Weekday.ordinal].isEmpty()
                    || routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Saturday.ordinal].isEmpty()
                    || routeInfo[YahooRouteInfoGetter.Companion.DiagramType.Holiday.ordinal].isEmpty())
        ) {
            return false
        }
        // 既にある路線アイテムを全消去
        database.deleteRouteDetailItemWithParentId(item.dataId)
        // 登録
        val registerItem = createRegisterRouteInfoDetailItemsAndFilterInfo(routeInfo, item.dataId)
        insertRouteDetailItems(registerItem.first)
        database.updateFilterInfoListItem(registerItem.second.distinctBy { it.trainTypeAndDestination })

        return true
    }

    /**
     * ソート情報更新
     */
    fun updateSortIndex() {
        if (routeList.value == null) {
            return
        }
        // TODO: 処理は要見直しのこと
        for ((index, item) in routeList.value!!.withIndex()) {
            item.sortIndex = index.toLong()
            database.updateRouteListItem(item)
        }
    }

    fun switchManualSortMode() {
        _isManualSortMode.value = !(_isManualSortMode.value ?: false)
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
            // ダイヤ種別毎のアイテム
            for (timeInfo in diagramType) {
                // 時刻情報追加
                addDataList.add(
                    RouteDetail(
                        parentDataId = parentDataId,
                        diagramType = index,
                        departureTime = timeInfo.time,
                        trainType = timeInfo.type,
                        destination = timeInfo.destination
                    )
                )
                // フィルタ用情報生成
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