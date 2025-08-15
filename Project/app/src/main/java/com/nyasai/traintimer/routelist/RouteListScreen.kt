package com.nyasai.traintimer.routelist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import com.nyasai.traintimer.routesearch.ListItemSelectDialogWithViewModel
import com.nyasai.traintimer.routesearch.ListItemSelectViewModel
import com.nyasai.traintimer.routesearch.SearchTargetInputDialogWithViewModel
import com.nyasai.traintimer.routesearch.SearchTargetInputViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    
    // ViewModelの状態を観察
    val routeList by routeListViewModel.routeList.observeAsState(emptyList())
    val isEditMode by routeListViewModel.isEditMode.observeAsState(false)
    
    // ダイアログの状態
    var showSearchDialog by remember { mutableStateOf(false) }
    var showStationSelectDialog by remember { mutableStateOf(false) }
    var showDestinationSelectDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<RouteListItem?>(null) }
    
    // ダイアログ用のデータ
    var stationOptions by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var destinationOptions by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var currentStationName by remember { mutableStateOf("") }
    
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
    
    // ViewModelインスタンス
    val searchTargetInputViewModel: SearchTargetInputViewModel = viewModel()
    val listItemSelectViewModel: ListItemSelectViewModel = viewModel()
    val routeListItemEditViewModel: RouteListItemEditViewModel = viewModel()
    val routeListItemDeleteConfirmViewModel: RouteListItemDeleteConfirmViewModel = viewModel()
    
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
                            handleEditModeToggle(routeListViewModel)
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
                            detectDragGestures(
                                onDragStart = { offset ->
                                    listState.layoutInfo.visibleItemsInfo
                                        .firstOrNull { item ->
                                            offset.y.toInt() in item.offset..(item.offset + item.size)
                                        }?.also { itemInfo ->
                                            isDragging = true
                                            initialDraggedIndex = itemInfo.index
                                            currentDragOverIndex = itemInfo.index
                                            draggedDistance = 0f
                                        }
                                },
                                onDragEnd = {
                                    // 並び替え処理の実行
                                    initialDraggedIndex?.let { fromIndex ->
                                        currentDragOverIndex?.let { toIndex ->
                                            if (fromIndex != toIndex) {
                                                // ローカルリストの並び替え
                                                val mutableList = localRouteList.toMutableList()
                                                val draggedItem = mutableList.removeAt(fromIndex)
                                                mutableList.add(toIndex, draggedItem)
                                                localRouteList = mutableList
                                                
                                                // ViewModelに変更を通知
                                                routeListViewModel.updateSortIndex(fromIndex, toIndex)
                                            }
                                        }
                                    }
                                    
                                    // 状態リセット
                                    draggedDistance = 0f
                                    currentDragOverIndex = null
                                    initialDraggedIndex = null
                                    isDragging = false
                                },
                                onDrag = { _, dragAmount ->
                                    draggedDistance += dragAmount.y
                                    
                                    // ドラッグ中のホバー対象を計算
                                    initialDraggedIndex?.let { draggedIndex ->
                                        val draggedItem = listState.layoutInfo.visibleItemsInfo
                                            .firstOrNull { it.index == draggedIndex }
                                        
                                        draggedItem?.let { item ->
                                            val draggedItemCenter = item.offset + item.size / 2 + draggedDistance
                                            
                                            val targetItem = listState.layoutInfo.visibleItemsInfo
                                                .minByOrNull { targetItem ->
                                                    kotlin.math.abs(
                                                        (targetItem.offset + targetItem.size / 2) - draggedItemCenter
                                                    )
                                                }
                                            
                                            targetItem?.let { target ->
                                                if (target.index != currentDragOverIndex) {
                                                    currentDragOverIndex = target.index
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    },
                state = listState
            ) {
                itemsIndexed(localRouteList) { index, item ->
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
                            handleRouteItemClick(
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
    
    // ダイアログ群
    if (showSearchDialog) {
        // コールバックを事前に設定
        searchTargetInputViewModel.onClickPositiveButtonCallback = {
            handleSearchDialogPositiveClick(
                scope,
                searchTargetInputViewModel,
                commonLoadingViewModel,
                routeListViewModel,
                listItemSelectViewModel,
                { name -> currentStationName = name },
                { options -> stationOptions = options },
                { options -> destinationOptions = options },
                { showSearchDialog = false },
                { showStationSelectDialog = true },
                { showDestinationSelectDialog = true }
            )
        }
        searchTargetInputViewModel.onClickNegativeButtonCallback = {
            showSearchDialog = false
        }
        
        SearchTargetInputDialogWithViewModel(
            isVisible = showSearchDialog,
            onDismiss = { showSearchDialog = false },
            viewModel = searchTargetInputViewModel
        )
    }
    
    if (showStationSelectDialog) {
        // コールバックを事前に設定
        listItemSelectViewModel.onClickPositiveButtonCallback = {
            handleStationSelectPositiveClick(
                scope,
                listItemSelectViewModel,
                commonLoadingViewModel,
                routeListViewModel,
                stationOptions,
                { station -> currentStationName = station },
                { options -> destinationOptions = options },
                { showStationSelectDialog = false },
                { showDestinationSelectDialog = true }
            )
        }
        listItemSelectViewModel.onClickNegativeButtonCallback = {
            showStationSelectDialog = false
        }
        
        ListItemSelectDialogWithViewModel(
            isVisible = showStationSelectDialog,
            onDismiss = { showStationSelectDialog = false },
            title = "駅を選択してください",
            viewModel = listItemSelectViewModel
        )
    }
    
    if (showDestinationSelectDialog) {
        // コールバックを事前に設定
        listItemSelectViewModel.onClickPositiveButtonCallback = {
            handleDestinationSelectPositiveClick(
                scope,
                listItemSelectViewModel,
                commonLoadingViewModel,
                routeListViewModel,
                destinationOptions,
                currentStationName
            ) { showDestinationSelectDialog = false }
        }
        listItemSelectViewModel.onClickNegativeButtonCallback = {
            showDestinationSelectDialog = false
        }
        
        ListItemSelectDialogWithViewModel(
            isVisible = showDestinationSelectDialog,
            onDismiss = { showDestinationSelectDialog = false },
            title = "行先を選択してください",
            viewModel = listItemSelectViewModel
        )
    }
    
    if (showEditDialog && selectedItem != null) {
        // コールバックを事前に設定
        routeListItemEditViewModel.onClickPositiveButtonCallback = { editType, dataId ->
            handleEditDialogPositiveClick(
                editType,
                scope,
                commonLoadingViewModel,
                routeListViewModel,
                selectedItem!!,
                { showDeleteConfirmDialog = true },
                { showEditDialog = false }
            )
        }
        routeListItemEditViewModel.onClickNegativeButtonCallback = { _, _ ->
            showEditDialog = false
        }
        
        RouteListItemEditDialogWithViewModel(
            isVisible = showEditDialog,
            targetDataId = selectedItem!!.dataId,
            onDismiss = { showEditDialog = false },
            viewModel = routeListItemEditViewModel
        )
    }
    
    if (showDeleteConfirmDialog && selectedItem != null) {
        // コールバックを事前に設定
        routeListItemDeleteConfirmViewModel.onClickPositiveButtonCallback = { dataId ->
            handleDeleteConfirmPositiveClick(
                dataId,
                routeListViewModel,
                { showDeleteConfirmDialog = false },
                { selectedItem = null }
            )
        }
        routeListItemDeleteConfirmViewModel.onClickNegativeButtonCallback = {
            showDeleteConfirmDialog = false
        }
        
        RouteListItemDeleteConfirmDialogWithViewModel(
            isVisible = showDeleteConfirmDialog,
            targetDataId = selectedItem!!.dataId,
            onDismiss = { showDeleteConfirmDialog = false },
            viewModel = routeListItemDeleteConfirmViewModel
        )
    }
}

/**
 * 検索ダイアログの肯定ボタンクリック処理
 */
private fun handleSearchDialogPositiveClick(
    scope: kotlinx.coroutines.CoroutineScope,
    searchViewModel: SearchTargetInputViewModel,
    loadingViewModel: CommonLoadingViewModel,
    routeListViewModel: RouteListViewModel,
    listSelectViewModel: ListItemSelectViewModel,
    setCurrentStationName: (String) -> Unit,
    setStationOptions: (Map<String, String>) -> Unit,
    setDestinationOptions: (Map<String, String>) -> Unit,
    hideSearchDialog: () -> Unit,
    showStationDialog: () -> Unit,
    showDestinationDialog: () -> Unit
) {
    scope.launch {
        hideSearchDialog()
        loadingViewModel.showLoading()
        
        try {
            val stationName = searchViewModel.getStationName()
            setCurrentStationName(stationName)
            
            val (stationListMap, destinationListMap) = withContext(Dispatchers.IO) {
                fetchStationAndDestinationData(routeListViewModel, stationName)
            }
            
            handleSearchResults(
                stationListMap,
                destinationListMap,
                listSelectViewModel,
                setStationOptions,
                setDestinationOptions,
                showStationDialog,
                showDestinationDialog
            )
        } catch (e: Exception) {
            // エラーハンドリング
        } finally {
            loadingViewModel.closeLoading()
            searchViewModel.clearUIData()
        }
    }
}

/**
 * 駅と目的地データの取得
 */
private fun fetchStationAndDestinationData(
    routeListViewModel: RouteListViewModel,
    stationName: String
): Pair<Map<String, String>?, Map<String, String>> {
    val stationList = routeListViewModel.getStationList(stationName)
    val destinationList = if (stationList?.isEmpty() != false) {
        routeListViewModel.getDestinationFromStationName(stationName)
    } else {
        emptyMap()
    }
    return Pair(stationList, destinationList)
}

/**
 * 検索結果の処理
 */
private fun handleSearchResults(
    stationListMap: Map<String, String>?,
    destinationListMap: Map<String, String>,
    listSelectViewModel: ListItemSelectViewModel,
    setStationOptions: (Map<String, String>) -> Unit,
    setDestinationOptions: (Map<String, String>) -> Unit,
    showStationDialog: () -> Unit,
    showDestinationDialog: () -> Unit
) {
    if (stationListMap?.isNotEmpty() == true) {
        setStationOptions(stationListMap)
        listSelectViewModel.updateItems(stationListMap.keys.toList())
        showStationDialog()
    } else {
        setDestinationOptions(destinationListMap)
        listSelectViewModel.updateItems(destinationListMap.keys.toList())
        showDestinationDialog()
    }
}

/**
 * 駅選択ダイアログの肯定ボタンクリック処理
 */
private fun handleStationSelectPositiveClick(
    scope: kotlinx.coroutines.CoroutineScope,
    listSelectViewModel: ListItemSelectViewModel,
    loadingViewModel: CommonLoadingViewModel,
    routeListViewModel: RouteListViewModel,
    stationOptions: Map<String, String>,
    setCurrentStationName: (String) -> Unit,
    setDestinationOptions: (Map<String, String>) -> Unit,
    hideStationDialog: () -> Unit,
    showDestinationDialog: () -> Unit
) {
    scope.launch {
        hideStationDialog()
        loadingViewModel.showLoading()
        
        try {
            val selectedStation = listSelectViewModel.selectedItemState
            setCurrentStationName(selectedStation)
            
            val destinationListMap = withContext(Dispatchers.IO) {
                routeListViewModel.getDestinationFromUrl(
                    stationOptions.getValue(selectedStation)
                )
            }
            
            setDestinationOptions(destinationListMap)
            listSelectViewModel.updateItems(destinationListMap.keys.toList())
            showDestinationDialog()
        } catch (e: Exception) {
            // エラーハンドリング
        } finally {
            loadingViewModel.closeLoading()
        }
    }
}

/**
 * 目的地選択ダイアログの肯定ボタンクリック処理
 */
private fun handleDestinationSelectPositiveClick(
    scope: kotlinx.coroutines.CoroutineScope,
    listSelectViewModel: ListItemSelectViewModel,
    loadingViewModel: CommonLoadingViewModel,
    routeListViewModel: RouteListViewModel,
    destinationOptions: Map<String, String>,
    currentStationName: String,
    hideDestinationDialog: () -> Unit
) {
    scope.launch {
        hideDestinationDialog()
        loadingViewModel.showLoading("時刻情報取得中")
        
        try {
            val selectedDestination = listSelectViewModel.selectedItemState
            val url = destinationOptions.getValue(selectedDestination)
            
            val (routeInfo, parentDataId) = withContext(Dispatchers.IO) {
                val routeInfo = routeListViewModel.getTimeTableInfo(
                    url,
                    { loadingViewModel.incrementMaxCountFromBackgroundTask(it) },
                    { loadingViewModel.incrementCurrentCountFromBackgroundTask(1) }
                )
                
                val newRouteListItem = RouteListItem().apply {
                    val splitDestinationKey = routeListViewModel.splitDestinationKey(selectedDestination)
                    routeName = splitDestinationKey.first
                    destination = splitDestinationKey.second
                    stationName = currentStationName
                }
                
                val parentDataId = routeListViewModel.registerRouteListItem(routeInfo, newRouteListItem)
                Pair(routeInfo, parentDataId)
            }
            
            loadingViewModel.changeText("時刻情報登録中")
            
            withContext(Dispatchers.IO) {
                routeListViewModel.registerRouteInfoDetailItems(routeInfo, parentDataId)
            }
        } catch (e: Exception) {
            // エラーハンドリング
        } finally {
            loadingViewModel.closeLoading()
        }
    }
}

/**
 * 編集ダイアログの肯定ボタンクリック処理
 */
private fun handleEditDialogPositiveClick(
    editType: RouteListItemEditViewModel.EditType,
    scope: kotlinx.coroutines.CoroutineScope,
    loadingViewModel: CommonLoadingViewModel,
    routeListViewModel: RouteListViewModel,
    selectedItem: RouteListItem,
    showDeleteDialog: () -> Unit,
    hideEditDialog: () -> Unit
) {
    when (editType) {
        RouteListItemEditViewModel.EditType.Update -> {
            scope.launch {
                loadingViewModel.showLoading("時刻情報更新中")
                try {
                    withContext(Dispatchers.IO) {
                        routeListViewModel.updateRouteInfo(
                            selectedItem,
                            { loadingViewModel.incrementMaxCountFromBackgroundTask(it) },
                            { loadingViewModel.incrementCurrentCountFromBackgroundTask(1) }
                        )
                    }
                } catch (e: Exception) {
                    // エラーハンドリング
                } finally {
                    loadingViewModel.closeLoading()
                }
            }
        }
        RouteListItemEditViewModel.EditType.Delete -> {
            showDeleteDialog()
        }
        RouteListItemEditViewModel.EditType.None -> {
            // 何もしない
        }
    }
    hideEditDialog()
}

/**
 * 削除確認ダイアログの肯定ボタンクリック処理
 */
private fun handleDeleteConfirmPositiveClick(
    dataId: Long?,
    routeListViewModel: RouteListViewModel,
    hideDeleteDialog: () -> Unit,
    clearSelectedItem: () -> Unit
) {
    dataId?.let { id ->
        routeListViewModel.deleteListItem(id)
    }
    hideDeleteDialog()
    clearSelectedItem()
}

/**
 * 路線アイテムクリック処理
 */
private fun handleRouteItemClick(
    isEditMode: Boolean,
    item: RouteListItem,
    onRouteItemClick: (Long) -> Unit,
    setSelectedItem: () -> Unit,
    showEditDialog: () -> Unit
) {
    if (!isEditMode) {
        onRouteItemClick(item.dataId)
    } else {
        setSelectedItem()
        showEditDialog()
    }
}

/**
 * 編集モード切り替え処理
 */
private fun handleEditModeToggle(
    routeListViewModel: RouteListViewModel
) {
    routeListViewModel.switchEditMode()
}

