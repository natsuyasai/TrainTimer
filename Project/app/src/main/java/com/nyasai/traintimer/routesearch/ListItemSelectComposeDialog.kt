package com.nyasai.traintimer.routesearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.nyasai.traintimer.R

/**
 * リストアイテム選択Composeダイアログ
 */
@Composable
fun ListItemSelectDialog(
    viewModel: ListItemSelectViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.select_station_message))
        },
        text = {
            Column(
                modifier = Modifier.selectableGroup()
            ) {
                viewModel.itemsState.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (item == viewModel.selectedItemState),
                                onClick = {
                                    viewModel.updateSelectedItem(item)
                                },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (item == viewModel.selectedItemState),
                            onClick = null // null にして Row の selectable で処理
                        )
                        Text(
                            text = item,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.onClickPositiveButtonCallback?.invoke()
                    onConfirm()
                }
            ) {
                Text(stringResource(id = R.string.select_station_yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.onClickNegativeButtonCallback?.invoke()
                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.select_station_no))
            }
        }
    )
}