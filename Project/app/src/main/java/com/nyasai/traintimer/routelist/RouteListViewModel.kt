package com.nyasai.traintimer.routelist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import kotlinx.coroutines.*
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
    fun getListItemsAsync() = database.getDestAllRouteListItemsSync()

    /**
     * リストアイテム削除
     */
    fun deleteListItem(dataId: Long) {
        database.deleteRouteListItem(dataId)
        database.deleteRouteDetailItemWithParentId(dataId)
        database.deleteFilterInfoItemWithParentId(dataId)
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
}