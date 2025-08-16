package com.nyasai.traintimer.routelist.state

import androidx.compose.runtime.Stable
import androidx.lifecycle.LiveData
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Collections

/**
 * リストのソート・編集モード管理を担当するクラス
 */
@Stable
class RouteSortManager(
    private val database: RouteDatabaseDao,
    private val coroutineScope: CoroutineScope
) {
    
    /**
     * ソート情報更新
     */
    fun updateSortIndex(routeList: LiveData<List<RouteListItem>>, from: Int, to: Int) {
        if (routeList.value == null) {
            return
        }
        
        val routeListItems = routeList.value!!
        
        if (from < to) {
            for (i in from until to) {
                Collections.swap(routeListItems, i, i + 1)
                val order1 = routeListItems[i].sortIndex
                val order2 = routeListItems[i + 1].sortIndex
                routeListItems[i].sortIndex = order2
                routeListItems[i + 1].sortIndex = order1
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(routeListItems, i, i - 1)
                val order1 = routeListItems[i].sortIndex
                val order2 = routeListItems[i - 1].sortIndex
                routeListItems[i].sortIndex = order2
                routeListItems[i - 1].sortIndex = order1
            }
        }
        
        coroutineScope.launch {
            database.updateRouteListItems(routeListItems)
        }
    }
}