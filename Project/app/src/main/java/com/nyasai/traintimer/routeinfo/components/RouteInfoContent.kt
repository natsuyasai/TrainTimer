package com.nyasai.traintimer.routeinfo.components

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
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.routeinfo.RouteInfoManagers
import com.nyasai.traintimer.routeinfo.RouteInfoScreenState
import com.nyasai.traintimer.util.YahooRouteInfoGetter

/**
 * 路線詳細画面のメインコンテンツComposable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteInfoContent(
    routeInfo: com.nyasai.traintimer.database.RouteListItem?,
    currentDiagramType: YahooRouteInfoGetter.Companion.DiagramType,
    currentCountItem: RouteDetail?,
    screenState: RouteInfoScreenState,
    managers: RouteInfoManagers,
    filterInfo: List<com.nyasai.traintimer.database.FilterInfo>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(colorResource(id = R.color.colorNormalBackground))
            )
        }
    }
}