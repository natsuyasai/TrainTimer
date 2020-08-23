package com.nyasai.traintimer.routesearch

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class SearchTargetInputViewModelFactory(private val application: Application): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SearchTargetInputViewModel::class.java)) {
            return SearchTargetInputViewModel(application) as T
        }
        throw IllegalArgumentException("不明なViewModelクラス")
    }
}