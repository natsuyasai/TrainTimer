package com.nyasai.traintimer.routelist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.commonparts.LoadingState
import com.nyasai.traintimer.commonparts.loadingState
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.logic.DialogManager
import com.nyasai.traintimer.routelist.logic.DragAndDropManager
import com.nyasai.traintimer.routelist.logic.EditModeManager
import com.nyasai.traintimer.routelist.logic.RouteRegistrationManager
import com.nyasai.traintimer.routelist.logic.RouteSearchManager
import com.nyasai.traintimer.routelist.components.RouteListContent
import com.nyasai.traintimer.routelist.components.RouteListDialogs
import com.nyasai.traintimer.util.WakeLockManager

/**
 * リファクタリングされた路線一覧画面のComposeスクリーン
 * State Hoistingパターンを適用
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListScreenRefactored(
    onRouteItemClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    routeListViewModel: RouteListViewModel = viewModel(),
    loadingState: LoadingState = loadingState()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // State Hoisting: 状態を分離
    val screenState = rememberRouteListScreenState()
    
    // マネージャーの初期化（remember内で安全に）
    val managers = remember {
        RouteListManagers(
            wakeLockManager = WakeLockManager(context),
            dialogManager = DialogManager(),
            routeListViewModel = routeListViewModel
        )
    }
    
    // ViewModelの状態観察
    val routeList by routeListViewModel.routeList.observeAsState(emptyList())
    val isEditMode by routeListViewModel.isEditMode.observeAsState(false)
    
    // ローカル状態の同期
    LaunchedEffect(routeList) {
        if (!screenState.dragDropState.isDragging) {
            screenState.localRouteList = routeList
        }
    }
    
    RouteListContent(
        routeList = screenState.localRouteList,
        isEditMode = isEditMode,
        screenState = screenState,
        managers = managers,
        loadingState = loadingState,
        onRouteItemClick = onRouteItemClick,
        onSettingsClick = onSettingsClick,
        modifier = modifier
    )
    
    // ダイアログハンドラ群
    RouteListDialogs(
        screenState = screenState,
        managers = managers,
        loadingState = loadingState
    )
}

/**
 * マネージャークラスの集約
 */
@Stable
data class RouteListManagers(
    val wakeLockManager: WakeLockManager,
    val dialogManager: DialogManager,
    val routeListViewModel: RouteListViewModel
) {
    val dragAndDropManager = DragAndDropManager(routeListViewModel)
    val editModeManager = EditModeManager(routeListViewModel, wakeLockManager)
    val routeSearchManager = RouteSearchManager(routeListViewModel, wakeLockManager)
    val routeRegistrationManager = RouteRegistrationManager(routeListViewModel, wakeLockManager)
}

