package com.nyasai.traintimer.routelist

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nyasai.traintimer.database.RouteDatabaseDao
import java.lang.IllegalArgumentException

/**
 * 路線一覧ViewModelファクトリ
 */
class RouteListViewModelFactory(
    private val dataSource: RouteDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RouteListViewModel::class.java)) {
            return RouteListViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("不明なViewModelクラス")
    }
}