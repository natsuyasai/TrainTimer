package com.nyasai.traintimer.routeinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.routeinfo.logic.InteractionManager
import com.nyasai.traintimer.routeinfo.logic.RouteDisplayManager
import com.nyasai.traintimer.routeinfo.logic.CountdownManager
import com.nyasai.traintimer.routeinfo.parts.FilterItemSelectDialog
import com.nyasai.traintimer.routeinfo.parts.RouteInfoItemCompose
import com.nyasai.traintimer.routeinfo.parts.RouteInfoTitleCompose
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

/**
 * 路線詳細情報画面のComposeスクリーン
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteInfoScreen(
    parentDataId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // マネージャクラスの初期化
    val countdownManager = remember { CountdownManager() }
    val routeDisplayManager = remember { RouteDisplayManager(countdownManager) }
    val interactionManager = remember { InteractionManager() }
    
    // ViewModelをFactoryを使って作成し、parentDataIdを渡す
    val database = RouteDatabase.getInstance(context).routeDatabaseDao
    val factory = RouteInfoViewModelFactory(database, context.applicationContext as android.app.Application, parentDataId)
    val routeInfoViewModel: RouteInfoViewModel = viewModel(factory = factory)
    
    // ViewModelの状態を観察
    val routeInfo by routeInfoViewModel.routeInfo.observeAsState()
    val currentDiagramType by routeInfoViewModel.currentDiagramType.observeAsState(YahooRouteInfoGetter.Companion.DiagramType.Weekday)
    val currentCountItem by routeInfoViewModel.currentCountItem.observeAsState()
    
    // ダイアログの状態
    var showFilterDialog by remember { mutableStateOf(false) }
    
    // フィルタ用のローカル状態
    var localFilterItems by remember { mutableStateOf<List<com.nyasai.traintimer.database.FilterInfo>>(emptyList()) }
    
    // タイマー状態
    var countdownText by remember { mutableStateOf("--:--") }
    var nextTimeInfo by remember { mutableStateOf("") }
    
    // カウントダウンタイマー
    LaunchedEffect(currentCountItem) {
        while (true) {
            currentCountItem?.let { countItem ->
                val diffSeconds = routeInfoViewModel.getNextDiffTime()
                countdownText = countdownManager.formatCountdownTime(diffSeconds)
                nextTimeInfo = countdownManager.buildNextTimeInfo(countItem)
            } ?: run {
                countdownText = "--:--"
                nextTimeInfo = ""
            }
            kotlinx.coroutines.delay(1000) // 1秒ごとに更新
        }
    }
    
    // 表示用の路線詳細リスト
    val displayRouteDetails = remember { mutableStateListOf<RouteDetail>() }
    
    // LazyColumnのスクロール状態
    val listState = rememberLazyListState()
    
    // 路線アイテムデータの監視
    val routeItems by routeInfoViewModel.routeItems.observeAsState(emptyList())
    val filterInfo by routeInfoViewModel.filterInfo.observeAsState(emptyList())
    
    // 初期化処理
    LaunchedEffect(parentDataId) {
        routeInfoViewModel.initializeAsync()
    }
    
    // フィルター更新用のトリガー
    var filterUpdateTrigger by remember { mutableIntStateOf(0) }
    
    // 表示リストの更新ロジック（データが変更されたときに実行）
    LaunchedEffect(currentDiagramType, routeItems, filterInfo, filterUpdateTrigger) {
        routeDisplayManager.updateDisplayRouteDetails(
            routeItems,
            routeInfoViewModel,
            displayRouteDetails,
            listState
        )
    }
    
    // 強制的な初期データロード（parentDataIdが変更されたとき）
    LaunchedEffect(parentDataId, routeItems) {
        if (routeItems.isNotEmpty()) {
            routeInfoViewModel.clearDisplayCache()
            displayRouteDetails.clear()
            displayRouteDetails.addAll(routeInfoViewModel.getDisplayRouteDetailItems(false))
        }
    }
    
    
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("路線詳細") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            // フィルタ情報を更新してダイアログを表示
                            localFilterItems = filterInfo.toList()
                            showFilterDialog = true
                        }) {
                            Icon(
                                Icons.Default.FilterAlt,
                                contentDescription = "フィルタ"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(colorResource(id = R.color.colorNormalBackground))
            ) {
                // タイトル部分
                routeInfo?.let { route ->
                    RouteInfoTitleCompose(
                        routeListItem = route,
                        currentDiagramType = currentDiagramType,
                        onTitleClick = {
                            interactionManager.handleTitleClick(
                                routeInfoViewModel
                            ) { filterUpdateTrigger++ }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    )
                }
                
                // カウントダウン表示部分
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .background(colorResource(id = R.color.colorNormalBackground))
                ) {
                    // 次の時刻情報
                    Text(
                        text = nextTimeInfo,
                        color = colorResource(id = R.color.textColor),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .weight(0.3f)
                            .fillMaxHeight()
                            .wrapContentHeight(Alignment.CenterVertically)
                            .padding(start = 10.dp)
                    )
                    
                    // カウントダウン
                    Text(
                        text = countdownText,
                        color = colorResource(id = R.color.textColor),
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .weight(0.7f)
                            .fillMaxHeight()
                            .wrapContentHeight(Alignment.CenterVertically)
                            .padding(end = 10.dp)
                    )
                }
                
                // 路線詳細リスト
                if (displayRouteDetails.isEmpty()) {
                    // データが空の場合の表示
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "路線詳細データがありません",
                                color = colorResource(id = R.color.textGray),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "路線データ件数: ${routeItems.size}",
                                color = colorResource(id = R.color.textGray),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "フィルタ件数: ${filterInfo.size}",
                                color = colorResource(id = R.color.textGray),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "現在のダイヤ: $currentDiagramType",
                                color = colorResource(id = R.color.textGray),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "親データID: $parentDataId",
                                color = colorResource(id = R.color.textGray),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        state = listState
                    ) {
                        itemsIndexed(displayRouteDetails) { index, routeDetail ->
                            RouteInfoItemCompose(
                                routeDetail = routeDetail,
                                isSelected = routeDetail.dataId == currentCountItem?.dataId,
                                onItemClick = { selectedRouteDetail ->
                                    interactionManager.handleItemClick(
                                        routeInfoViewModel,
                                        selectedRouteDetail
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
    
    // フィルタダイアログ
    if (showFilterDialog) {
        FilterItemSelectDialog(
            isVisible = showFilterDialog,
            filterItems = localFilterItems,
            onItemToggle = { index ->
                if (index in 0 until localFilterItems.size) {
                    val mutableList = localFilterItems.toMutableList()
                    val item = mutableList[index]
                    // 新しいFilterInfoオブジェクトを作成して状態を変更
                    mutableList[index] = FilterInfo(
                        item.dataId,
                        item.parentDataId,
                        item.trainTypeAndDestination,
                        !item.isShow
                    )
                    localFilterItems = mutableList
                }
            },
            onPositiveClick = {
                // フィルタ情報を更新
                scope.launch {
                    withContext(Dispatchers.IO) {
                        routeInfoViewModel.updateFilterInfoListItem(localFilterItems)
                    }
                    filterUpdateTrigger++
                    showFilterDialog = false
                }
            },
            onNegativeClick = {
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

