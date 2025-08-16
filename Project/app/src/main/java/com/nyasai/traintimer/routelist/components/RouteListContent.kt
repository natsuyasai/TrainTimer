package com.nyasai.traintimer.routelist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.nyasai.traintimer.R
import com.nyasai.traintimer.commonparts.CommonLoadingCompose
import com.nyasai.traintimer.commonparts.LoadingState
import com.nyasai.traintimer.commonparts.loadingState
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.RouteListManagers
import com.nyasai.traintimer.routelist.RouteListScreenState

/**
 * 路線一覧画面のメインコンテンツComposable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListContent(
    routeList: List<RouteListItem>,
    isEditMode: Boolean,
    screenState: RouteListScreenState,
    managers: RouteListManagers,
    onRouteItemClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    loadingState: LoadingState = loadingState()
) {
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                RouteListTopBar(
                    isEditMode = isEditMode,
                    onAddClick = screenState.dialogActions::showSearchDialog,
                    onEditClick = managers.editModeManager::handleEditModeToggle,
                    onSettingsClick = onSettingsClick
                )
            }
        ) { paddingValues ->
            
            RouteListLazyColumn(
                routeList = routeList,
                isEditMode = isEditMode,
                screenState = screenState,
                managers = managers,
                onRouteItemClick = onRouteItemClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(colorResource(id = R.color.colorNormalBackground))
            )
        }
        
        // 共通ローディング
        CommonLoadingCompose(loadingState = loadingState)
    }
}