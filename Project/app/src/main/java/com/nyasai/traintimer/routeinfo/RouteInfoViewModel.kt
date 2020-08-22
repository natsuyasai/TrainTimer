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
    fun updateCurrentCountItem() {
        _currentCountItem.value = getNearTimeItem()
    }

    /**
     * 表示用路線詳細アイテム取得
     */
    fun getDisplayRouteDetailsItems(): List<RouteDetails> {
        return if(routeItems.value == null){
            listOf()
        } else {
            val filter = routeItems.value?.filter {it ->
                it.diagramType == currentDiagramType.value?.ordinal
            }
            // 時刻順ソート
            filter?.sortedWith(Comparator<RouteDetails>{ v1,v2 ->
                var diff = ChronoUnit.SECONDS.between(LocalTime.parse(v1.departureTime), LocalTime.parse(v2.departureTime))
                when {
                    diff < 0 -> 1
                    diff > 0 -> -1
                    else -> 0
                }
            }) ?: listOf()
        }
    }

    /**
     * 直近の時刻のアイテムを取得する
     */
    fun getNearTimeItem(): RouteDetails? {
        var now = LocalTime.now()
        for(item in getDisplayRouteDetailsItems()) {
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