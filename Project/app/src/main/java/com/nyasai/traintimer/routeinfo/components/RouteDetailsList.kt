package com.nyasai.traintimer.routeinfo.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.routeinfo.RouteInfoManagers
import com.nyasai.traintimer.routeinfo.RouteInfoScreenState
import com.nyasai.traintimer.routeinfo.parts.RouteInfoItemCompose

/**
 * 路線詳細リストのComposable
 */
@Composable
fun RouteDetailsList(
    displayRouteDetails: List<RouteDetail>,
    currentCountItem: RouteDetail?,
    managers: RouteInfoManagers,
    screenState: RouteInfoScreenState,
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
                        managers.interactionManager.handleItemClickWithState(
                            screenState,
                            selectedRouteDetail
                        )
                    },
                    allRouteDetails = displayRouteDetails,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}