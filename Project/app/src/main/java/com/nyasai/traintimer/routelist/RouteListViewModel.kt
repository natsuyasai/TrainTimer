package com.nyasai.traintimer.routelist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

/**
 * 路線一覧Viewmodel
 */
class RouteListViewModel(
    val database: RouteDatabaseDao,
    application: Application): AndroidViewModel(application) {

    // ジョブ
    private var viewModelJob = Job()

    //
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // 路線一覧
    val routeList = database.getAllRouteListItems()

    /**
     * データクリア
     */
    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clearAllRouteListItem()
        }
    }

    /**
     * データ更新
     */
    private suspend fun update(item: RouteListItem) {
        withContext(Dispatchers.IO) {
            database.updateRouteListItem(item)
        }
    }

    /**
     * データ追加
     */
    private suspend fun insert(item: RouteListItem) {
        withContext(Dispatchers.IO) {
            database.insertRouteListItem(item)
        }
    }
}