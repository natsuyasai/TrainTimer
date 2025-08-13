package com.nyasai.traintimer.routesearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.nyasai.traintimer.R

/**
 * 検索対象入力用Composeダイアログ
 */
@Composable
fun SearchTargetInputDialog(
    viewModel: SearchTargetInputViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.search_input_message))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = viewModel.stationNameState,
                    onValueChange = viewModel::updateStationName,
                    label = { Text("駅名") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.onClickPositiveButtonCallback?.invoke()
                    viewModel.clearUIData()
                    onConfirm()
                }
            ) {
                Text(stringResource(id = R.string.search_input_yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.onClickNegativeButtonCallback?.invoke()
                    viewModel.clearUIData()
                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.search_input_no))
            }
        }
    )
}