package com.nyasai.traintimer.routelist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.R
import com.nyasai.traintimer.commonparts.CommonLoadingCompose
import com.nyasai.traintimer.commonparts.CommonLoadingViewModel
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routesearch.*
// import com.nyasai.traintimer.commonparts.RouteListItemCompose
import kotlinx.coroutines.launch

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
                            Icon(Icons.Default.MoreVert, contentDescription = "手動ソート")
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
                items(routeList) { item ->
                    // TODO: RouteListItemComposeの実装
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clickable {
                                if (!isManualSortMode) {
                                    onRouteItemClick(item.dataId)
                                } else {
                                    // 手動ソートモードでは編集ダイアログを表示
                                    selectedItem = item
                                    showEditDialog = true
                                }
                            }
                    ) {
                        Text(
                            text = "${item.routeName} - ${item.destination}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
        
        // 共通ローディング
        CommonLoadingCompose(viewModel = commonLoadingViewModel)
    }
    
    // ダイアログ群
    if (showSearchDialog) {
        SearchTargetInputDialogWithViewModel(
            isVisible = showSearchDialog,
            onDismiss = { showSearchDialog = false },
            viewModel = searchTargetInputViewModel.apply {
                onClickPositiveButtonCallback = {
                    scope.launch {
                        showSearchDialog = false
                        commonLoadingViewModel.showLoading()
                        
                        try {
                            val stationListMap = routeListViewModel.getStationList(getStationName())
                            if (stationListMap?.isNotEmpty() == true) {
                                stationOptions = stationListMap
                                listItemSelectViewModel.updateItems(stationListMap.keys.toList())
                                showStationSelectDialog = true
                            } else {
                                // 直接行先検索
                                val destinationListMap = routeListViewModel.getDestinationFromStationName(getStationName())
                                destinationOptions = destinationListMap
                                listItemSelectViewModel.updateItems(destinationListMap.keys.toList())
                                showDestinationSelectDialog = true
                            }
                        } catch (e: Exception) {
                            // エラーハンドリング
                        } finally {
                            commonLoadingViewModel.closeLoading()
                        }
                    }
                }
                onClickNegativeButtonCallback = {
                    showSearchDialog = false
                }
            }
        )
    }
    
    if (showStationSelectDialog) {
        // TODO: ListItemSelectDialogWithViewModelの実装
        /*
        ListItemSelectDialogWithViewModel(
            isVisible = showStationSelectDialog,
            onDismiss = { showStationSelectDialog = false },
            viewModel = listItemSelectViewModel.apply {
                onClickPositiveButtonCallback = {
                    scope.launch {
                        showStationSelectDialog = false
                        commonLoadingViewModel.showLoading()
                        
                        try {
                            val selectedStation = selectedItemState
                            val destinationListMap = routeListViewModel.getDestinationFromUrl(
                                stationOptions.getValue(selectedStation)
                            )
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
                onClickNegativeButtonCallback = {
                    showStationSelectDialog = false
                }
            }
        )
        */
    }
    
    if (showDestinationSelectDialog) {
        // TODO: ListItemSelectDialogWithViewModelの実装
        /*
        ListItemSelectDialogWithViewModel(
            isVisible = showDestinationSelectDialog,
            onDismiss = { showDestinationSelectDialog = false },
            viewModel = listItemSelectViewModel.apply {
                onClickPositiveButtonCallback = {
                    scope.launch {
                        showDestinationSelectDialog = false
                        commonLoadingViewModel.showLoading("時刻情報取得中")
                        
                        try {
                            val selectedDestination = selectedItemState
                            val url = destinationOptions.getValue(selectedDestination)
                            
                            // 路線情報を追加
                            val routeInfo = routeListViewModel.getTimeTableInfo(
                                url,
                                { commonLoadingViewModel.incrementMaxCountFromBackgroundTask(it) },
                                { commonLoadingViewModel.incrementCurrentCountFromBackgroundTask(1) }
                            )
                            
                            commonLoadingViewModel.changeText("時刻情報登録中")
                            
                            // 新しい路線アイテムを作成
                            val newRouteListItem = RouteListItem().apply {
                                val splitDestinationKey = routeListViewModel.splitDestinationKey(selectedDestination)
                                routeName = splitDestinationKey.first
                                destination = splitDestinationKey.second
                                stationName = searchTargetInputViewModel.getStationName()
                            }
                            
                            val parentDataId = routeListViewModel.registerRouteListItem(routeInfo, newRouteListItem)
                            routeListViewModel.registerRouteInfoDetailItems(routeInfo, parentDataId)
                            
                        } catch (e: Exception) {
                            // エラーハンドリング
                        } finally {
                            commonLoadingViewModel.closeLoading()
                        }
                    }
                }
                onClickNegativeButtonCallback = {
                    showDestinationSelectDialog = false
                }
            }
        )
        */
    }
    
    if (showEditDialog && selectedItem != null) {
        RouteListItemEditDialogWithViewModel(
            isVisible = showEditDialog,
            targetDataId = selectedItem!!.dataId,
            onDismiss = { showEditDialog = false },
            viewModel = routeListItemEditViewModel.apply {
                // targetDataId = selectedItem!!.dataId
                onClickPositiveButtonCallback = { editType, dataId ->
                    when (editType) {
                        RouteListItemEditViewModel.EditType.Update -> {
                            scope.launch {
                                commonLoadingViewModel.showLoading("時刻情報更新中")
                                try {
                                    routeListViewModel.updateRouteInfo(
                                        selectedItem!!,
                                        { commonLoadingViewModel.incrementMaxCountFromBackgroundTask(it) },
                                        { commonLoadingViewModel.incrementCurrentCountFromBackgroundTask(1) }
                                    )
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
                onClickNegativeButtonCallback = { _, _ ->
                    showEditDialog = false
                }
            }
        )
    }
    
    if (showDeleteConfirmDialog && selectedItem != null) {
        RouteListItemDeleteConfirmDialogWithViewModel(
            isVisible = showDeleteConfirmDialog,
            targetDataId = selectedItem!!.dataId,
            onDismiss = { showDeleteConfirmDialog = false },
            viewModel = routeListItemDeleteConfirmViewModel.apply {
                onClickPositiveButtonCallback = { dataId ->
                    dataId?.let { id ->
                        routeListViewModel.deleteListItem(id)
                    }
                    showDeleteConfirmDialog = false
                    selectedItem = null
                }
                onClickNegativeButtonCallback = {
                    showDeleteConfirmDialog = false
                }
            }
        )
    }
}