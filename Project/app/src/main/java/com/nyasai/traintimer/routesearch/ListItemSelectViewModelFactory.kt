package com.nyasai.traintimer.routesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ListItemSelectViewModelFactory : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListItemSelectViewModel::class.java)) {
            return ListItemSelectViewModel() as T
        }
        throw IllegalArgumentException("不明なViewModelクラス")
    }
}