package com.nyasai.traintimer.routeinfo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nyasai.traintimer.database.RouteDatabaseDao

/**
 * 路線詳細情報ViewModelファクトリ
 */
class RouteInfoViewModelFactory(
    private val dataSource: RouteDatabaseDao,
    private val application: Application,
    private val parentId: Long
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RouteInfoViewModel::class.java)) {
            return RouteInfoViewModel(dataSource, application, parentId) as T
        }
        throw IllegalArgumentException("不明なViewModelクラス")
    }
}