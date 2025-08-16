package com.nyasai.traintimer.routesearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onPositiveClick: (String) -> Unit,
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
                                        selectedColor = colorResource(id = R.color.textColor),
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
                        onPositiveClick(currentSelectedItem)
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "OK",
                        color = colorResource(id = R.color.textColor)
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

@Preview(showBackground = true)
@Composable
private fun ListItemSelectDialogPreview() {
    ListItemSelectDialog(
        isVisible = true,
        title = "路線を選択してください",
        items = listOf("JR山手線", "JR中央線", "東京メトロ丸ノ内線", "都営新宿線"),
        selectedItem = "JR山手線",
        onItemSelect = {},
        onPositiveClick = { _ -> },
        onNegativeClick = {},
        onDismiss = {}
    )
}