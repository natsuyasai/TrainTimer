package com.nyasai.traintimer.routelist

import com.nyasai.traintimer.commonparts.CommonLoadingViewModel
import com.nyasai.traintimer.database.RouteListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 編集モード関連機能を管理するクラス
 */
class EditModeManager(
    private val routeListViewModel: RouteListViewModel
) {
    
    /**
     * 編集モード切り替え処理
     */
    fun handleEditModeToggle() {
        routeListViewModel.switchEditMode()
    }
    
    /**
     * 編集ダイアログの肯定ボタンクリック処理
     */
    fun handleEditDialogPositiveClick(
        editType: RouteListItemEditViewModel.EditType,
        scope: CoroutineScope,
        loadingViewModel: CommonLoadingViewModel,
        selectedItem: RouteListItem,
        showDeleteDialog: () -> Unit,
        showColorDialog: () -> Unit,
        hideEditDialog: () -> Unit
    ) {
        when (editType) {
            RouteListItemEditViewModel.EditType.Update -> {
                handleRouteUpdate(scope, loadingViewModel, selectedItem)
            }
            RouteListItemEditViewModel.EditType.SetColor -> {
                showColorDialog()
            }
            RouteListItemEditViewModel.EditType.Delete -> {
                showDeleteDialog()
            }
            RouteListItemEditViewModel.EditType.None -> {
                // 何もしない
            }
        }
        hideEditDialog()
    }
    
    /**
     * 路線情報更新処理
     */
    private fun handleRouteUpdate(
        scope: CoroutineScope,
        loadingViewModel: CommonLoadingViewModel,
        selectedItem: RouteListItem
    ) {
        scope.launch {
            loadingViewModel.showLoading("時刻情報更新中")
            try {
                withContext(Dispatchers.IO) {
                    routeListViewModel.updateRouteInfo(
                        selectedItem,
                        { loadingViewModel.incrementMaxCountFromBackgroundTask(it) },
                        { loadingViewModel.incrementCurrentCountFromBackgroundTask(1) }
                    )
                }
            } catch (e: Exception) {
                // エラーハンドリング
            } finally {
                loadingViewModel.closeLoading()
            }
        }
    }
    
    /**
     * 路線の色更新処理
     */
    fun handleColorUpdate(
        selectedItem: RouteListItem,
        newColor: Int?
    ) {
        selectedItem.displayColor = newColor
        routeListViewModel.updateRouteListItemColor(selectedItem.dataId, newColor)
    }
}