package com.nyasai.traintimer.routesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchTargetInputViewModelFactory : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchTargetInputViewModel::class.java)) {
            return SearchTargetInputViewModel() as T
        }
        throw IllegalArgumentException("不明なViewModelクラス")
    }
}