package com.nyasai.traintimer.routeinfo

import com.nyasai.traintimer.database.FilterInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * フィルター機能を管理するクラス
 */
class FilterManager {
    
    /**
     * フィルターボタンクリック処理
     */
    fun handleFilterButtonClick(
        scope: CoroutineScope,
        filterInfo: List<FilterInfo>,
        filterItemSelectViewModel: FilterItemSelectViewModel,
        routeInfoViewModel: RouteInfoViewModel,
        showDialog: () -> Unit
    ) {
        scope.launch {
            try {
                // 現在のフィルタ情報を取得してダイアログに設定
                if (filterInfo.isNotEmpty()) {
                    filterItemSelectViewModel.updateFilterItems(filterInfo)
                } else {
                    // フィルタ情報が空の場合は同期取得
                    val syncFilterItems = routeInfoViewModel.getFilterInfoItemWithParentIdSync()
                    filterItemSelectViewModel.updateFilterItems(syncFilterItems)
                }
                showDialog()
            } catch (e: Exception) {
                // エラーハンドリング
                showDialog() // ダイアログは表示する
            }
        }
    }
    
    /**
     * フィルターダイアログ肯定ボタンクリック処理
     */
    fun handleFilterPositiveClick(
        scope: CoroutineScope,
        routeInfoViewModel: RouteInfoViewModel,
        filterItemSelectViewModel: FilterItemSelectViewModel,
        triggerUpdate: () -> Unit,
        hideDialog: () -> Unit
    ) {
        scope.launch {
            try {
                // IOディスパッチャーでデータベース更新を実行
                withContext(Dispatchers.IO) {
                    routeInfoViewModel.updateFilterInfoListItem(filterItemSelectViewModel.filterItemsState)
                }
                // ViewModelのキャッシュをクリア
                routeInfoViewModel.clearDisplayCache()
                // 少し遅延を入れてからUIを更新（データベース更新の完了を確実にするため）
                kotlinx.coroutines.delay(100)
                // フィルター更新トリガーを増加させてLaunchedEffectを実行
                triggerUpdate()
            } catch (e: Exception) {
                // エラーハンドリング
            }
        }
        hideDialog()
    }
    
    /**
     * フィルターダイアログ否定ボタンクリック処理
     */
    fun handleFilterNegativeClick(
        hideDialog: () -> Unit
    ) {
        hideDialog()
    }
}