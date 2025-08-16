package com.nyasai.traintimer.routelist.dialogs

import androidx.compose.runtime.Composable
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.RouteListViewModel
import com.nyasai.traintimer.routelist.logic.DialogManager
import com.nyasai.traintimer.routelist.parts.RouteListItemDeleteConfirmDialog

/**
 * 削除確認ダイアログの表示と処理を管理するComposable関数
 */
@Composable
fun DeleteConfirmDialogHandler(
    showDeleteConfirmDialog: Boolean,
    selectedItem: RouteListItem?,
    onDialogDismiss: () -> Unit,
    dialogManager: DialogManager,
    routeListViewModel: RouteListViewModel,
    onSelectedItemClear: () -> Unit
) {
    if (showDeleteConfirmDialog && selectedItem != null) {
        RouteListItemDeleteConfirmDialog(
            isVisible = showDeleteConfirmDialog,
            onPositiveClick = {
                dialogManager.handleDeleteConfirmPositiveClick(
                    selectedItem.dataId,
                    routeListViewModel,
                    onDialogDismiss,
                    onSelectedItemClear
                )
            },
            onNegativeClick = {
                onDialogDismiss()
            },
            onDismiss = onDialogDismiss
        )
    }
}