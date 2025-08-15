package com.nyasai.traintimer.routesearch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListItemSelectViewModel : ViewModel() {

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: (() -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: (() -> Unit)? = null

    // アイテム選択
    var onSelectItem: ((item: String) -> Unit)? = null

    // 表示アイテム (Compose用の State版)
    var itemsState by mutableStateOf<List<String>>(emptyList())
        private set

    fun updateItems(value: List<String>) {
        itemsState = value
    }


    // 選択したアイテム (Compose用の State版)
    var selectedItemState by mutableStateOf("")
        private set

    fun updateSelectedItem(value: String) {
        selectedItemState = value
        onSelectItem?.invoke(value)
    }

    /**
     * 画面表示データクリア
     */
    fun clearUIData() {
        itemsState = emptyList()
        selectedItemState = ""
    }
}