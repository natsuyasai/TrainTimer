package com.nyasai.traintimer.commonparts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * 共通ローディング用ViewModel
 */
class CommonLoadingViewModel : ViewModel() {

    // ローディング表示状態
    private val _isVisible: MutableLiveData<Boolean> = MutableLiveData()
    var isVisible: LiveData<Boolean> = _isVisible

    // テキスト
    private val _loadingText: MutableLiveData<String> = MutableLiveData()
    var loadingText: LiveData<String> = _loadingText


    init {
        _isVisible.value = false
        setDefaultText()
    }

    /**
     * ローディング表示
     */
    fun showLoading(text: String = "") {
        if (text.isNotBlank() && text.isNotEmpty()) {
            _loadingText.value = text
        } else {
            setDefaultText()
        }
        _isVisible.value = true
    }

    /**
     * ローディング終了
     */
    fun closeLoading() {
        _isVisible.value = false
        setDefaultText()
    }

    /**
     * 表示中か
     */
    fun isVisible() : Boolean {
        return _isVisible.value == true
    }

    /**
     * テキスト変更
     */
    fun changeText(text: String) {
        _loadingText.value = text
    }

    /**
     * デフォルトテキスト設定
     */
    private fun setDefaultText() {
        _loadingText.value = "読み込み中……"
    }

}