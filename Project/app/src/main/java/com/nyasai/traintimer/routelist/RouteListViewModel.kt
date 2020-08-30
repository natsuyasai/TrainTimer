package com.nyasai.traintimer.routelist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetails
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
     * リストアイテム取得(同期)
     */
     fun getListItemsAsync(): List<RouteListItem> {
        return database.getDestAllRouteListItemsSync()
    }

    /**
     * リストアイテム削除
     */
    suspend fun deleteListItem(dataId: Long) {
        withContext(Dispatchers.IO) {
            database.deleteRouteListItem(dataId)
            database.deleteRouteDetailsItemWithParentId(dataId)
            database.deleteFilterInfoItemWithParentId(dataId)
        }
    }

    /**
     * 路線詳細情報追加
     */
    suspend fun insertRouteDetailItems(data: List<RouteDetails>) {
        withContext(Dispatchers.IO) {
            database.insertRouteDetailsItems(data)
        }
    }

    /**
     * フィルタ情報追加
     */
    suspend fun insertFilterInfoItems(data: List<FilterInfo>) {
        withContext(Dispatchers.IO) {
            database.insertFilterInfoItems(data)
        }
    }

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
    suspend fun insert(item: RouteListItem) {
        withContext(Dispatchers.IO) {
            database.insertRouteListItem(item)
        }
    }
}