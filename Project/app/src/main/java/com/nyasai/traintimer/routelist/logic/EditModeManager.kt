package com.nyasai.traintimer.routelist.logic

import com.nyasai.traintimer.commonparts.LoadingState
import com.nyasai.traintimer.commonparts.loadingState
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.parts.EditType
import com.nyasai.traintimer.routelist.RouteListScreenState
import com.nyasai.traintimer.util.WakeLockManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 編集モード関連機能を管理するクラス
 */
class EditModeManager(
    private val screenState: RouteListScreenState,
    private val wakeLockManager: WakeLockManager? = null
) {
    
    /**
     * 編集モード切り替え処理
     */
    fun handleEditModeToggle() {
        screenState.switchEditMode()
    }
    
    /**
     * 編集ダイアログの肯定ボタンクリック処理
     */
    fun handleEditDialogPositiveClick(
        editType: EditType,
        scope: CoroutineScope,
        loadingState: LoadingState,
        selectedItem: RouteListItem,
        showDeleteDialog: () -> Unit,
        showColorDialog: () -> Unit,
        hideEditDialog: () -> Unit
    ) {
        when (editType) {
            EditType.Update -> {
                handleRouteUpdate(scope, loadingState, selectedItem)
            }
            EditType.SetColor -> {
                showColorDialog()
            }
            EditType.Delete -> {
                showDeleteDialog()
            }
            EditType.None -> {
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
        loadingState: LoadingState,
        selectedItem: RouteListItem
    ) {
        scope.launch {
            loadingState.actions.show("時刻情報更新中")
            
            // WakeLockを取得してスリープを防止
            wakeLockManager?.acquireWakeLock()
            
            try {
                withContext(Dispatchers.IO) {
                    screenState.updateRouteInfo(
                        selectedItem,
                        { loadingState.actions.incrementMaxCount(it) },
                        { loadingState.actions.incrementCurrentCount(1) }
                    )
                }
            } catch (e: Exception) {
                // エラーハンドリング
            } finally {
                loadingState.actions.close()
                // WakeLockを解放
                wakeLockManager?.releaseWakeLock()
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
        screenState.updateRouteListItemColor(selectedItem.dataId, newColor)
    }
}