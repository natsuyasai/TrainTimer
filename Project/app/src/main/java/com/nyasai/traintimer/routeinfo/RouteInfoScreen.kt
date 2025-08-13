package com.nyasai.traintimer.routeinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import com.nyasai.traintimer.commonparts.RouteInfoItemCompose
import com.nyasai.traintimer.commonparts.RouteInfoTitleCompose
import kotlinx.coroutines.launch
import java.util.*

/**
 * 路線詳細情報画面のComposeスクリーン
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteInfoScreen(
    parentDataId: Long,
    onBackClick: () -> Unit,
    routeInfoViewModel: RouteInfoViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // ViewModelの状態を観察
    val routeInfo by routeInfoViewModel.routeInfo.observeAsState()
    val currentDiagramType by routeInfoViewModel.currentDiagramType.observeAsState(YahooRouteInfoGetter.Companion.DiagramType.Weekday)
    val currentCountItem by routeInfoViewModel.currentCountItem.observeAsState()
    
    // ダイアログの状態
    var showFilterDialog by remember { mutableStateOf(false) }
    
    // フィルタ用のViewModel
    val filterItemSelectViewModel: FilterItemSelectViewModel = viewModel()
    
    // タイマー状態
    var countdownText by remember { mutableStateOf("--:--:--") }
    var nextTimeInfo by remember { mutableStateOf("") }
    
    // カウントダウンタイマー
    LaunchedEffect(currentCountItem) {
        while (true) {
            currentCountItem?.let { countItem ->
                val diffSeconds = routeInfoViewModel.getNextDiffTime()
                countdownText = formatCountdownTime(diffSeconds)
                nextTimeInfo = buildNextTimeInfo(countItem)
            } ?: run {
                countdownText = "--:--:--"
                nextTimeInfo = ""
            }
            kotlinx.coroutines.delay(1000) // 1秒ごとに更新
        }
    }
    
    // 表示用の路線詳細リスト
    val displayRouteDetails = remember { mutableStateListOf<RouteDetail>() }
    
    // 路線アイテムデータの監視
    val routeItems by routeInfoViewModel.routeItems.observeAsState(emptyList())
    val filterInfo by routeInfoViewModel.filterInfo.observeAsState(emptyList())
    
    // 初期化処理
    LaunchedEffect(parentDataId) {
        routeInfoViewModel.initializeAsync()
    }
    
    // 表示リストの更新ロジック（データが変更されたときに実行）
    LaunchedEffect(currentDiagramType, routeItems, filterInfo) {
        if (routeItems.isNotEmpty()) { // データがある場合のみ更新
            displayRouteDetails.clear()
            displayRouteDetails.addAll(routeInfoViewModel.getDisplayRouteDetailItems(false))
            // キャッシュクリアのためfalseを指定
            routeInfoViewModel.updateCurrentCountItem(false)
        }
    }
    
    // 強制的な初期データロード（LiveDataが初期化されてから）
    LaunchedEffect(routeItems) {
        if (routeItems.isNotEmpty() && displayRouteDetails.isEmpty()) {
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
                            Icon(Icons.Default.Settings, contentDescription = "フィルタ")
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
                            routeInfoViewModel.setNextDiagramType()
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
                            .padding(start = 5.dp)
                    )
                    
                    // カウントダウン
                    Text(
                        text = countdownText,
                        color = colorResource(id = R.color.textRed),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(0.7f)
                            .fillMaxHeight()
                            .wrapContentHeight(Alignment.CenterVertically)
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
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(displayRouteDetails) { routeDetail ->
                            RouteInfoItemCompose(
                                routeDetail = routeDetail,
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
                            // 表示リストを更新（キャッシュクリア）
                            displayRouteDetails.clear()
                            displayRouteDetails.addAll(routeInfoViewModel.getDisplayRouteDetailItems(false))
                            // カウントアイテムも更新
                            routeInfoViewModel.updateCurrentCountItem(false)
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
 * カウントダウン時間をフォーマット
 */
private fun formatCountdownTime(diffSeconds: Long): String {
    return when {
        diffSeconds < 0 -> "--:--:--"
        diffSeconds < 60 -> "00:00:${String.format("%02d", diffSeconds)}"
        diffSeconds < 3600 -> {
            val minutes = diffSeconds / 60
            val seconds = diffSeconds % 60
            "00:${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
        }
        else -> {
            val hours = diffSeconds / 3600
            val minutes = (diffSeconds % 3600) / 60
            val seconds = diffSeconds % 60
            "${String.format("%02d", hours)}:${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
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