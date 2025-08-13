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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nyasai.traintimer.R

/**
 * リストアイテム選択ダイアログのComposeコンポーネント
 */
@Composable
fun ListItemSelectDialogWithViewModel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: ListItemSelectViewModel,
    title: String = "選択してください",
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.colorNormalBackground)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // タイトル
                    Text(
                        text = title,
                        color = colorResource(id = R.color.textColor),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    
                    // アイテムリスト
                    val items = viewModel.itemsState
                    var selectedItem by remember { mutableStateOf(viewModel.selectedItemState) }
                    
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
                                        selected = (item == selectedItem),
                                        onClick = {
                                            selectedItem = item
                                            viewModel.updateSelectedItem(item)
                                        }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (item == selectedItem),
                                    onClick = {
                                        selectedItem = item
                                        viewModel.updateSelectedItem(item)
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
                    
                    // ボタン群
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                viewModel.onClickNegativeButtonCallback?.invoke()
                                onDismiss()
                            }
                        ) {
                            Text(
                                text = "キャンセル",
                                color = colorResource(id = R.color.textColor)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                viewModel.onClickPositiveButtonCallback?.invoke()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.actionBar)
                            )
                        ) {
                            Text(
                                text = "OK",
                                color = colorResource(id = R.color.textColorPrimary)
                            )
                        }
                    }
                }
            }
        }
    }
}