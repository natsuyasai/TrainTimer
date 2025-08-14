package com.nyasai.traintimer.routesearch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.R

/**
 * リストアイテム選択ダイアログ (Jetpack Compose版)
 */
@Composable
fun ListItemSelectDialog(
    isVisible: Boolean,
    title: String,
    items: List<String>,
    selectedItem: String,
    onItemSelect: (String) -> Unit,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        var currentSelectedItem by remember { mutableStateOf(selectedItem) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorResource(id = R.color.textColor)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    ) {
                        items(items) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (item == currentSelectedItem),
                                        onClick = {
                                            currentSelectedItem = item
                                            onItemSelect(item)
                                        }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (item == currentSelectedItem),
                                    onClick = {
                                        currentSelectedItem = item
                                        onItemSelect(item)
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = colorResource(id = R.color.actionBar),
                                        unselectedColor = colorResource(id = R.color.textColor)
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item,
                                    color = colorResource(id = R.color.textColor),
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
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
                    Text(
                        text = "OK",
                        color = colorResource(id = R.color.actionBar)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onNegativeClick()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "キャンセル",
                        color = colorResource(id = R.color.textColor)
                    )
                }
            }
        )
    }
}

/**
 * ViewModelと統合されたListItemSelectDialog
 */
@Composable
fun ListItemSelectDialogWithViewModel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: ListItemSelectViewModel = viewModel(),
    title: String = "選択してください"
) {
    ListItemSelectDialog(
        isVisible = isVisible,
        title = title,
        items = viewModel.itemsState,
        selectedItem = viewModel.selectedItemState,
        onItemSelect = viewModel::updateSelectedItem,
        onPositiveClick = {
            viewModel.onClickPositiveButtonCallback?.invoke()
        },
        onNegativeClick = {
            viewModel.onClickNegativeButtonCallback?.invoke()
        },
        onDismiss = onDismiss
    )
}

@Preview(showBackground = true)
@Composable
private fun ListItemSelectDialogPreview() {
    ListItemSelectDialog(
        isVisible = true,
        title = "路線を選択してください",
        items = listOf("JR山手線", "JR中央線", "東京メトロ丸ノ内線", "都営新宿線"),
        selectedItem = "JR山手線",
        onItemSelect = {},
        onPositiveClick = {},
        onNegativeClick = {},
        onDismiss = {}
    )
}