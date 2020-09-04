package com.nyasai.traintimer.routeinfo

import androidx.lifecycle.ViewModel
import com.nyasai.traintimer.database.FilterInfo

class FilterItemSelectViewModel: ViewModel(){

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: (() -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: (() -> Unit)? = null

    // 表示アイテム
    var filterItemList: MutableList<FilterInfo> = mutableListOf()

}