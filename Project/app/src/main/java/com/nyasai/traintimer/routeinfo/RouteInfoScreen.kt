package com.nyasai.traintimer.routeinfo

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.routeinfo.components.RouteInfoContent
import com.nyasai.traintimer.routeinfo.logic.InteractionManager
import com.nyasai.traintimer.routeinfo.logic.RouteDisplayManager
import com.nyasai.traintimer.routeinfo.logic.CountdownManager
import com.nyasai.traintimer.routeinfo.parts.FilterDialogHandler
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

