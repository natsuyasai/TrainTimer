package com.nyasai.traintimer.routesearch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchTargetInputViewModel : ViewModel() {

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: (() -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: (() -> Unit)? = null

    // 駅名 (既存のLiveData版)
    private val _stationName: MutableLiveData<String> = MutableLiveData()

    fun getStationName() = _stationName.value ?: ""

    fun setStationName(value: String) {
        _stationName.value = value
    }

    // 駅名 (Compose用の State版)
    var stationNameState by mutableStateOf("")
        private set

    fun updateStationName(value: String) {
        stationNameState = value
        _stationName.value = value // 既存のLiveDataとの同期
    }

    /**
     * 画面表示データクリア
     */
    fun clearUIData() {
        _stationName.value = ""
        stationNameState = ""
    }
}