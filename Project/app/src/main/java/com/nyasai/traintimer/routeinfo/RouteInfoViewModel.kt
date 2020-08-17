package com.nyasai.traintimer.routeinfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetails
import com.nyasai.traintimer.define.Define
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

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
    var currentDiagramType = Define.DiagramType.Weekday

    // 表示中時刻データ種類

    /**
     * 表示用路線詳細アイテム取得
     */
    fun getDisplayRouteDetailsItems(): List<RouteDetails> {
        if(routeItems.value == null){
            return listOf()
        }
        else {
            val filter = routeItems.value?.filter {it ->
                it.diagramType == (currentDiagramType as Int)
            }
            return filter ?: listOf()
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clearAllRouteDetailsItem()
        }
    }

    private suspend fun update(item: RouteDetails) {
        withContext(Dispatchers.IO) {
            database.updateRouteDetailsItem(item)
        }
    }

    private suspend fun insert(item: RouteDetails) {
        withContext(Dispatchers.IO) {
            database.insertRouteDetailsItem(item)
        }
    }
}