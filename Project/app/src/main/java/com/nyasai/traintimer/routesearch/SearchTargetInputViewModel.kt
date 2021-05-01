package com.nyasai.traintimer.routesearch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchTargetInputViewModel : ViewModel() {

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: (() -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: (() -> Unit)? = null

    // 駅名
    private val _stationName: MutableLiveData<String> = MutableLiveData()

    fun getStationName() = _stationName.value ?: ""

    fun setStationName(value: String) {
        _stationName.value = value
    }

    /**
     * 画面表示データクリア
     */
    fun clearUIData() {
        _stationName.value = ""
    }
}