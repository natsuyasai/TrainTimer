package com.nyasai.traintimer.routelist.dialogs

import androidx.compose.runtime.Composable
import com.nyasai.traintimer.database.RouteListItem
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
    onSelectedItemUpdate: (RouteListItem) -> Unit,
    onColorUpdateTrigger: () -> Unit
) {
    if (showColorSelectDialog && selectedItem != null) {
        ColorSelectDialog(
            isVisible = showColorSelectDialog,
            currentColor = selectedItem.displayColor,
            onColorSelected = { newColor ->
                // selectedItemのdisplayColorを即座に更新
                val updatedItem = selectedItem.apply { displayColor = newColor }
                onSelectedItemUpdate(updatedItem)
                editModeManager.handleColorUpdate(updatedItem, newColor)
                // リコンポジションを強制するためのトリガー更新
                onColorUpdateTrigger()
                onDialogDismiss()
            },
            onDismiss = onDialogDismiss
        )
    }
}