package com.nyasai.traintimer.routeinfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nyasai.traintimer.database.FilterInfo

class FilterItemSelectViewModel : ViewModel() {

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: (() -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: (() -> Unit)? = null

    // 表示アイテム (既存版 - 従来のFragment用)
    var filterItemList: MutableList<FilterInfo> = mutableListOf()

    // 表示アイテム (Compose用の State版)
    var filterItemsState by mutableStateOf<List<FilterInfo>>(emptyList())
        private set

    /**
     * フィルタアイテムリストを更新
     */
    fun updateFilterItems(items: List<FilterInfo>) {
        filterItemsState = items
        filterItemList.clear()
        filterItemList.addAll(items) // 既存のリストとの同期
    }

    /**
     * 特定のアイテムの表示状態を切り替え
     */
    fun toggleItemVisibility(index: Int) {
        if (index in filterItemsState.indices) {
            val updatedItems = filterItemsState.toMutableList()
            updatedItems[index] = updatedItems[index].copy(
                isShow = !updatedItems[index].isShow
            )
            filterItemsState = updatedItems
            
            // 既存のリストとも同期
            if (index in filterItemList.indices) {
                filterItemList[index].isShow = updatedItems[index].isShow
            }
        }
    }

    /**
     * Positiveボタンの処理
     */
    fun onPositiveButtonClick() {
        onClickPositiveButtonCallback?.invoke()
    }

    /**
     * Negativeボタンの処理
     */
    fun onNegativeButtonClick() {
        onClickNegativeButtonCallback?.invoke()
    }

    /**
     * 画面表示データクリア
     */
    fun clearUIData() {
        filterItemsState = emptyList()
        filterItemList.clear()
    }
}