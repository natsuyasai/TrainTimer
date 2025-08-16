package com.nyasai.traintimer.routelist.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.nyasai.traintimer.commonparts.LoadingState
import com.nyasai.traintimer.commonparts.loadingState
import com.nyasai.traintimer.routelist.logic.RouteSearchManager
import com.nyasai.traintimer.routesearch.SearchTargetInputDialog

/**
 * 検索ダイアログの表示と処理を管理するComposable関数
 */
@Composable
fun SearchDialogHandler(
    showSearchDialog: Boolean,
    searchStationName: String,
    onStationNameChange: (String) -> Unit,
    onDialogDismiss: () -> Unit,
    routeSearchManager: RouteSearchManager,
    loadingState: LoadingState,
    onCurrentStationNameChange: (String) -> Unit,
    onStationOptionsChange: (Map<String, String>) -> Unit,
    onDestinationOptionsChange: (Map<String, String>) -> Unit,
    onShowStationDialog: () -> Unit,
    onShowDestinationDialog: () -> Unit
) {
    val scope = rememberCoroutineScope()
    
    if (showSearchDialog) {
        SearchTargetInputDialog(
            isVisible = showSearchDialog,
            stationName = searchStationName,
            onStationNameChange = onStationNameChange,
            onPositiveClick = {
                routeSearchManager.handleSearchDialogPositiveClick(
                    scope,
                    searchStationName,
                    loadingState,
                    onCurrentStationNameChange,
                    onStationOptionsChange,
                    onDestinationOptionsChange,
                    onDialogDismiss,
                    onShowStationDialog,
                    onShowDestinationDialog
                )
            },
            onNegativeClick = {
                onStationNameChange("")
                onDialogDismiss()
            },
            onDismiss = onDialogDismiss
        )
    }
}