package com.nyasai.traintimer.routesearch

import android.app.Application
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SearchTargetInputViewModel(application: Application): AndroidViewModel(application){

    // 駅名
    private val _stationName: MutableLiveData<String> = MutableLiveData()

    fun getStationName() = _stationName.value ?: ""

    fun setStationName(value: String) {
        _stationName.value = value
    }
}