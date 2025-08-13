package com.nyasai.traintimer.setting

import androidx.lifecycle.ViewModel

class AppInfoViewModel : ViewModel() {

    // アプリ情報
    val appTitle = "時刻表"
    val twitterInfo = "Twitter:natsuyasai7"

    // 閉じるボタン押下時コールバック
    var onCloseCallback: (() -> Unit)? = null

    /**
     * 閉じるボタンの処理
     */
    fun onCloseButtonClick() {
        onCloseCallback?.invoke()
    }
}