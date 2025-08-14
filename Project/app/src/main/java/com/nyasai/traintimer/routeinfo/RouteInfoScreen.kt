package com.nyasai.traintimer.routeinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeParseException
import java.util.*

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
    
    // フィルタ用のViewModel
    val filterItemSelectViewModel: FilterItemSelectViewModel = viewModel()
    
    // タイマー状態
    var countdownText by remember { mutableStateOf("--:--") }
    var nextTimeInfo by remember { mutableStateOf("") }
    
    // カウントダウンタイマー
    LaunchedEffect(currentCountItem) {
        while (true) {
            currentCountItem?.let { countItem ->
                val diffSeconds = routeInfoViewModel.getNextDiffTime()
                countdownText = formatCountdownTime(diffSeconds)
                nextTimeInfo = buildNextTimeInfo(countItem)
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
    var filterUpdateTrigger by remember { mutableStateOf(0) }
    
    // 表示リストの更新ロジック（データが変更されたときに実行）
    LaunchedEffect(currentDiagramType, routeItems, filterInfo, filterUpdateTrigger) {
        // データが存在する場合のみ処理
        if (routeItems.isNotEmpty()) {
            // キャッシュをクリアして最新データを取得
            routeInfoViewModel.clearDisplayCache()
            val newDisplayItems = routeInfoViewModel.getDisplayRouteDetailItems(false)
            displayRouteDetails.clear()
            displayRouteDetails.addAll(newDisplayItems)
            routeInfoViewModel.updateCurrentCountItem(false)
            
            // 自動スクロール処理
            if (displayRouteDetails.isNotEmpty()) {
                val nextTrainIndex = findNextTrainIndex(displayRouteDetails)
                if (nextTrainIndex >= 0) {
                    listState.animateScrollToItem(nextTrainIndex)
                }
            }
        }
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
                            Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            scope.launch {
                                try {
                                    // 現在のフィルタ情報を取得してダイアログに設定
                                    val currentFilterItems = filterInfo
                                    if (currentFilterItems.isNotEmpty()) {
                                        filterItemSelectViewModel.updateFilterItems(currentFilterItems)
                                    } else {
                                        // フィルタ情報が空の場合は同期取得
                                        val syncFilterItems = routeInfoViewModel.getFilterInfoItemWithParentIdSync()
                                        filterItemSelectViewModel.updateFilterItems(syncFilterItems)
                                    }
                                    showFilterDialog = true
                                } catch (e: Exception) {
                                    // エラーハンドリング
                                    showFilterDialog = true // ダイアログは表示する
                                }
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_filter_alt_24px),
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
                            // ダイヤ種別を切り替え
                            routeInfoViewModel.setNextDiagramType()
                            // フィルター更新トリガーを増加させて表示を更新
                            filterUpdateTrigger++
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
                        fontSize = 40.sp,
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
                                text = "現在のダイヤ: ${currentDiagramType}",
                                color = colorResource(id = R.color.textGray),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "親データID: ${parentDataId}",
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
                                    routeInfoViewModel.setCurrentCountItem(selectedRouteDetail)
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
        FilterItemSelectDialogWithViewModel(
            isVisible = showFilterDialog,
            onDismiss = { showFilterDialog = false },
            viewModel = filterItemSelectViewModel.apply {
                onClickPositiveButtonCallback = {
                    scope.launch {
                        try {
                            routeInfoViewModel.updateFilterInfoListItem(filterItemSelectViewModel.filterItemsState)
                            // フィルター更新トリガーを増加させてLaunchedEffectを実行
                            filterUpdateTrigger++
                        } catch (e: Exception) {
                            // エラーハンドリング
                        }
                    }
                    showFilterDialog = false
                }
                onClickNegativeButtonCallback = {
                    showFilterDialog = false
                }
            }
        )
    }
}

/**
 * 現在時刻より先で最も近い電車のインデックスを取得
 */
private fun findNextTrainIndex(routeDetails: List<RouteDetail>): Int {
    val now = LocalTime.now()
    
    return routeDetails.indexOfFirst { routeDetail ->
        try {
            val departureTime = routeDetail.departureTime
            if (!departureTime.isNullOrEmpty()) {
                val trainTime = LocalTime.parse(departureTime)
                trainTime.isAfter(now)
            } else {
                false
            }
        } catch (e: DateTimeParseException) {
            false
        }
    }
}

/**
 * カウントダウン時間をMM:SS形式でフォーマット
 */
private fun formatCountdownTime(diffSeconds: Long): String {
    return when {
        diffSeconds < 0 -> "--:--"
        diffSeconds < 60 -> {
            val seconds = diffSeconds % 60
            "00:${String.format("%02d", seconds)}"
        }
        else -> {
            val minutes = diffSeconds / 60
            val seconds = diffSeconds % 60
            "${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
        }
    }
}

/**
 * 次の時刻情報構築
 */
private fun buildNextTimeInfo(countItem: RouteDetail): String {
    return buildString {
        append("${countItem.departureTime ?: "--:--"}\n")
        append("${countItem.trainType ?: "--"}\n")
        append("${countItem.destination ?: "--"}")
    }
}