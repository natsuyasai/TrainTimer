package com.nyasai.traintimer.routeinfo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
 * フィルタアイテム選択ダイアログのComposeコンポーネント
 */
@Composable
fun FilterItemSelectDialogWithViewModel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: FilterItemSelectViewModel,
    title: String = "表示フィルタ設定",
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
                    
                    // フィルタアイテムリスト
                    val filterItems = viewModel.filterItemsState
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                    ) {
                        itemsIndexed(filterItems) { index, filterItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = filterItem.isShow,
                                    onCheckedChange = { _ ->
                                        viewModel.toggleItemVisibility(index)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = colorResource(id = R.color.actionBar),
                                        uncheckedColor = colorResource(id = R.color.textColor),
                                        checkmarkColor = colorResource(id = R.color.textColorPrimary)
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // 種別名+行先の組み合わせ
                                    Text(
                                        text = filterItem.trainTypeAndDestination,
                                        color = colorResource(id = R.color.textColor),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            if (index < filterItems.size - 1) {
                                Divider(
                                    color = colorResource(id = R.color.textGray),
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
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
                                viewModel.onNegativeButtonClick()
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
                                viewModel.onPositiveButtonClick()
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