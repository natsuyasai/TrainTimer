package com.nyasai.traintimer.routelist.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nyasai.traintimer.routelist.parts.RouteListItemCompose
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.RouteListManagers
import com.nyasai.traintimer.routelist.RouteListScreenState

/**
 * ドラッグ対応アイテムのComposable
 */
@Composable
fun RouteListItemWithDragSupport(
    item: RouteListItem,
    index: Int,
    screenState: RouteListScreenState,
    isEditMode: Boolean,
    managers: RouteListManagers,
    onRouteItemClick: (Long) -> Unit
) {
    val isBeingDragged = screenState.dragDropState.isDragging && 
            screenState.dragDropState.initialDraggedIndex == index
    
    val itemModifier = Modifier
        .fillMaxWidth()
        .then(
            if (isBeingDragged) {
                Modifier
                    .zIndex(1f)
                    .graphicsLayer {
                        translationY = screenState.dragDropState.draggedDistance
                        scaleX = 1.05f
                        scaleY = 1.05f
                    }
                    .shadow(8.dp)
            } else {
                Modifier
            }
        )
        .clickable(enabled = !screenState.dragDropState.isDragging) {
            managers.dialogManager.handleRouteItemClick(
                isEditMode,
                item,
                onRouteItemClick,
                { screenState.updateSelectedItem(item) },
                { screenState.dialogActions.showEditDialog(item) }
            )
        }

    RouteListItemCompose(
        routeListItem = item,
        modifier = itemModifier
    )
}