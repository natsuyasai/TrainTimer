package com.nyasai.traintimer.routelist.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.nyasai.traintimer.commonparts.LoadingState
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.logic.EditModeManager
import com.nyasai.traintimer.routelist.parts.RouteListItemEditDialog
import com.nyasai.traintimer.routelist.parts.rememberRouteListItemEditState

/**
 * 編集ダイアログの表示と処理を管理するComposable関数
 */
@Composable
fun EditDialogHandler(
    showEditDialog: Boolean,
    selectedItem: RouteListItem?,
    onDialogDismiss: () -> Unit,
    editModeManager: EditModeManager,
    loadingState: LoadingState,
    onShowDeleteConfirmDialog: () -> Unit,
    onShowColorSelectDialog: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val editState = rememberRouteListItemEditState()
    
    if (showEditDialog && selectedItem != null) {
        // データIDを設定
        editState.actions.setTargetDataId(selectedItem.dataId)

        RouteListItemEditDialog(
            isVisible = showEditDialog,
            selectedEditType = editState.state.selectedEditType,
            onEditTypeChange = editState.actions::updateEditType,
            onPositiveClick = {
                editModeManager.handleEditDialogPositiveClick(
                    editState.state.selectedEditType,
                    scope,
                    loadingState,
                    selectedItem,
                    onShowDeleteConfirmDialog,
                    onShowColorSelectDialog,
                    onDialogDismiss
                )
                editState.actions.clearUIData()
            },
            onNegativeClick = {
                editState.actions.clearUIData()
            },
            onDismiss = onDialogDismiss
        )
    }
}