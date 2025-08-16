package com.nyasai.traintimer.routelist.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.nyasai.traintimer.commonparts.CommonLoadingViewModel
import com.nyasai.traintimer.routelist.logic.RouteSearchManager
import com.nyasai.traintimer.routesearch.ListItemSelectDialog

/**
 * 駅選択ダイアログの表示と処理を管理するComposable関数
 */
@Composable
fun StationSelectDialogHandler(
    showStationSelectDialog: Boolean,
    stationOptions: Map<String, String>,
    selectedStationItem: String,
    onStationItemChange: (String) -> Unit,
    onDialogDismiss: () -> Unit,
    routeSearchManager: RouteSearchManager,
    commonLoadingViewModel: CommonLoadingViewModel,
    onCurrentStationNameChange: (String) -> Unit,
    onDestinationOptionsChange: (Map<String, String>) -> Unit,
    onShowDestinationDialog: () -> Unit
) {
    val scope = rememberCoroutineScope()
    
    if (showStationSelectDialog) {
        val stationItems = stationOptions.keys.toList()
        val currentSelected = selectedStationItem.takeIf { it in stationItems } ?: stationItems.firstOrNull() ?: ""
        
        ListItemSelectDialog(
            isVisible = showStationSelectDialog,
            title = "駅を選択してください",
            items = stationItems,
            selectedItem = currentSelected,
            onItemSelect = onStationItemChange,
            onPositiveClick = {
                routeSearchManager.handleStationSelectPositiveClick(
                    scope,
                    selectedStationItem,
                    commonLoadingViewModel,
                    stationOptions,
                    onCurrentStationNameChange,
                    onDestinationOptionsChange,
                    onDialogDismiss,
                    onShowDestinationDialog
                )
            },
            onNegativeClick = {
                onDialogDismiss()
            },
            onDismiss = onDialogDismiss
        )
    }
}