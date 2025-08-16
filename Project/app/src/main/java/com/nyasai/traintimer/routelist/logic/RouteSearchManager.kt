package com.nyasai.traintimer.routelist.logic

import com.nyasai.traintimer.commonparts.CommonLoadingViewModel
import com.nyasai.traintimer.routelist.RouteListViewModel
import com.nyasai.traintimer.routesearch.ListItemSelectViewModel
import com.nyasai.traintimer.util.WakeLockManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 路線検索関連機能を管理するクラス
 */
class RouteSearchManager(
    private val routeListViewModel: RouteListViewModel,
    private val wakeLockManager: WakeLockManager
) {

    /**
     * 検索ダイアログの肯定ボタンクリック処理（純粋Composable版）
     */
    fun handleSearchDialogPositiveClick(
        scope: CoroutineScope,
        stationName: String,
        loadingViewModel: CommonLoadingViewModel,
        listSelectViewModel: ListItemSelectViewModel,
        setCurrentStationName: (String) -> Unit,
        setStationOptions: (Map<String, String>) -> Unit,
        setDestinationOptions: (Map<String, String>) -> Unit,
        hideSearchDialog: () -> Unit,
        showStationDialog: () -> Unit,
        showDestinationDialog: () -> Unit
    ) {
        scope.launch {
            hideSearchDialog()
            loadingViewModel.showLoading()
            
            // WakeLockを取得してスリープを防止
            wakeLockManager.acquireWakeLock()
            
            try {
                setCurrentStationName(stationName)
                
                val (stationListMap, destinationListMap) = withContext(Dispatchers.IO) {
                    fetchStationAndDestinationData(stationName)
                }
                
                handleSearchResults(
                    stationListMap,
                    destinationListMap,
                    listSelectViewModel,
                    setStationOptions,
                    setDestinationOptions,
                    showStationDialog,
                    showDestinationDialog
                )
            } catch (e: Exception) {
                // エラーハンドリング
            } finally {
                loadingViewModel.closeLoading()
                // WakeLockを解放
                wakeLockManager.releaseWakeLock()
            }
        }
    }
    
    /**
     * 駅選択ダイアログの肯定ボタンクリック処理
     */
    fun handleStationSelectPositiveClick(
        scope: CoroutineScope,
        listSelectViewModel: ListItemSelectViewModel,
        loadingViewModel: CommonLoadingViewModel,
        stationOptions: Map<String, String>,
        setCurrentStationName: (String) -> Unit,
        setDestinationOptions: (Map<String, String>) -> Unit,
        hideStationDialog: () -> Unit,
        showDestinationDialog: () -> Unit
    ) {
        scope.launch {
            hideStationDialog()
            loadingViewModel.showLoading()
            
            // WakeLockを取得してスリープを防止
            wakeLockManager.acquireWakeLock()
            
            try {
                val selectedStation = listSelectViewModel.selectedItemState
                setCurrentStationName(selectedStation)
                
                val destinationListMap = withContext(Dispatchers.IO) {
                    routeListViewModel.getDestinationFromUrl(
                        stationOptions.getValue(selectedStation)
                    )
                }
                
                setDestinationOptions(destinationListMap)
                listSelectViewModel.updateItems(destinationListMap.keys.toList())
                showDestinationDialog()
            } catch (e: Exception) {
                // エラーハンドリング
            } finally {
                loadingViewModel.closeLoading()
                // WakeLockを解放
                wakeLockManager.releaseWakeLock()
            }
        }
    }
    
    /**
     * 駅と目的地データの取得
     */
    private fun fetchStationAndDestinationData(
        stationName: String
    ): Pair<Map<String, String>?, Map<String, String>> {
        val stationList = routeListViewModel.getStationList(stationName)
        val destinationList = if (stationList?.isEmpty() != false) {
            routeListViewModel.getDestinationFromStationName(stationName)
        } else {
            emptyMap()
        }
        return Pair(stationList, destinationList)
    }
    
    /**
     * 検索結果の処理
     */
    private fun handleSearchResults(
        stationListMap: Map<String, String>?,
        destinationListMap: Map<String, String>,
        listSelectViewModel: ListItemSelectViewModel,
        setStationOptions: (Map<String, String>) -> Unit,
        setDestinationOptions: (Map<String, String>) -> Unit,
        showStationDialog: () -> Unit,
        showDestinationDialog: () -> Unit
    ) {
        if (stationListMap?.isNotEmpty() == true) {
            setStationOptions(stationListMap)
            listSelectViewModel.updateItems(stationListMap.keys.toList())
            showStationDialog()
        } else {
            setDestinationOptions(destinationListMap)
            listSelectViewModel.updateItems(destinationListMap.keys.toList())
            showDestinationDialog()
        }
    }
}