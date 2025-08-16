package com.nyasai.traintimer.routelist.components

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.RouteListManagers
import com.nyasai.traintimer.routelist.RouteListScreenState

/**
 * 路線一覧のLazyColumnComposable（ドラッグ&ドロップ対応）
 */
@Composable
fun RouteListLazyColumn(
    routeList: List<RouteListItem>,
    isEditMode: Boolean,
    screenState: RouteListScreenState,
    managers: RouteListManagers,
    onRouteItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    LazyColumn(
        modifier = modifier.pointerInput(Unit) {
            if (isEditMode) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        managers.dragAndDropManager.handleDragStart(
                            offset,
                            listState
                        ) { itemInfo ->
                            screenState.dragDropActions.startDrag(itemInfo.index)
                        }
                    },
                    onDragEnd = {
                        managers.dragAndDropManager.handleDragEnd(
                            screenState.dragDropState.initialDraggedIndex,
                            screenState.dragDropState.currentDragOverIndex,
                            screenState.localRouteList
                        ) { newList ->
                            screenState.localRouteList = newList
                        }
                        
                        managers.dragAndDropManager.resetDragState {
                            screenState.dragDropActions.resetDragState()
                        }
                    },
                    onDrag = { _, dragAmount ->
                        managers.dragAndDropManager.handleDrag(
                            dragAmount,
                            screenState.dragDropState.initialDraggedIndex,
                            listState,
                            screenState.dragDropState.draggedDistance
                        ) { newDistance, newDragOverIndex ->
                            screenState.dragDropActions.updateDrag(newDistance, newDragOverIndex)
                        }
                    }
                )
            }
        },
        state = listState
    ) {
        itemsIndexed(
            items = routeList,
            key = { _, item -> 
                // パフォーマンス最適化: 安定したkeyを使用
                "${item.dataId}_${item.displayColor}"
            }
        ) { index, item ->
            RouteListItemWithDragSupport(
                item = item,
                index = index,
                screenState = screenState,
                isEditMode = isEditMode,
                managers = managers,
                onRouteItemClick = onRouteItemClick
            )
        }
    }
}