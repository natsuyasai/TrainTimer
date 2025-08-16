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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
 * 路線一覧画面のComposeスクリーン
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListScreen(
    onRouteItemClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    routeListViewModel: RouteListViewModel = viewModel(),
    commonLoadingViewModel: CommonLoadingViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // WakeLockManagerの初期化
    val wakeLockManager = remember { WakeLockManager(context) }
    
    // DragAndDropManagerの初期化
    val dragAndDropManager = remember { DragAndDropManager(routeListViewModel) }
    
    // DialogManagerの初期化
    val dialogManager = remember { DialogManager() }
    
    // EditModeManagerの初期化
    val editModeManager = remember { EditModeManager(routeListViewModel, wakeLockManager) }
    
    // RouteSearchManagerの初期化
    val routeSearchManager = remember { RouteSearchManager(routeListViewModel, wakeLockManager) }
    
    // RouteRegistrationManagerの初期化
    val routeRegistrationManager = remember {
        RouteRegistrationManager(
            routeListViewModel,
            wakeLockManager
        )
    }
    
    // ViewModelの状態を観察
    val routeList by routeListViewModel.routeList.observeAsState(emptyList())
    val isEditMode by routeListViewModel.isEditMode.observeAsState(false)
    
    // 色更新用のリコンポジション強制フラグ
    var colorUpdateTrigger by remember { mutableIntStateOf(0) }
    
    // ダイアログの状態
    var showSearchDialog by remember { mutableStateOf(false) }
    var showStationSelectDialog by remember { mutableStateOf(false) }
    var showDestinationSelectDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showColorSelectDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<RouteListItem?>(null) }
    
    // ダイアログ用のデータ
    var stationOptions by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var destinationOptions by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var currentStationName by remember { mutableStateOf("") }
    var searchStationName by remember { mutableStateOf("") }
    var selectedStationItem by remember { mutableStateOf("") }
    var selectedDestinationItem by remember { mutableStateOf("") }
    
    // ドラッグ&ドロップ状態（AndroidX公式デモに基づくアプローチ）
    var isDragging by remember { mutableStateOf(false) }
    var draggedDistance by remember { mutableFloatStateOf(0f) }
    var initialDraggedIndex by remember { mutableStateOf<Int?>(null) }
    var currentDragOverIndex by remember { mutableStateOf<Int?>(null) }
    val listState = rememberLazyListState()
    
    // ローカル状態でリストを管理
    var localRouteList by remember { mutableStateOf(routeList) }
    
    // routeListが変更されたときにローカル状態を同期
    LaunchedEffect(routeList) {
        if (!isDragging) {
            localRouteList = routeList
        }
    }
    
    
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            if (isEditMode) "路線一覧 (編集モード)" else "路線一覧"
                        ) 
                    },
                    actions = {
                        // 路線追加ボタン
                        IconButton(onClick = { showSearchDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "路線追加")
                        }
                        
                        // 編集
                        IconButton(onClick = { 
                            editModeManager.handleEditModeToggle()
                        }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "編集"
                            )
                        }
                        
                        // 設定ボタン
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = "設定")
                        }
                    }
                )
            }
        ) { paddingValues ->
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(colorResource(id = R.color.colorNormalBackground))
                    .pointerInput(Unit) {
                        if (isEditMode) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { offset ->
                                    dragAndDropManager.handleDragStart(
                                        offset,
                                        listState
                                    ) { itemInfo ->
                                        isDragging = true
                                        initialDraggedIndex = itemInfo.index
                                        currentDragOverIndex = itemInfo.index
                                        draggedDistance = 0f
                                    }
                                },
                                onDragEnd = {
                                    dragAndDropManager.handleDragEnd(
                                        initialDraggedIndex,
                                        currentDragOverIndex,
                                        localRouteList
                                    ) { newList ->
                                        localRouteList = newList
                                    }
                                    
                                    dragAndDropManager.resetDragState {
                                        draggedDistance = 0f
                                        currentDragOverIndex = null
                                        initialDraggedIndex = null
                                        isDragging = false
                                    }
                                },
                                onDrag = { _, dragAmount ->
                                    dragAndDropManager.handleDrag(
                                        dragAmount,
                                        initialDraggedIndex,
                                        listState,
                                        draggedDistance
                                    ) { newDistance, newDragOverIndex ->
                                        draggedDistance = newDistance
                                        if (newDragOverIndex != currentDragOverIndex) {
                                            currentDragOverIndex = newDragOverIndex
                                        }
                                    }
                                }
                            )
                        }
                    },
                state = listState
            ) {
                itemsIndexed(
                    items = localRouteList,
                    key = { _, item -> "${item.dataId}_${item.displayColor}_$colorUpdateTrigger" }
                ) { index, item ->
                    val isBeingDragged = isDragging && initialDraggedIndex == index
                    
                    val itemModifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isBeingDragged) {
                                Modifier
                                    .zIndex(1f)
                                    .graphicsLayer {
                                        translationY = draggedDistance
                                        scaleX = 1.05f
                                        scaleY = 1.05f
                                    }
                                    .shadow(8.dp)
                            } else {
                                Modifier
                            }
                        )
                        .clickable(enabled = !isDragging) {
                            dialogManager.handleRouteItemClick(
                                isEditMode,
                                item,
                                onRouteItemClick,
                                { selectedItem = item },
                                { showEditDialog = true }
                            )
                        }

                    RouteListItemCompose(
                        routeListItem = item,
                        modifier = itemModifier
                    )
                }
            }
        }
        
        // 共通ローディング
        CommonLoadingCompose(viewModel = commonLoadingViewModel)
    }
    
    // ダイアログハンドラ群
    SearchDialogHandler(
        showSearchDialog = showSearchDialog,
        searchStationName = searchStationName,
        onStationNameChange = { searchStationName = it },
        onDialogDismiss = { showSearchDialog = false },
        routeSearchManager = routeSearchManager,
        commonLoadingViewModel = commonLoadingViewModel,
        onCurrentStationNameChange = { name -> currentStationName = name },
        onStationOptionsChange = { options -> stationOptions = options },
        onDestinationOptionsChange = { options -> destinationOptions = options },
        onShowStationDialog = { showStationSelectDialog = true },
        onShowDestinationDialog = { showDestinationSelectDialog = true }
    )
    
    StationSelectDialogHandler(
        showStationSelectDialog = showStationSelectDialog,
        stationOptions = stationOptions,
        selectedStationItem = selectedStationItem,
        onStationItemChange = { selectedStationItem = it },
        onDialogDismiss = { showStationSelectDialog = false },
        routeSearchManager = routeSearchManager,
        commonLoadingViewModel = commonLoadingViewModel,
        onCurrentStationNameChange = { station -> currentStationName = station },
        onDestinationOptionsChange = { options -> destinationOptions = options },
        onShowDestinationDialog = { showDestinationSelectDialog = true }
    )
    
    DestinationSelectDialogHandler(
        showDestinationSelectDialog = showDestinationSelectDialog,
        destinationOptions = destinationOptions,
        selectedDestinationItem = selectedDestinationItem,
        onDestinationItemChange = { selectedDestinationItem = it },
        onDialogDismiss = { showDestinationSelectDialog = false },
        routeRegistrationManager = routeRegistrationManager,
        commonLoadingViewModel = commonLoadingViewModel,
        currentStationName = currentStationName
    )
    
    EditDialogHandler(
        showEditDialog = showEditDialog,
        selectedItem = selectedItem,
        onDialogDismiss = { showEditDialog = false },
        editModeManager = editModeManager,
        commonLoadingViewModel = commonLoadingViewModel,
        onShowDeleteConfirmDialog = { showDeleteConfirmDialog = true },
        onShowColorSelectDialog = { showColorSelectDialog = true }
    )
    
    DeleteConfirmDialogHandler(
        showDeleteConfirmDialog = showDeleteConfirmDialog,
        selectedItem = selectedItem,
        onDialogDismiss = { showDeleteConfirmDialog = false },
        dialogManager = dialogManager,
        routeListViewModel = routeListViewModel,
        onSelectedItemClear = { selectedItem = null }
    )
    
    ColorSelectDialogHandler(
        showColorSelectDialog = showColorSelectDialog,
        selectedItem = selectedItem,
        onDialogDismiss = { showColorSelectDialog = false },
        editModeManager = editModeManager,
        onSelectedItemUpdate = { selectedItem = it },
        onColorUpdateTrigger = { colorUpdateTrigger++ }
    )
}




