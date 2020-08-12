package com.nyasai.traintimer.routeinfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetails
import com.nyasai.traintimer.database.RouteListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class RouteInfoViewModel (val database: RouteDatabaseDao,
                          application: Application,
                          val parentId: Long): AndroidViewModel(application) {


    // ジョブ
    private var viewModelJob = Job()

    //
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // 路線詳細
    val routeItems = database.getRouteDetailsItemsWithParentId(parentId)


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