package com.nyasai.traintimer.routelist.components

import androidx.compose.runtime.Composable
import com.nyasai.traintimer.commonparts.LoadingState
import com.nyasai.traintimer.routelist.RouteListManagers
import com.nyasai.traintimer.routelist.RouteListScreenState
import com.nyasai.traintimer.routelist.dialogs.ColorSelectDialogHandler
import com.nyasai.traintimer.routelist.dialogs.DeleteConfirmDialogHandler
import com.nyasai.traintimer.routelist.dialogs.DestinationSelectDialogHandler
import com.nyasai.traintimer.routelist.dialogs.EditDialogHandler
import com.nyasai.traintimer.routelist.dialogs.SearchDialogHandler
import com.nyasai.traintimer.routelist.dialogs.StationSelectDialogHandler

/**
 * 路線一覧画面のダイアログハンドラ群のComposable
 */
@Composable
fun RouteListDialogs(
    screenState: RouteListScreenState,
    managers: RouteListManagers,
    loadingState: LoadingState
) {
    SearchDialogHandler(
        showSearchDialog = screenState.dialogState.showSearchDialog,
        searchStationName = screenState.searchState.searchStationName,
        onStationNameChange = screenState.searchActions::updateSearchStationName,
        onDialogDismiss = screenState.dialogActions::hideSearchDialog,
        routeSearchManager = managers.routeSearchManager,
        loadingState = loadingState,
        onCurrentStationNameChange = screenState.searchActions::updateCurrentStationName,
        onStationOptionsChange = screenState.searchActions::updateStationOptions,
        onDestinationOptionsChange = screenState.searchActions::updateDestinationOptions,
        onShowStationDialog = screenState.dialogActions::showStationSelectDialog,
        onShowDestinationDialog = screenState.dialogActions::showDestinationSelectDialog
    )
    
    StationSelectDialogHandler(
        showStationSelectDialog = screenState.dialogState.showStationSelectDialog,
        stationOptions = screenState.searchState.stationOptions,
        selectedStationItem = screenState.searchState.selectedStationItem,
        onStationItemChange = screenState.searchActions::updateSelectedStationItem,
        onDialogDismiss = screenState.dialogActions::hideStationSelectDialog,
        routeSearchManager = managers.routeSearchManager,
        loadingState = loadingState,
        onCurrentStationNameChange = screenState.searchActions::updateCurrentStationName,
        onDestinationOptionsChange = screenState.searchActions::updateDestinationOptions,
        onShowDestinationDialog = screenState.dialogActions::showDestinationSelectDialog
    )
    
    DestinationSelectDialogHandler(
        showDestinationSelectDialog = screenState.dialogState.showDestinationSelectDialog,
        destinationOptions = screenState.searchState.destinationOptions,
        selectedDestinationItem = screenState.searchState.selectedDestinationItem,
        onDestinationItemChange = screenState.searchActions::updateSelectedDestinationItem,
        onDialogDismiss = screenState.dialogActions::hideDestinationSelectDialog,
        routeRegistrationManager = managers.routeRegistrationManager,
        loadingState = loadingState,
        currentStationName = screenState.searchState.currentStationName
    )
    
    EditDialogHandler(
        showEditDialog = screenState.dialogState.showEditDialog,
        selectedItem = screenState.dialogState.selectedItem,
        onDialogDismiss = screenState.dialogActions::hideEditDialog,
        editModeManager = managers.editModeManager,
        loadingState = loadingState,
        onShowDeleteConfirmDialog = screenState.dialogActions::showDeleteConfirmDialog,
        onShowColorSelectDialog = screenState.dialogActions::showColorSelectDialog
    )
    
    DeleteConfirmDialogHandler(
        showDeleteConfirmDialog = screenState.dialogState.showDeleteConfirmDialog,
        selectedItem = screenState.dialogState.selectedItem,
        onDialogDismiss = screenState.dialogActions::hideDeleteConfirmDialog,
        dialogManager = managers.dialogManager,
        routeListViewModel = managers.routeListViewModel,
        onSelectedItemClear = screenState.dialogActions::clearSelectedItem
    )
    
    ColorSelectDialogHandler(
        showColorSelectDialog = screenState.dialogState.showColorSelectDialog,
        selectedItem = screenState.dialogState.selectedItem,
        onDialogDismiss = screenState.dialogActions::hideColorSelectDialog,
        editModeManager = managers.editModeManager,
        screenState = screenState
    )
}