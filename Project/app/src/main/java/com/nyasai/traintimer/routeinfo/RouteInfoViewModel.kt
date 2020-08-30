package com.nyasai.traintimer.routeinfo

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetails
import com.nyasai.traintimer.define.Define
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * 路線詳細情報表示用ViewModel
 */
class RouteInfoViewModel (val database: RouteDatabaseDao,
                          application: Application,
                          parentId: Long): AndroidViewModel(application) {


    // ジョブ
    private var viewModelJob = Job()

    //
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // 路線情報
    val routeInfo = database.getRouteListItemWithId(parentId)

    // 路線詳細
    val routeItems = database.getRouteDetailsItemsWithParentId(parentId)

    // 現在カウント中のアイテム
    private var _currentCountItem: MutableLiveData<RouteDetails> = MutableLiveData()
    var currentCountItem: LiveData<RouteDetails> = _currentCountItem

    // 現在の表示ダイア種別
    private var _currentDiagramType: MutableLiveData<Define.DiagramType> = MutableLiveData()
    var currentDiagramType: LiveData<Define.DiagramType> = _currentDiagramType

    // 表示アイテムキャッシュ
    private var _displayRouteDetailsItemCache: List<RouteDetails>? = null

    init {
        _currentDiagramType.value = when(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
            1 -> Define.DiagramType.Sunday
            7 -> Define.DiagramType.Saturday
            else -> Define.DiagramType.Weekday
        }
        _currentCountItem.value = getNearTimeItem()
    }

    /**
     * 表示ダイア更新
     */
    fun setCurrentDiagramType(type: Define.DiagramType) {
        _currentDiagramType.value = type
    }

    /**
     * 次の表示ダイアに設定
     */
    fun setNextDiagramType() {
        _currentDiagramType.value = when(_currentDiagramType.value)
        {
            Define.DiagramType.Weekday -> Define.DiagramType.Saturday
            Define.DiagramType.Saturday -> Define.DiagramType.Sunday
            Define.DiagramType.Sunday -> Define.DiagramType.Weekday
            else -> Define.DiagramType.Weekday
        }
        // タイマ表示用に対象データを更新しておく
        updateCurrentCountItem()
    }

    /**
     * カウントダウン中アイテム設定
     */
    fun setCurrentCountItem(item: RouteDetails?) {
        _currentCountItem.value = item
    }
    fun updateCurrentCountItem(useCache: Boolean = false) {
        _currentCountItem.value = getNearTimeItem(useCache)
    }

    /**
     * 表示用路線詳細アイテム取得
     */
    fun getDisplayRouteDetailsItems(useCache: Boolean = false): List<RouteDetails> {
        if(useCache && _displayRouteDetailsItemCache != null) {
            return _displayRouteDetailsItemCache!!
        }
        return if(routeItems.value == null){
            listOf()
        } else {
            val filter = routeItems.value?.filter {it ->
                it.diagramType == currentDiagramType.value?.ordinal
            }
            // 時刻順ソート
            _displayRouteDetailsItemCache = filter?.sortedWith { v1, v2 ->
                val correctedV1 = correctDepartureTimeForSort(v1.departureTime)
                val correctedV2 = correctDepartureTimeForSort(v2.departureTime)
                val diffHour = correctedV1.first - correctedV2.first
                if(diffHour != 0){
                    diffHour
                }
                else {
                    correctedV1.second - correctedV2.second
                }
            }
            _displayRouteDetailsItemCache ?: listOf()
        }
    }

    /**
     * ソート用に時刻情報を補正
     * @param departureTime 補正前文字列
     * @return 補正後文字列
     */
    private fun correctDepartureTimeForSort(departureTime: String): Pair<Int,Int> {
        val hour = Integer.parseInt(departureTime.substring(0,2))
        val minutes = Integer.parseInt(departureTime.substring(3))
        // 一番遅い終電が2時前かつ一番早い始発が4時台のため，間の3時を区切りとする
        if(hour in 0..3) {
            // 0時～3時は24時間表記の24時～27時に変換する
            return Pair(hour + 24, minutes)
        }
        return Pair(hour, minutes)
    }

    /**
     * 直近の時刻のアイテムを取得する
     */
    fun getNearTimeItem(useCache: Boolean = false): RouteDetails? {
        val now = LocalTime.now()
        for(item in getDisplayRouteDetailsItems(useCache)) {
            if(LocalTime.parse(item.departureTime) > now){
                return item
            }
        }
        return null
    }

    /**
     * データクリア
     */
    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clearAllRouteDetailsItem()
        }
    }

    /**
     * データ更新
     */
    private suspend fun update(item: RouteDetails) {
        withContext(Dispatchers.IO) {
            database.updateRouteDetailsItem(item)
        }
    }

    /**
     * データ追加
     */
    private suspend fun insert(item: RouteDetails) {
        withContext(Dispatchers.IO) {
            database.insertRouteDetailsItem(item)
        }
    }
}