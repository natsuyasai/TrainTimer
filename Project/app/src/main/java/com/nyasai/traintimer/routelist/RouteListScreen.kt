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
import android.app.Application
import com.nyasai.traintimer.commonparts.LoadingState
import com.nyasai.traintimer.commonparts.loadingState
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.logic.DialogManager
import com.nyasai.traintimer.routelist.logic.DragAndDropManager
import com.nyasai.traintimer.routelist.logic.EditModeManager
import com.nyasai.traintimer.routelist.logic.RouteRegistrationManager
import com.nyasai.traintimer.routelist.logic.RouteSearchManager
import com.nyasai.traintimer.database.RouteDatabase
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
    loadingState: LoadingState = loadingState()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Database DAO取得
    val database = RouteDatabase.getInstance(context).routeDatabaseDao
    
    // State Hoisting: 完全にViewModelから分離した状態管理
    val screenState = rememberRouteListScreenState(
        database = database,
        application = context.applicationContext as Application
    )
    
    // マネージャーの初期化（remember内で安全に）
    val managers = remember {
        RouteListManagers(
            wakeLockManager = WakeLockManager(context),
            dialogManager = DialogManager(),
            screenState = screenState
        )
    }
    
    // State Holderの状態観察
    val routeList by screenState.routeList.observeAsState(emptyList())
    val isEditMode by screenState.isEditMode.observeAsState(false)
    
    // ローカル状態の同期
    LaunchedEffect(routeList) {
        if (!screenState.isDragging) {
            screenState.updateLocalRouteList(routeList)
        }
    }
    
    RouteListContent(
        routeList = screenState.localRouteList.toList(),
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
 * マネージャークラスの集約（State Holder版）
 */
@Stable
data class RouteListManagers(
    val wakeLockManager: WakeLockManager,
    val dialogManager: DialogManager,
    val screenState: RouteListScreenState
) {
    val dragAndDropManager = DragAndDropManager(screenState)
    val editModeManager = EditModeManager(screenState, wakeLockManager)
    val routeSearchManager = RouteSearchManager(screenState, wakeLockManager)
    val routeRegistrationManager = RouteRegistrationManager(screenState, wakeLockManager)
}

