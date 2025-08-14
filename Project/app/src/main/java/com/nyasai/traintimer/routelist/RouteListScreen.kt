package com.nyasai.traintimer.routelist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
import com.nyasai.traintimer.routesearch.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    routeListViewModel: RouteListViewModel = viewModel(),
    commonLoadingViewModel: CommonLoadingViewModel = viewModel(),
    modifier: Modifier = Modifier
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
    var draggedDistance by remember { mutableStateOf(0f) }
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
                            routeListViewModel.switchEditMode()
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
                            if (!isEditMode) {
                                onRouteItemClick(item.dataId)
                            } else {
                                // 手動ソートモードでは編集ダイアログを表示
                                selectedItem = item
                                showEditDialog = true
                            }
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
            scope.launch {
                showSearchDialog = false
                commonLoadingViewModel.showLoading()
                
                try {
                    val stationName = searchTargetInputViewModel.getStationName()
                    currentStationName = stationName // 駅名を保存
                    
                    // IOディスパッチャーでネットワーク処理を実行
                    val (stationListMap, destinationListMap) = withContext(Dispatchers.IO) {
                        val stationList = routeListViewModel.getStationList(stationName)
                        val destinationList = if (stationList?.isEmpty() != false) {
                            routeListViewModel.getDestinationFromStationName(stationName)
                        } else {
                            emptyMap<String, String>()
                        }
                        Pair(stationList, destinationList)
                    }
                    
                    if (stationListMap?.isNotEmpty() == true) {
                        stationOptions = stationListMap
                        listItemSelectViewModel.updateItems(stationListMap.keys.toList())
                        showStationSelectDialog = true
                    } else {
                        destinationOptions = destinationListMap
                        listItemSelectViewModel.updateItems(destinationListMap.keys.toList())
                        showDestinationSelectDialog = true
                    }
                } catch (e: Exception) {
                    // エラーハンドリング
                } finally {
                    commonLoadingViewModel.closeLoading()
                    // 処理完了後にデータをクリア
                    searchTargetInputViewModel.clearUIData()
                }
            }
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
            scope.launch {
                showStationSelectDialog = false
                commonLoadingViewModel.showLoading()
                
                try {
                    val selectedStation = listItemSelectViewModel.selectedItemState
                    // 複数候補がある場合は、選択された情報を保持（○○（○○県）表記になる）
                    currentStationName = selectedStation
                    
                    // IOディスパッチャーでネットワーク処理を実行
                    val destinationListMap = withContext(Dispatchers.IO) {
                        routeListViewModel.getDestinationFromUrl(
                            stationOptions.getValue(selectedStation)
                        )
                    }
                    
                    destinationOptions = destinationListMap
                    listItemSelectViewModel.updateItems(destinationListMap.keys.toList())
                    showDestinationSelectDialog = true
                } catch (e: Exception) {
                    // エラーハンドリング
                } finally {
                    commonLoadingViewModel.closeLoading()
                }
            }
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
            scope.launch {
                showDestinationSelectDialog = false
                commonLoadingViewModel.showLoading("時刻情報取得中")
                
                try {
                    val selectedDestination = listItemSelectViewModel.selectedItemState
                    val url = destinationOptions.getValue(selectedDestination)
                    
                    // IOディスパッチャーでネットワーク処理を実行
                    val (routeInfo, parentDataId) = withContext(Dispatchers.IO) {
                        // 路線情報を取得
                        val routeInfo = routeListViewModel.getTimeTableInfo(
                            url,
                            { commonLoadingViewModel.incrementMaxCountFromBackgroundTask(it) },
                            { commonLoadingViewModel.incrementCurrentCountFromBackgroundTask(1) }
                        )
                        
                        // 新しい路線アイテムを作成
                        val newRouteListItem = RouteListItem().apply {
                            val splitDestinationKey = routeListViewModel.splitDestinationKey(selectedDestination)
                            routeName = splitDestinationKey.first
                            destination = splitDestinationKey.second
                            stationName = currentStationName
                        }
                        
                        val parentDataId = routeListViewModel.registerRouteListItem(routeInfo, newRouteListItem)
                        Pair(routeInfo, parentDataId)
                    }
                    
                    commonLoadingViewModel.changeText("時刻情報登録中")
                    
                    // データベース操作もIOディスパッチャーで実行
                    withContext(Dispatchers.IO) {
                        routeListViewModel.registerRouteInfoDetailItems(routeInfo, parentDataId)
                    }
                    
                } catch (e: Exception) {
                    // エラーハンドリング
                } finally {
                    commonLoadingViewModel.closeLoading()
                }
            }
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
            when (editType) {
                RouteListItemEditViewModel.EditType.Update -> {
                    scope.launch {
                        commonLoadingViewModel.showLoading("時刻情報更新中")
                        try {
                            // IOディスパッチャーでネットワーク処理を実行
                            withContext(Dispatchers.IO) {
                                routeListViewModel.updateRouteInfo(
                                    selectedItem!!,
                                    { commonLoadingViewModel.incrementMaxCountFromBackgroundTask(it) },
                                    { commonLoadingViewModel.incrementCurrentCountFromBackgroundTask(1) }
                                )
                            }
                        } catch (e: Exception) {
                            // エラーハンドリング
                        } finally {
                            commonLoadingViewModel.closeLoading()
                        }
                    }
                }
                RouteListItemEditViewModel.EditType.Delete -> {
                    showDeleteConfirmDialog = true
                }
                RouteListItemEditViewModel.EditType.None -> {
                    // 何もしない
                }
            }
            showEditDialog = false
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
            dataId?.let { id ->
                routeListViewModel.deleteListItem(id)
            }
            showDeleteConfirmDialog = false
            selectedItem = null
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

