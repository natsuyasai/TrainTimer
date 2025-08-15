package com.nyasai.traintimer.routelist

import com.nyasai.traintimer.commonparts.CommonLoadingViewModel
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routesearch.ListItemSelectViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 路線登録処理機能を管理するクラス
 */
class RouteRegistrationManager(
    private val routeListViewModel: RouteListViewModel
) {
    
    /**
     * 目的地選択ダイアログの肯定ボタンクリック処理
     */
    fun handleDestinationSelectPositiveClick(
        scope: CoroutineScope,
        listSelectViewModel: ListItemSelectViewModel,
        loadingViewModel: CommonLoadingViewModel,
        destinationOptions: Map<String, String>,
        currentStationName: String,
        hideDestinationDialog: () -> Unit
    ) {
        scope.launch {
            hideDestinationDialog()
            loadingViewModel.showLoading("時刻情報取得中")
            
            try {
                val selectedDestination = listSelectViewModel.selectedItemState
                val url = destinationOptions.getValue(selectedDestination)
                
                val (routeInfo, parentDataId) = withContext(Dispatchers.IO) {
                    val routeInfo = routeListViewModel.getTimeTableInfo(
                        url,
                        { loadingViewModel.incrementMaxCountFromBackgroundTask(it) },
                        { loadingViewModel.incrementCurrentCountFromBackgroundTask(1) }
                    )
                    
                    val newRouteListItem = createRouteListItem(selectedDestination, currentStationName)
                    val parentDataId = routeListViewModel.registerRouteListItem(routeInfo, newRouteListItem)
                    Pair(routeInfo, parentDataId)
                }
                
                loadingViewModel.changeText("時刻情報登録中")
                
                withContext(Dispatchers.IO) {
                    routeListViewModel.registerRouteInfoDetailItems(routeInfo, parentDataId)
                }
            } catch (e: Exception) {
                // エラーハンドリング
            } finally {
                loadingViewModel.closeLoading()
            }
        }
    }
    
    
    /**
     * 新しい路線リストアイテムの作成
     */
    private fun createRouteListItem(
        selectedDestination: String,
        currentStationName: String
    ): RouteListItem {
        return RouteListItem().apply {
            val splitDestinationKey = routeListViewModel.splitDestinationKey(selectedDestination)
            routeName = splitDestinationKey.first
            destination = splitDestinationKey.second
            stationName = currentStationName
        }
    }
}