package com.nyasai.traintimer.routeinfo.parts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.routeinfo.RouteInfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * フィルタダイアログの表示と処理を管理するComposable関数
 */
@Composable
fun FilterDialogHandler(
    showFilterDialog: Boolean,
    localFilterItems: List<FilterInfo>,
    onFilterItemsChange: (List<FilterInfo>) -> Unit,
    onDialogDismiss: () -> Unit,
    routeInfoViewModel: RouteInfoViewModel,
    onFilterUpdate: () -> Unit
) {
    val scope = rememberCoroutineScope()
    
    if (showFilterDialog) {
        FilterItemSelectDialog(
            isVisible = showFilterDialog,
            filterItems = localFilterItems,
            onItemToggle = { index ->
                if (index in 0 until localFilterItems.size) {
                    val mutableList = localFilterItems.toMutableList()
                    val item = mutableList[index]
                    // 新しいFilterInfoオブジェクトを作成して状態を変更
                    mutableList[index] = FilterInfo(
                        item.dataId,
                        item.parentDataId,
                        item.trainTypeAndDestination,
                        !item.isShow
                    )
                    onFilterItemsChange(mutableList)
                }
            },
            onPositiveClick = {
                // フィルタ情報を更新
                scope.launch {
                    withContext(Dispatchers.IO) {
                        routeInfoViewModel.updateFilterInfoListItem(localFilterItems)
                    }
                    onFilterUpdate()
                    onDialogDismiss()
                }
            },
            onNegativeClick = {
                onDialogDismiss()
            },
            onDismiss = onDialogDismiss
        )
    }
}