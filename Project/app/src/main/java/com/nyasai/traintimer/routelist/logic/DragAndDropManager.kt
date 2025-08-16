package com.nyasai.traintimer.routelist.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.lazy.LazyListItemInfo
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.RouteListViewModel
import kotlin.math.abs

/**
 * ドラッグ&ドロップ機能を管理するクラス
 */
class DragAndDropManager(
    private val routeListViewModel: RouteListViewModel
) {
    
    /**
     * ドラッグ開始処理
     */
    fun handleDragStart(
        offset: Offset,
        listState: LazyListState,
        onItemFound: (LazyListItemInfo) -> Unit
    ) {
        listState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.y.toInt() in item.offset..(item.offset + item.size)
            }?.also { itemInfo ->
                onItemFound(itemInfo)
            }
    }
    
    /**
     * ドラッグ終了処理
     */
    fun handleDragEnd(
        initialDraggedIndex: Int?,
        currentDragOverIndex: Int?,
        localRouteList: List<RouteListItem>,
        updateLocalRouteList: (List<RouteListItem>) -> Unit
    ) {
        initialDraggedIndex?.let { fromIndex ->
            currentDragOverIndex?.let { toIndex ->
                if (fromIndex != toIndex) {
                    performSortUpdate(fromIndex, toIndex, localRouteList, updateLocalRouteList)
                }
            }
        }
    }
    
    /**
     * ドラッグ中の処理
     */
    fun handleDrag(
        dragAmount: Offset,
        initialDraggedIndex: Int?,
        listState: LazyListState,
        currentDraggedDistance: Float,
        onDragUpdate: (Float, Int?) -> Unit
    ) {
        val newDistance = currentDraggedDistance + dragAmount.y
        
        initialDraggedIndex?.let { draggedIndex ->
            val newDragOverIndex = calculateDragOverIndex(draggedIndex, newDistance, listState)
            onDragUpdate(newDistance, newDragOverIndex)
        } ?: run {
            onDragUpdate(newDistance, null)
        }
    }
    
    /**
     * ドラッグ状態リセット処理
     */
    fun resetDragState(resetAction: () -> Unit) {
        resetAction()
    }
    
    /**
     * 並び替え実行処理
     */
    private fun performSortUpdate(
        fromIndex: Int,
        toIndex: Int,
        localRouteList: List<RouteListItem>,
        updateLocalRouteList: (List<RouteListItem>) -> Unit
    ) {
        // ローカルリストの並び替え
        val mutableList = localRouteList.toMutableList()
        val draggedItem = mutableList.removeAt(fromIndex)
        mutableList.add(toIndex, draggedItem)
        updateLocalRouteList(mutableList)
        
        // ViewModelに変更を通知
        routeListViewModel.updateSortIndex(fromIndex, toIndex)
    }
    
    /**
     * ドラッグ中のホバー対象インデックス計算
     */
    private fun calculateDragOverIndex(
        draggedIndex: Int,
        draggedDistance: Float,
        listState: LazyListState
    ): Int? {
        val draggedItem = listState.layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == draggedIndex }
        
        return draggedItem?.let { item ->
            val draggedItemCenter = item.offset + item.size / 2 + draggedDistance
            
            val targetItem = listState.layoutInfo.visibleItemsInfo
                .minByOrNull { targetItem ->
                    abs(
                        (targetItem.offset + targetItem.size / 2) - draggedItemCenter
                    )
                }
            
            targetItem?.index
        }
    }
}