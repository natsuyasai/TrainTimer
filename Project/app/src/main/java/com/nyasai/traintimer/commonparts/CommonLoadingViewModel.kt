package com.nyasai.traintimer.commonparts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.locks.ReentrantLock

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

    // 最大件数
    private val _maxCount: MutableLiveData<Int> = MutableLiveData()
    var maxCount: LiveData<Int> = _maxCount

    // 現在件数
    private val _currentCount: MutableLiveData<Int> = MutableLiveData()
    var currentCount: LiveData<Int> = _currentCount

    // 排他用オブジェクト
    private val _maxCountLockObj = ReentrantLock()
    private val _currentCountLockObj = ReentrantLock()


    init {
        _isVisible.value = false
        _maxCount.value = 0
        _currentCount.value = 0
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
        _maxCount.value = 0
        _currentCount.value = 0
        setDefaultText()
    }

    /**
     * 最大件数更新
     */
    fun updateMaxCountFromBackgroundTask(count: Int) {
        exclusiveUpdate(_maxCountLockObj) { _maxCount.postValue(count) }
    }

    /**
     * 最大件数インクリメント
     */
    fun incrementMaxCountFromBackgroundTask(count: Int) {
        exclusiveUpdate(_maxCountLockObj) {
            _maxCount.postValue(if (maxCount.value != null) maxCount.value!! + count else count)
        }
    }

    /**
     * 現在件数インクリメント
     */
    fun incrementCurrentCountFromBackgroundTask(count: Int) {
        exclusiveUpdate(_currentCountLockObj) {
            _currentCount.postValue(if (currentCount.value != null) currentCount.value!! + count else count)
        }
    }

    /**
     * 表示中か
     */
    fun isVisible(): Boolean {
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

    /**
     * 排他更新
     */
    private fun exclusiveUpdate(lockObj: ReentrantLock, func: () -> Unit) {
        lockObj.lock()
        try {
            func()
        } finally {
            lockObj.unlock()
        }
    }
}