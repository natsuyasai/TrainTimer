package com.nyasai.traintimer.routesearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nyasai.traintimer.R

/**
 * 検索対象入力用ダイアログ (Jetpack Compose版)
 */
@Composable
fun SearchTargetInputDialog(
    isVisible: Boolean,
    stationName: String,
    onStationNameChange: (String) -> Unit,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        val focusManager = LocalFocusManager.current

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(id = R.string.search_input_message),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = stationName,
                        onValueChange = onStationNameChange,
                        label = {
                            Text(text = "駅名")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onPositiveClick()
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(id = R.string.search_input_yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onNegativeClick()
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(id = R.string.search_input_no))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchTargetInputDialogPreview() {
    SearchTargetInputDialog(
        isVisible = true,
        stationName = "新宿",
        onStationNameChange = {},
        onPositiveClick = {},
        onNegativeClick = {},
        onDismiss = {}
    )
}