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
// import com.nyasai.traintimer.commonparts.RouteInfoItemCompose
// import com.nyasai.traintimer.commonparts.RouteInfoTitleCompose
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
    
    // 表示用の路線詳細リスト
    val displayRouteDetails = remember { mutableStateListOf<RouteDetail>() }
    
    // 初期化処理（簡略化）
    LaunchedEffect(parentDataId) {
        // TODO: 初期化ロジックの実装
        // routeInfoViewModel.initializeAsync()
    }
    
    LaunchedEffect(routeInfoViewModel) {
        // TODO: 表示リストの更新ロジック
        // displayRouteDetails.clear()
        // displayRouteDetails.addAll(routeInfoViewModel.getDisplayRouteDetailItems())
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
                                    // TODO: フィルタ項目の取得
                                    // val filterItems = routeInfoViewModel.getFilterInfoItemWithParentIdSync()
                                    // filterItemSelectViewModel.updateFilterItems(filterItems)
                                    showFilterDialog = true
                                } catch (e: Exception) {
                                    // エラーハンドリング
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
                    // TODO: RouteInfoTitleComposeの実装
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${route.routeName} - ${route.destination}",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(displayRouteDetails) { routeDetail ->
                        // TODO: RouteInfoItemComposeの実装
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${routeDetail.departureTime} - ${routeDetail.trainType}",
                                modifier = Modifier.padding(16.dp)
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
                            // TODO: フィルタ情報の更新
                            // routeInfoViewModel.updateFilterInfoListItem(filterItemsState)
                            // 表示リストを更新
                            // displayRouteDetails.clear()
                            // displayRouteDetails.addAll(routeInfoViewModel.getDisplayRouteDetailItems())
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
 * カウントダウン計算（簡略化版）
 */
private fun calculateCountdown(countItem: RouteDetail): String {
    // 実際のカウントダウン計算ロジックを実装
    // RouteInfoFragmentからロジックを移植
    return "--:--:--"
}

/**
 * 次の時刻情報構築（簡略化版）
 */
private fun buildNextTimeInfo(countItem: RouteDetail): String {
    return buildAnnotatedString {
        append("${countItem.departureTime ?: "--:--"}\n")
        append("${countItem.trainType ?: "--"}\n")
        append("${countItem.destination ?: "--"}")
    }.toString()
}