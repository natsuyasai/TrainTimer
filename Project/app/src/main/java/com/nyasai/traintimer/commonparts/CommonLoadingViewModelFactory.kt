package com.nyasai.traintimer.commonparts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class CommonLoadingViewModelFactory : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CommonLoadingViewModel::class.java)) {
            return CommonLoadingViewModel() as T
        }
        throw IllegalArgumentException("不明なViewModelクラス")
    }
}