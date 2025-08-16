package com.nyasai.traintimer.routelist.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.nyasai.traintimer.commonparts.CommonLoadingViewModel
import com.nyasai.traintimer.routelist.logic.RouteRegistrationManager
import com.nyasai.traintimer.routesearch.ListItemSelectDialog

/**
 * 行先選択ダイアログの表示と処理を管理するComposable関数
 */
@Composable
fun DestinationSelectDialogHandler(
    showDestinationSelectDialog: Boolean,
    destinationOptions: Map<String, String>,
    selectedDestinationItem: String,
    onDestinationItemChange: (String) -> Unit,
    onDialogDismiss: () -> Unit,
    routeRegistrationManager: RouteRegistrationManager,
    commonLoadingViewModel: CommonLoadingViewModel,
    currentStationName: String
) {
    val scope = rememberCoroutineScope()
    
    if (showDestinationSelectDialog) {
        val destinationItems = destinationOptions.keys.toList()
        val currentSelected = selectedDestinationItem.takeIf { it in destinationItems } ?: destinationItems.firstOrNull() ?: ""
        
        ListItemSelectDialog(
            isVisible = showDestinationSelectDialog,
            title = "行先を選択してください",
            items = destinationItems,
            selectedItem = currentSelected,
            onItemSelect = onDestinationItemChange,
            onPositiveClick = { selectedDestination ->
                routeRegistrationManager.handleDestinationSelectPositiveClick(
                    scope,
                    selectedDestination,
                    commonLoadingViewModel,
                    destinationOptions,
                    currentStationName
                ) { onDialogDismiss() }
            },
            onNegativeClick = {
                onDialogDismiss()
            },
            onDismiss = onDialogDismiss
        )
    }
}