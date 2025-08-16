package com.nyasai.traintimer.routeinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.routeinfo.logic.InteractionManager
import com.nyasai.traintimer.routeinfo.logic.RouteDisplayManager
import com.nyasai.traintimer.routeinfo.logic.CountdownManager
import com.nyasai.traintimer.routeinfo.parts.FilterDialogHandler
import com.nyasai.traintimer.routeinfo.parts.RouteInfoItemCompose
import com.nyasai.traintimer.routeinfo.parts.RouteInfoTitleCompose
import com.nyasai.traintimer.util.YahooRouteInfoGetter

/**
 * リファクタリングされた路線詳細情報画面のComposeスクリーン
 * State Hoistingパターンと適切な副作用管理を適用
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteInfoScreenRefactored(
    parentDataId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // State Hoisting: 状態を分離
    val screenState = rememberRouteInfoScreenState()
    
    // マネージャーの初期化
    val managers = remember {
        RouteInfoManagers()
    }
    
    // ViewModelの作成と初期化
    val database = RouteDatabase.getInstance(context).routeDatabaseDao
    val factory = RouteInfoViewModelFactory(
        database, 
        context.applicationContext as android.app.Application, 
        parentDataId
    )
    val routeInfoViewModel: RouteInfoViewModel = viewModel(factory = factory)
    
    // ViewModelの状態観察
    val routeInfo by routeInfoViewModel.routeInfo.observeAsState()
    val currentDiagramType by routeInfoViewModel.currentDiagramType.observeAsState(
        YahooRouteInfoGetter.Companion.DiagramType.Weekday
    )
    val currentCountItem by routeInfoViewModel.currentCountItem.observeAsState()
    val routeItems by routeInfoViewModel.routeItems.observeAsState(emptyList())
    val filterInfo by routeInfoViewModel.filterInfo.observeAsState(emptyList())
    
    // 初期化処理
    LaunchedEffect(parentDataId) {
        routeInfoViewModel.initializeAsync()
    }
    
    // 改善されたカウントダウン効果
    CountdownEffect(
        currentCountItem = currentCountItem,
        onCountdownUpdate = screenState.countdownActions::updateCountdown,
        getDiffTimeSeconds = { routeInfoViewModel.getNextDiffTime() },
        formatCountdownTime = { managers.countdownManager.formatCountdownTime(it) },
        buildNextTimeInfo = { managers.countdownManager.buildNextTimeInfo(it) }
    )
    
    // LazyListStateをrememberで作成
    val updateListState = rememberLazyListState()
    
    // 表示リストの更新ロジック
    LaunchedEffect(currentDiagramType, routeItems, filterInfo, screenState.filterUpdateTrigger) {
        managers.routeDisplayManager.updateDisplayRouteDetails(
            routeItems,
            routeInfoViewModel,
            screenState.displayRouteDetails,
            updateListState
        )
    }
    
    // 強制的な初期データロード
    LaunchedEffect(parentDataId, routeItems) {
        if (routeItems.isNotEmpty()) {
            routeInfoViewModel.clearDisplayCache()
            screenState.clearDisplayRouteDetails()
            screenState.updateDisplayRouteDetails(
                routeInfoViewModel.getDisplayRouteDetailItems(false)
            )
        }
    }
    
    RouteInfoContent(
        routeInfo = routeInfo,
        currentDiagramType = currentDiagramType,
        currentCountItem = currentCountItem,
        screenState = screenState,
        managers = managers,
        routeInfoViewModel = routeInfoViewModel,
        filterInfo = filterInfo,
        onBackClick = onBackClick,
        modifier = modifier
    )
    
    // フィルタダイアログ
    FilterDialogHandler(
        showFilterDialog = screenState.filterDialogState.showFilterDialog,
        localFilterItems = screenState.filterDialogState.localFilterItems,
        onFilterItemsChange = screenState.filterDialogActions::updateLocalFilterItems,
        onDialogDismiss = screenState.filterDialogActions::hideFilterDialog,
        routeInfoViewModel = routeInfoViewModel,
        onFilterUpdate = screenState::incrementFilterUpdateTrigger
    )
}

/**
 * マネージャークラスの集約
 */
@Stable
data class RouteInfoManagers(
    val countdownManager: CountdownManager = CountdownManager(),
    val routeDisplayManager: RouteDisplayManager = RouteDisplayManager(CountdownManager()),
    val interactionManager: InteractionManager = InteractionManager()
)

/**
 * メインコンテンツのComposable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RouteInfoContent(
    routeInfo: com.nyasai.traintimer.database.RouteListItem?,
    currentDiagramType: YahooRouteInfoGetter.Companion.DiagramType,
    currentCountItem: RouteDetail?,
    screenState: RouteInfoScreenState,
    managers: RouteInfoManagers,
    routeInfoViewModel: RouteInfoViewModel,
    filterInfo: List<com.nyasai.traintimer.database.FilterInfo>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                RouteInfoTopBar(
                    onBackClick = onBackClick,
                    onFilterClick = {
                        screenState.filterDialogActions.showFilterDialog(filterInfo)
                    }
                )
            }
        ) { paddingValues ->
            
            RouteInfoMainContent(
                routeInfo = routeInfo,
                currentDiagramType = currentDiagramType,
                currentCountItem = currentCountItem,
                screenState = screenState,
                managers = managers,
                routeInfoViewModel = routeInfoViewModel,
                listState = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(colorResource(id = R.color.colorNormalBackground))
            )
        }
    }
}

/**
 * トップバーのComposable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RouteInfoTopBar(
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    TopAppBar(
        title = { Text("路線詳細") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
            }
        },
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(Icons.Default.FilterAlt, contentDescription = "フィルタ")
            }
        }
    )
}

/**
 * メインコンテンツのComposable
 */
@Composable
private fun RouteInfoMainContent(
    routeInfo: com.nyasai.traintimer.database.RouteListItem?,
    currentDiagramType: YahooRouteInfoGetter.Companion.DiagramType,
    currentCountItem: RouteDetail?,
    screenState: RouteInfoScreenState,
    managers: RouteInfoManagers,
    routeInfoViewModel: RouteInfoViewModel,
    listState: androidx.compose.foundation.lazy.LazyListState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // タイトル部分
        routeInfo?.let { route ->
            RouteInfoTitleCompose(
                routeListItem = route,
                currentDiagramType = currentDiagramType,
                onTitleClick = {
                    managers.interactionManager.handleTitleClick(routeInfoViewModel) { 
                        screenState.incrementFilterUpdateTrigger() 
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            )
        }
        
        // カウントダウン表示部分
        CountdownDisplay(
            countdownState = screenState.countdownState,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        )
        
        // 路線詳細リスト
        RouteDetailsList(
            displayRouteDetails = screenState.displayRouteDetails,
            currentCountItem = currentCountItem,
            managers = managers,
            routeInfoViewModel = routeInfoViewModel,
            listState = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

/**
 * カウントダウン表示のComposable
 */
@Composable
private fun CountdownDisplay(
    countdownState: CountdownState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.background(colorResource(id = R.color.colorNormalBackground))
    ) {
        // 次の時刻情報
        Text(
            text = countdownState.nextTimeInfo,
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
            text = countdownState.countdownText,
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
}

/**
 * 路線詳細リストのComposable
 */
@Composable
private fun RouteDetailsList(
    displayRouteDetails: List<RouteDetail>,
    currentCountItem: RouteDetail?,
    managers: RouteInfoManagers,
    routeInfoViewModel: RouteInfoViewModel,
    listState: androidx.compose.foundation.lazy.LazyListState,
    modifier: Modifier = Modifier
) {
    if (displayRouteDetails.isEmpty()) {
        // データが空の場合の表示
        EmptyRouteDetailsView(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier,
            state = listState
        ) {
            itemsIndexed(
                items = displayRouteDetails,
                key = { _, item -> item.dataId }
            ) { _, routeDetail ->
                RouteInfoItemCompose(
                    routeDetail = routeDetail,
                    isSelected = routeDetail.dataId == currentCountItem?.dataId,
                    onItemClick = { selectedRouteDetail ->
                        managers.interactionManager.handleItemClick(
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

/**
 * 空データ時の表示Composable
 */
@Composable
private fun EmptyRouteDetailsView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
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
        }
    }
}