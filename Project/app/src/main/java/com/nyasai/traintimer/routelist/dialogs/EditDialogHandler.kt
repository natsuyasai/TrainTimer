package com.nyasai.traintimer.routelist.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.commonparts.CommonLoadingViewModel
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.logic.EditModeManager
import com.nyasai.traintimer.routelist.parts.RouteListItemEditDialogWithViewModel
import com.nyasai.traintimer.routelist.parts.RouteListItemEditViewModel

/**
 * 編集ダイアログの表示と処理を管理するComposable関数
 */
@Composable
fun EditDialogHandler(
    showEditDialog: Boolean,
    selectedItem: RouteListItem?,
    onDialogDismiss: () -> Unit,
    editModeManager: EditModeManager,
    commonLoadingViewModel: CommonLoadingViewModel,
    onShowDeleteConfirmDialog: () -> Unit,
    onShowColorSelectDialog: () -> Unit
) {
    val scope = rememberCoroutineScope()
    
    if (showEditDialog && selectedItem != null) {
        // ViewModelインスタンス
        val routeListItemEditViewModel: RouteListItemEditViewModel = viewModel()
        
        // コールバックを事前に設定
        routeListItemEditViewModel.onClickPositiveButtonCallback = { editType, dataId ->
            editModeManager.handleEditDialogPositiveClick(
                editType,
                scope,
                commonLoadingViewModel,
                selectedItem,
                onShowDeleteConfirmDialog,
                onShowColorSelectDialog,
                onDialogDismiss
            )
        }
        routeListItemEditViewModel.onClickNegativeButtonCallback = { _, _ ->
            onDialogDismiss()
        }

        RouteListItemEditDialogWithViewModel(
            isVisible = showEditDialog,
            targetDataId = selectedItem.dataId,
            onDismiss = onDialogDismiss,
            viewModel = routeListItemEditViewModel
        )
    }
}