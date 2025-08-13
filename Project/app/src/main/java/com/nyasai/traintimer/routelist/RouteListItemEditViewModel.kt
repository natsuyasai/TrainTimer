package com.nyasai.traintimer.routelist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RouteListItemEditViewModel : ViewModel() {

    /**
     * 編集種別
     */
    enum class EditType {
        None,
        Update,
        Delete
    }

    // 編集対象のデータID
    private var _targetDataId: Long? = null
    val targetDataId: Long? get() = _targetDataId

    // 選択されている編集種別
    var selectedEditType by mutableStateOf(EditType.Update)
        private set

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: ((type: EditType, dataId: Long?) -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: ((type: EditType, dataId: Long?) -> Unit)? = null

    /**
     * 編集種別を更新
     */
    fun updateEditType(editType: EditType) {
        selectedEditType = editType
    }

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
        onClickPositiveButtonCallback?.invoke(selectedEditType, _targetDataId)
    }

    /**
     * Negativeボタンの処理
     */
    fun onNegativeButtonClick() {
        onClickNegativeButtonCallback?.invoke(selectedEditType, _targetDataId)
    }

    /**
     * 画面表示データクリア
     */
    fun clearUIData() {
        selectedEditType = EditType.Update
        _targetDataId = null
    }
}