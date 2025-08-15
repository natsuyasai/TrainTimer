package com.nyasai.traintimer.routelist

import com.nyasai.traintimer.database.RouteListItem

/**
 * ダイアログ管理機能を管理するクラス
 */
class DialogManager {
    
    /**
     * 路線アイテムクリック処理
     */
    fun handleRouteItemClick(
        isEditMode: Boolean,
        item: RouteListItem,
        onRouteItemClick: (Long) -> Unit,
        setSelectedItem: () -> Unit,
        showEditDialog: () -> Unit
    ) {
        if (!isEditMode) {
            onRouteItemClick(item.dataId)
        } else {
            setSelectedItem()
            showEditDialog()
        }
    }
    
    /**
     * 削除確認ダイアログの肯定ボタンクリック処理
     */
    fun handleDeleteConfirmPositiveClick(
        dataId: Long?,
        routeListViewModel: RouteListViewModel,
        hideDeleteDialog: () -> Unit,
        clearSelectedItem: () -> Unit
    ) {
        dataId?.let { id ->
            routeListViewModel.deleteListItem(id)
        }
        hideDeleteDialog()
        clearSelectedItem()
    }
}