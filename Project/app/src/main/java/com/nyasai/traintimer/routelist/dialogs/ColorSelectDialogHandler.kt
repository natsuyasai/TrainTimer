package com.nyasai.traintimer.routelist.dialogs

import androidx.compose.runtime.Composable
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.RouteListScreenState
import com.nyasai.traintimer.routelist.logic.EditModeManager
import com.nyasai.traintimer.routelist.parts.ColorSelectDialog

/**
 * 色選択ダイアログの表示と処理を管理するComposable関数
 */
@Composable
fun ColorSelectDialogHandler(
    showColorSelectDialog: Boolean,
    selectedItem: RouteListItem?,
    onDialogDismiss: () -> Unit,
    editModeManager: EditModeManager,
    screenState: RouteListScreenState
) {
    if (showColorSelectDialog && selectedItem != null) {
        ColorSelectDialog(
            isVisible = showColorSelectDialog,
            currentColor = selectedItem.displayColor,
            onColorSelected = { newColor ->
                // 新しいRouteListItemインスタンスを作成（不変性を保つ）
                val updatedItem = RouteListItem().apply {
                    dataId = selectedItem.dataId
                    routeName = selectedItem.routeName
                    stationName = selectedItem.stationName
                    destination = selectedItem.destination
                    sortIndex = selectedItem.sortIndex
                    displayColor = newColor
                }
                
                // 選択アイテムを更新
                screenState.updateSelectedItem(updatedItem)
                
                // ローカルリストを即座に更新（画面に即座に反映）
                screenState.updateLocalRouteListItem(updatedItem)
                
                // データベースに保存
                editModeManager.handleColorUpdate(updatedItem, newColor)
                
                // リコンポジションを強制するためのトリガー更新
                screenState.incrementColorUpdateTrigger()
                
                onDialogDismiss()
            },
            onDismiss = onDialogDismiss
        )
    }
}