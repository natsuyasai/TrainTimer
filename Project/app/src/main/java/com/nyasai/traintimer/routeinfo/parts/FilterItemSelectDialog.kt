package com.nyasai.traintimer.routeinfo.parts

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                                .padding(vertical = 4.dp)
                                .clickable { onItemToggle(index) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = filterInfo.isShow,
                                onCheckedChange = {
                                    onItemToggle(index)
                                },
                                modifier = Modifier.testTag(filterInfo.trainTypeAndDestination)
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


@Preview(showBackground = true)
@Composable
private fun FilterItemSelectDialogPreview() {
    val sampleFilterItems = listOf(
        FilterInfo().apply {
            dataId = 1
            parentDataId = 1
            trainTypeAndDestination = "快速 新宿方面"
            isShow = true
        },
        FilterInfo().apply {
            dataId = 2
            parentDataId = 1
            trainTypeAndDestination = "普通 立川方面"
            isShow = false
        },
        FilterInfo().apply {
            dataId = 3
            parentDataId = 1
            trainTypeAndDestination = "特急 高尾方面"
            isShow = true
        }
    )
    
    FilterItemSelectDialog(
        isVisible = true,
        filterItems = sampleFilterItems,
        onItemToggle = {},
        onPositiveClick = {},
        onNegativeClick = {},
        onDismiss = {}
    )
}