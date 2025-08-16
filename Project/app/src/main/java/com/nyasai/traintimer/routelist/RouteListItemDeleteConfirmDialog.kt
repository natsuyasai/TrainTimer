package com.nyasai.traintimer.routelist

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nyasai.traintimer.R

/**
 * 路線リストアイテム削除確認ダイアログ
 */
@Composable
fun RouteListItemDeleteConfirmDialog(
    isVisible: Boolean,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "削除の確認",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.route_list_item_delete_confirm_message),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onPositiveClick()
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(id = R.string.route_list_item_delete_confirm_yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onNegativeClick()
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(id = R.string.route_list_item_delete_confirm_no))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RouteListItemDeleteConfirmDialogPreview() {
    RouteListItemDeleteConfirmDialog(
        isVisible = true,
        onPositiveClick = {},
        onNegativeClick = {},
        onDismiss = {}
    )
}