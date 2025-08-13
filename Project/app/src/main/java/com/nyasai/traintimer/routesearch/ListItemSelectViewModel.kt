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

    // 表示アイテム (既存のLiveData版)
    private val _items: MutableLiveData<Array<String>> = MutableLiveData()
    fun getItems() = _items.value ?: arrayOf()
    fun setItems(value: Array<String>) {
        _items.value = value
        itemsState = value.toList() // Compose版との同期
    }

    // 表示アイテム (Compose用の State版)
    var itemsState by mutableStateOf<List<String>>(emptyList())
        private set

    fun updateItems(value: List<String>) {
        itemsState = value
        _items.value = value.toTypedArray() // 既存のLiveDataとの同期
    }

    // 選択したアイテム (既存版)
    var selectItem: String = when {
        getItems().isNotEmpty() -> getItems()[0]
        else -> ""
    }

    // 選択したアイテム (Compose用の State版)
    var selectedItemState by mutableStateOf("")
        private set

    fun updateSelectedItem(value: String) {
        selectedItemState = value
        selectItem = value // 既存のプロパティとの同期
        onSelectItem?.invoke(value)
    }

    /**
     * 画面表示データクリア
     */
    fun clearUIData() {
        _items.value = arrayOf()
        itemsState = emptyList()
        selectedItemState = ""
    }
}