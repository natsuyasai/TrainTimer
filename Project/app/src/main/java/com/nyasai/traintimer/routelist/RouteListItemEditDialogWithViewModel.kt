package com.nyasai.traintimer.routelist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.R

/**
 * 路線一覧アイテム編集ダイアログ (Jetpack Compose版)
 */
@Composable
fun RouteListItemEditDialog(
    isVisible: Boolean,
    selectedEditType: RouteListItemEditViewModel.EditType,
    onEditTypeChange: (RouteListItemEditViewModel.EditType) -> Unit,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        val editOptions = listOf(
            "更新" to RouteListItemEditViewModel.EditType.Update,
            "色設定" to RouteListItemEditViewModel.EditType.SetColor,
            "削除" to RouteListItemEditViewModel.EditType.Delete
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "編集操作を選択してください",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                ) {
                    editOptions.forEach { (optionText, editType) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (editType == selectedEditType),
                                    onClick = { onEditTypeChange(editType) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (editType == selectedEditType),
                                onClick = null
                            )
                            Text(
                                text = optionText,
                                modifier = Modifier.padding(start = 16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onPositiveClick()
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(id = R.string.route_list_item_edit_yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onNegativeClick()
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(id = R.string.route_list_item_edit_no))
                }
            }
        )
    }
}

/**
 * ViewModelと統合されたRouteListItemEditDialog
 */
@Composable
fun RouteListItemEditDialogWithViewModel(
    isVisible: Boolean,
    targetDataId: Long?,
    onDismiss: () -> Unit,
    viewModel: RouteListItemEditViewModel = viewModel()
) {
    // データIDを設定
    if (targetDataId != null) {
        viewModel.setTargetDataId(targetDataId)
    }

    RouteListItemEditDialog(
        isVisible = isVisible,
        selectedEditType = viewModel.selectedEditType,
        onEditTypeChange = viewModel::updateEditType,
        onPositiveClick = {
            viewModel.onPositiveButtonClick()
            viewModel.clearUIData()
        },
        onNegativeClick = {
            viewModel.onNegativeButtonClick()
            viewModel.clearUIData()
        },
        onDismiss = onDismiss
    )
}

@Preview(showBackground = true)
@Composable
private fun RouteListItemEditDialogPreview() {
    RouteListItemEditDialog(
        isVisible = true,
        selectedEditType = RouteListItemEditViewModel.EditType.Update,
        onEditTypeChange = {},
        onPositiveClick = {},
        onNegativeClick = {},
        onDismiss = {}
    )
}