package com.nyasai.traintimer.routeinfo.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.routeinfo.RouteInfoManagers
import com.nyasai.traintimer.routeinfo.RouteInfoScreenState
import com.nyasai.traintimer.routeinfo.parts.RouteInfoTitleCompose
import com.nyasai.traintimer.util.YahooRouteInfoGetter

/**
 * 路線詳細画面のメインコンテンツComposable
 */
@Composable
fun RouteInfoMainContent(
    routeInfo: com.nyasai.traintimer.database.RouteListItem?,
    currentDiagramType: YahooRouteInfoGetter.Companion.DiagramType,
    currentCountItem: RouteDetail?,
    screenState: RouteInfoScreenState,
    managers: RouteInfoManagers,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    Column(modifier = modifier) {
        // タイトル部分
        routeInfo?.let { route ->
            RouteInfoTitleCompose(
                routeListItem = route,
                currentDiagramType = currentDiagramType,
                onTitleClick = {
                    managers.interactionManager.handleTitleClickWithState(screenState) { 
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
            screenState = screenState,
            listState = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}