package com.nyasai.traintimer.routelist.logic

import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.RouteListScreenState

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
        screenState: RouteListScreenState,
        hideDeleteDialog: () -> Unit,
        clearSelectedItem: () -> Unit
    ) {
        dataId?.let { id ->
            screenState.deleteListItem(id)
        }
        hideDeleteDialog()
        clearSelectedItem()
    }
}