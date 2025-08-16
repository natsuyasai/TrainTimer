package com.nyasai.traintimer.routeinfo

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import android.app.Application
import androidx.compose.ui.platform.LocalContext
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
 * 完全なState Hoistingパターンを適用し、ViewModelを削除
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteInfoScreenRefactored(
    parentDataId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Database DAOの取得
    val database = RouteDatabase.getInstance(context).routeDatabaseDao
    
    // State Hoisting: 分割されたマネージャーによる状態管理
    val screenState = rememberRouteInfoScreenState(
        database = database,
        application = context.applicationContext as Application,
        parentId = parentDataId
    )
    
    // マネージャーの初期化
    val managers = remember {
        RouteInfoManagers()
    }
    
    // State Holderの状態観察
    val routeInfo by screenState.routeInfo.observeAsState()
    val currentDiagramType by screenState.currentDiagramType.observeAsState(
        YahooRouteInfoGetter.Companion.DiagramType.Weekday
    )
    val currentCountItem by screenState.currentCountItem.observeAsState()
    val routeItems by screenState.routeItems.observeAsState(emptyList())
    val filterInfo by screenState.filterInfo.observeAsState(emptyList())
    
    // 初期化処理
    LaunchedEffect(parentDataId) {
        screenState.initializeAsync()
    }
    
    // State Holderのクリーンアップ
    DisposableEffect(screenState) {
        onDispose {
            screenState.onCleared()
        }
    }
    
    // 改善されたカウントダウン効果
    CountdownEffect(
        currentCountItem = currentCountItem,
        onCountdownUpdate = screenState.countdownActions::updateCountdown,
        getDiffTimeSeconds = { screenState.getNextDiffTime() },
        formatCountdownTime = { managers.countdownManager.formatCountdownTime(it) },
        buildNextTimeInfo = { managers.countdownManager.buildNextTimeInfo(it) }
    )
    
    // LazyListStateをrememberで作成
    val updateListState = rememberLazyListState()
    
    // 表示リストの更新ロジック
    LaunchedEffect(currentDiagramType, routeItems, filterInfo, screenState.filterUpdateTrigger) {
        managers.routeDisplayManager.updateDisplayRouteDetailsWithState(
            routeItems,
            screenState,
            screenState.displayRouteDetails,
            updateListState
        )
    }
    
    // 強制的な初期データロード
    LaunchedEffect(parentDataId, routeItems) {
        if (routeItems.isNotEmpty()) {
            screenState.clearDisplayCache()
            screenState.clearDisplayRouteDetails()
            screenState.updateDisplayRouteDetails(
                screenState.getDisplayRouteDetailItems(false)
            )
        }
    }
    
    RouteInfoContent(
        routeInfo = routeInfo,
        currentDiagramType = currentDiagramType,
        currentCountItem = currentCountItem,
        screenState = screenState,
        managers = managers,
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
        screenState = screenState,
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

