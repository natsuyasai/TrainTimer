package com.nyasai.traintimer.routelist.logic

import com.nyasai.traintimer.commonparts.LoadingState
import com.nyasai.traintimer.commonparts.loadingState
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.RouteListViewModel
import com.nyasai.traintimer.util.WakeLockManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 路線登録処理機能を管理するクラス
 */
class RouteRegistrationManager(
    private val routeListViewModel: RouteListViewModel,
    private val wakeLockManager: WakeLockManager
) {

    /**
     * 目的地選択ダイアログの肯定ボタンクリック処理
     */
    fun handleDestinationSelectPositiveClick(
        scope: CoroutineScope,
        selectedDestination: String,
        loadingState: LoadingState,
        destinationOptions: Map<String, String>,
        currentStationName: String,
        hideDestinationDialog: () -> Unit
    ) {
        scope.launch {
            hideDestinationDialog()
            loadingState.actions.show("時刻情報取得中")
            
            // WakeLockを取得してスリープを防止
            wakeLockManager.acquireWakeLock()
            
            try {
                val url = destinationOptions.getValue(selectedDestination)
                
                val (routeInfo, parentDataId) = withContext(Dispatchers.IO) {
                    val routeInfo = routeListViewModel.getTimeTableInfo(
                        url,
                        { loadingState.actions.incrementMaxCount(it) },
                        { loadingState.actions.incrementCurrentCount(1) }
                    )
                    
                    val newRouteListItem = createRouteListItem(selectedDestination, currentStationName)
                    val parentDataId = routeListViewModel.registerRouteListItem(routeInfo, newRouteListItem)
                    Pair(routeInfo, parentDataId)
                }

                loadingState.actions.changeText("時刻情報登録中")
                
                withContext(Dispatchers.IO) {
                    routeListViewModel.registerRouteInfoDetailItems(routeInfo, parentDataId)
                }
            } catch (e: Exception) {
                // エラーハンドリング
            } finally {
                loadingState.actions.close()
                // WakeLockを解放
                wakeLockManager.releaseWakeLock()
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