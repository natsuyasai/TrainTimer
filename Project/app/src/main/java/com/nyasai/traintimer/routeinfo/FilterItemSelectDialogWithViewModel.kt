package com.nyasai.traintimer.routeinfo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.FilterInfo

/**
 * フィルタ対象選択ダイアログ (Jetpack Compose版)
 */
@Composable
fun FilterItemSelectDialog(
    isVisible: Boolean,
    filterItems: List<FilterInfo>,
    onItemToggle: (Int) -> Unit,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(id = R.string.select_filter_message),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    filterItems.forEachIndexed { index, filterInfo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = filterInfo.isShow,
                                onCheckedChange = {
                                    onItemToggle(index)
                                }
                            )
                            Text(
                                text = filterInfo.trainTypeAndDestination,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    if (filterItems.isEmpty()) {
                        Text(
                            text = "表示対象がありません",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    Text(text = stringResource(id = R.string.select_filter_yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onNegativeClick()
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(id = R.string.select_filter_no))
                }
            }
        )
    }
}

/**
 * ViewModelと統合されたFilterItemSelectDialog
 */
@Composable
fun FilterItemSelectDialogWithViewModel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: FilterItemSelectViewModel = viewModel()
) {
    FilterItemSelectDialog(
        isVisible = isVisible,
        filterItems = viewModel.filterItemsState,
        onItemToggle = viewModel::toggleItemVisibility,
        onPositiveClick = {
            viewModel.onPositiveButtonClick()
        },
        onNegativeClick = {
            viewModel.onNegativeButtonClick()
        },
        onDismiss = onDismiss
    )
}