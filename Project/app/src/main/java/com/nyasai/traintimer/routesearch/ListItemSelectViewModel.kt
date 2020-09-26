package com.nyasai.traintimer.routesearch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListItemSelectViewModel: ViewModel(){

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: (() -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: (() -> Unit)? = null

    // アイテム選択
    var onSelectItem:((item: String) -> Unit)? = null

    // 表示アイテム
    private val _items: MutableLiveData<Array<String>> = MutableLiveData()
    fun getItems() = _items.value ?: arrayOf()
    fun setItems(value: Array<String>) {
        _items.value = value
    }

    // 選択したアイテム
    var selectItem: String = when{
        getItems().isNotEmpty() -> getItems()[0]
        else -> ""
    }

    /**
     * 画面表示データクリア
     */
    fun clearUIData() {
        _items.value = arrayOf()
    }
}