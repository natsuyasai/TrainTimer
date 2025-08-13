package com.nyasai.traintimer.routelist

import androidx.lifecycle.ViewModel

class RouteListItemDeleteConfirmViewModel : ViewModel() {

    // 削除対象のデータID
    private var _targetDataId: Long? = null
    val targetDataId: Long? get() = _targetDataId

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: ((dataId: Long?) -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: ((dataId: Long?) -> Unit)? = null

    /**
     * データIDを設定
     */
    fun setTargetDataId(dataId: Long?) {
        _targetDataId = dataId
    }

    /**
     * Positiveボタンの処理
     */
    fun onPositiveButtonClick() {
        onClickPositiveButtonCallback?.invoke(_targetDataId)
    }

    /**
     * Negativeボタンの処理
     */
    fun onNegativeButtonClick() {
        onClickNegativeButtonCallback?.invoke(_targetDataId)
    }

    /**
     * 画面表示データクリア
     */
    fun clearUIData() {
        _targetDataId = null
    }
}