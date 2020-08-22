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

    // 現在の表示ダイア種別
    private var _currentDiagramType: MutableLiveData<Define.DiagramType> = MutableLiveData()
    var currentDiagramType: LiveData<Define.DiagramType> = _currentDiagramType

    init {
        _currentDiagramType.value = Define.DiagramType.Weekday
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
            filter ?: listOf()
        }
    }

    fun getNearTimeItemId(): Long {
        return 0L
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