package com.nyasai.traintimer.routelist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.R
import com.nyasai.traintimer.commonparts.CommonLoadingCompose
import com.nyasai.traintimer.commonparts.CommonLoadingViewModel
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.logic.DialogManager
import com.nyasai.traintimer.routelist.logic.DragAndDropManager
import com.nyasai.traintimer.routelist.logic.EditModeManager
import com.nyasai.traintimer.routelist.logic.RouteRegistrationManager
import com.nyasai.traintimer.routelist.logic.RouteSearchManager
import com.nyasai.traintimer.routelist.dialogs.ColorSelectDialogHandler
import com.nyasai.traintimer.routelist.dialogs.DeleteConfirmDialogHandler
import com.nyasai.traintimer.routelist.dialogs.DestinationSelectDialogHandler
import com.nyasai.traintimer.routelist.dialogs.EditDialogHandler
import com.nyasai.traintimer.routelist.dialogs.SearchDialogHandler
import com.nyasai.traintimer.routelist.dialogs.StationSelectDialogHandler
import com.nyasai.traintimer.routelist.parts.RouteListItemCompose
import com.nyasai.traintimer.util.WakeLockManager

/**
 * リファクタリングされた路線一覧画面のComposeスクリーン
 * State Hoistingパターンを適用
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListScreenRefactored(
    onRouteItemClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    routeListViewModel: RouteListViewModel = viewModel(),
    commonLoadingViewModel: CommonLoadingViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // State Hoisting: 状態を分離
    val screenState = rememberRouteListScreenState()
    
    // マネージャーの初期化（remember内で安全に）
    val managers = remember {
        RouteListManagers(
            wakeLockManager = WakeLockManager(context),
            dialogManager = DialogManager(),
            routeListViewModel = routeListViewModel
        )
    }
    
    // ViewModelの状態観察
    val routeList by routeListViewModel.routeList.observeAsState(emptyList())
    val isEditMode by routeListViewModel.isEditMode.observeAsState(false)
    
    // ローカル状態の同期
    LaunchedEffect(routeList) {
        if (!screenState.dragDropState.isDragging) {
            screenState.localRouteList = routeList
        }
    }
    
    RouteListContent(
        routeList = screenState.localRouteList,
        isEditMode = isEditMode,
        screenState = screenState,
        managers = managers,
        commonLoadingViewModel = commonLoadingViewModel,
        onRouteItemClick = onRouteItemClick,
        onSettingsClick = onSettingsClick,
        modifier = modifier
    )
    
    // ダイアログハンドラ群
    RouteListDialogs(
        screenState = screenState,
        managers = managers,
        commonLoadingViewModel = commonLoadingViewModel
    )
}

/**
 * マネージャークラスの集約
 */
@Stable
data class RouteListManagers(
    val wakeLockManager: WakeLockManager,
    val dialogManager: DialogManager,
    val routeListViewModel: RouteListViewModel
) {
    val dragAndDropManager = DragAndDropManager(routeListViewModel)
    val editModeManager = EditModeManager(routeListViewModel, wakeLockManager)
    val routeSearchManager = RouteSearchManager(routeListViewModel, wakeLockManager)
    val routeRegistrationManager = RouteRegistrationManager(routeListViewModel, wakeLockManager)
}

/**
 * メインコンテンツのComposable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RouteListContent(
    routeList: List<RouteListItem>,
    isEditMode: Boolean,
    screenState: RouteListScreenState,
    managers: RouteListManagers,
    commonLoadingViewModel: CommonLoadingViewModel,
    onRouteItemClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                RouteListTopBar(
                    isEditMode = isEditMode,
                    onAddClick = screenState.dialogActions::showSearchDialog,
                    onEditClick = managers.editModeManager::handleEditModeToggle,
                    onSettingsClick = onSettingsClick
                )
            }
        ) { paddingValues ->
            
            RouteListLazyColumn(
                routeList = routeList,
                isEditMode = isEditMode,
                screenState = screenState,
                managers = managers,
                listState = listState,
                onRouteItemClick = onRouteItemClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(colorResource(id = R.color.colorNormalBackground))
            )
        }
        
        // 共通ローディング
        CommonLoadingCompose(viewModel = commonLoadingViewModel)
    }
}

/**
 * トップバーのComposable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RouteListTopBar(
    isEditMode: Boolean,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = { 
            Text(
                if (isEditMode) "路線一覧 (編集モード)" else "路線一覧"
            ) 
        },
        actions = {
            // 路線追加ボタン
            IconButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "路線追加")
            }
            
            // 編集ボタン
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "編集")
            }
            
            // 設定ボタン
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "設定")
            }
        }
    )
}

/**
 * LazyColumnのComposable（ドラッグ&ドロップ対応）
 */
@Composable
private fun RouteListLazyColumn(
    routeList: List<RouteListItem>,
    isEditMode: Boolean,
    screenState: RouteListScreenState,
    managers: RouteListManagers,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onRouteItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.pointerInput(Unit) {
            if (isEditMode) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        managers.dragAndDropManager.handleDragStart(
                            offset,
                            listState
                        ) { itemInfo ->
                            screenState.dragDropActions.startDrag(itemInfo.index)
                        }
                    },
                    onDragEnd = {
                        managers.dragAndDropManager.handleDragEnd(
                            screenState.dragDropState.initialDraggedIndex,
                            screenState.dragDropState.currentDragOverIndex,
                            screenState.localRouteList
                        ) { newList ->
                            screenState.localRouteList = newList
                        }
                        
                        managers.dragAndDropManager.resetDragState {
                            screenState.dragDropActions.resetDragState()
                        }
                    },
                    onDrag = { _, dragAmount ->
                        managers.dragAndDropManager.handleDrag(
                            dragAmount,
                            screenState.dragDropState.initialDraggedIndex,
                            listState,
                            screenState.dragDropState.draggedDistance
                        ) { newDistance, newDragOverIndex ->
                            screenState.dragDropActions.updateDrag(newDistance, newDragOverIndex)
                        }
                    }
                )
            }
        },
        state = listState
    ) {
        itemsIndexed(
            items = routeList,
            key = { _, item -> 
                // パフォーマンス最適化: 安定したkeyを使用
                "${item.dataId}_${item.displayColor}"
            }
        ) { index, item ->
            RouteListItemWithDragSupport(
                item = item,
                index = index,
                screenState = screenState,
                isEditMode = isEditMode,
                managers = managers,
                onRouteItemClick = onRouteItemClick
            )
        }
    }
}

/**
 * ドラッグ対応アイテムのComposable
 */
@Composable
private fun RouteListItemWithDragSupport(
    item: RouteListItem,
    index: Int,
    screenState: RouteListScreenState,
    isEditMode: Boolean,
    managers: RouteListManagers,
    onRouteItemClick: (Long) -> Unit
) {
    val isBeingDragged = screenState.dragDropState.isDragging && 
            screenState.dragDropState.initialDraggedIndex == index
    
    val itemModifier = Modifier
        .fillMaxWidth()
        .then(
            if (isBeingDragged) {
                Modifier
                    .zIndex(1f)
                    .graphicsLayer {
                        translationY = screenState.dragDropState.draggedDistance
                        scaleX = 1.05f
                        scaleY = 1.05f
                    }
                    .shadow(8.dp)
            } else {
                Modifier
            }
        )
        .clickable(enabled = !screenState.dragDropState.isDragging) {
            managers.dialogManager.handleRouteItemClick(
                isEditMode,
                item,
                onRouteItemClick,
                { screenState.updateSelectedItem(item) },
                { screenState.dialogActions.showEditDialog(item) }
            )
        }

    RouteListItemCompose(
        routeListItem = item,
        modifier = itemModifier
    )
}

/**
 * ダイアログハンドラ群のComposable
 */
@Composable
private fun RouteListDialogs(
    screenState: RouteListScreenState,
    managers: RouteListManagers,
    commonLoadingViewModel: CommonLoadingViewModel
) {
    SearchDialogHandler(
        showSearchDialog = screenState.dialogState.showSearchDialog,
        searchStationName = screenState.searchState.searchStationName,
        onStationNameChange = screenState.searchActions::updateSearchStationName,
        onDialogDismiss = screenState.dialogActions::hideSearchDialog,
        routeSearchManager = managers.routeSearchManager,
        commonLoadingViewModel = commonLoadingViewModel,
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
        commonLoadingViewModel = commonLoadingViewModel,
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
        commonLoadingViewModel = commonLoadingViewModel,
        currentStationName = screenState.searchState.currentStationName
    )
    
    EditDialogHandler(
        showEditDialog = screenState.dialogState.showEditDialog,
        selectedItem = screenState.dialogState.selectedItem,
        onDialogDismiss = screenState.dialogActions::hideEditDialog,
        editModeManager = managers.editModeManager,
        commonLoadingViewModel = commonLoadingViewModel,
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