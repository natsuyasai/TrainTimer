package com.nyasai.traintimer.routelist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * 路線一覧Viewmodel
 */
class RouteListViewModel(
    val database: RouteDatabaseDao,
    application: Application): AndroidViewModel(application) {

    // 本VM用job
    private val _job = Job()
    // 本スコープ用のコンテキスト
    private val _ioContext: CoroutineContext
        get() = _ioContext + _job
    
    // 路線一覧
    val routeList = database.getAllRouteListItems()

    /**
     * onClearedフック
     */
    override fun onCleared() {
        _job.cancel()
        super.onCleared()
    }

    /**
     * リストアイテム取得(同期)
     */
    suspend fun getListItemsAsync(): List<RouteListItem> {
        return withContext(_ioContext) {
            database.getDestAllRouteListItemsSync()
        }
    }

    /**
     * リストアイテム削除
     */
    suspend fun deleteListItem(dataId: Long) {
        withContext(_ioContext) {
            database.deleteRouteListItem(dataId)
            database.deleteRouteDetailItemWithParentId(dataId)
            database.deleteFilterInfoItemWithParentId(dataId)
        }
    }

    /**
     * 路線詳細情報追加
     */
    suspend fun insertRouteDetailItems(datum: List<RouteDetail>) {
        withContext(_ioContext) {
            database.insertRouteDetailItems(datum)
        }
    }

    /**
     * フィルタ情報追加
     */
    suspend fun insertFilterInfoItems(data: List<FilterInfo>) {
        withContext(_ioContext) {
            database.insertFilterInfoItems(data)
        }
    }

    /**
     * データクリア
     */
    private suspend fun clear() {
        withContext(_ioContext) {
            database.clearAllRouteListItem()
        }
    }

    /**
     * データ更新
     */
    private suspend fun update(item: RouteListItem) {
        withContext(_ioContext) {
            database.updateRouteListItem(item)
        }
    }

    /**
     * データ追加
     */
    suspend fun insert(item: RouteListItem) {
        withContext(_ioContext) {
            database.insertRouteListItem(item)
        }
    }
}