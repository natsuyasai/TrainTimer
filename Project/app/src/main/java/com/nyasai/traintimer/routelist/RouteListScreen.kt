package com.nyasai.traintimer.routelist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.R
import com.nyasai.traintimer.commonparts.CommonLoadingCompose
import com.nyasai.traintimer.commonparts.CommonLoadingViewModel
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routesearch.*
import com.nyasai.traintimer.commonparts.RouteListItemCompose
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
    routeListViewModel: RouteListViewModel = viewModel(),
    commonLoadingViewModel: CommonLoadingViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // ViewModelの状態を観察
    val routeList by routeListViewModel.routeList.observeAsState(emptyList())
    val isManualSortMode by routeListViewModel.isManualSortMode.observeAsState(false)
    
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
    
    // ドラッグ&ドロップ状態
    var draggedItem by remember { mutableStateOf<RouteListItem?>(null) }
    var draggedIndex by remember { mutableStateOf(-1) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    
    // ViewModelインスタンス
    val searchTargetInputViewModel: SearchTargetInputViewModel = viewModel()
    val listItemSelectViewModel: ListItemSelectViewModel = viewModel()
    val routeListItemEditViewModel: RouteListItemEditViewModel = viewModel()
    val routeListItemDeleteConfirmViewModel: RouteListItemDeleteConfirmViewModel = viewModel()
    
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("路線一覧") },
                    actions = {
                        // 路線追加ボタン
                        IconButton(onClick = { showSearchDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "路線追加")
                        }
                        
                        // ソートボタン
                        IconButton(onClick = { 
                            routeListViewModel.switchManualSortMode()
                        }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "手動ソート"
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
                    .background(
                        if (isManualSortMode) 
                            colorResource(id = R.color.colorSortBackground)
                        else 
                            colorResource(id = R.color.colorNormalBackground)
                    ),
                state = rememberLazyListState()
            ) {
                itemsIndexed(routeList) { index, item ->
                    val isDragged = draggedItem == item
                    val itemModifier = if (isManualSortMode) {
                        Modifier
                            .fillMaxWidth()
                            .then(
                                if (isDragged) {
                                    Modifier
                                        .zIndex(1f)
                                        .graphicsLayer {
                                            translationX = dragOffset.x
                                            translationY = dragOffset.y
                                        }
                                        .shadow(8.dp)
                                } else {
                                    Modifier
                                }
                            )
                            .pointerInput(item.dataId) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        draggedItem = item
                                        draggedIndex = index
                                        dragOffset = Offset.Zero
                                    },
                                    onDragEnd = {
                                        // ドラッグ終了時の処理
                                        draggedItem?.let { draggedRouteItem ->
                                            val currentIndex = routeList.indexOf(draggedRouteItem)
                                            val targetIndex = calculateTargetIndex(dragOffset.y, routeList.size, currentIndex)
                                            
                                            if (currentIndex != targetIndex && targetIndex >= 0 && targetIndex < routeList.size) {
                                                routeListViewModel.updateSortIndex(currentIndex, targetIndex)
                                            }
                                        }
                                        
                                        // 状態リセット
                                        draggedItem = null
                                        draggedIndex = -1
                                        dragOffset = Offset.Zero
                                    },
                                    onDrag = { change, dragAmount ->
                                        dragOffset += dragAmount
                                    }
                                )
                            }
                            .clickable {
                                if (!isManualSortMode) {
                                    onRouteItemClick(item.dataId)
                                } else {
                                    // 手動ソートモードでは編集ダイアログを表示
                                    selectedItem = item
                                    showEditDialog = true
                                }
                            }
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onRouteItemClick(item.dataId)
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

/**
 * ドラッグオフセットから移動先インデックスを計算
 */
private fun calculateTargetIndex(dragOffsetY: Float, listSize: Int, currentIndex: Int): Int {
    val itemHeight = 80 // 大体のアイテム高さ (dp -> px変換は概算)
    val moveCount = (dragOffsetY / itemHeight).toInt()
    val targetIndex = currentIndex + moveCount
    
    return when {
        targetIndex < 0 -> 0
        targetIndex >= listSize -> listSize - 1
        else -> targetIndex
    }
}