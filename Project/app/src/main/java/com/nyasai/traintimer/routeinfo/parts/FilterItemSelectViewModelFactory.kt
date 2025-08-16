package com.nyasai.traintimer.routeinfo.parts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * FilterItemSelectViewModelのファクトリークラス
 */
class FilterItemSelectViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilterItemSelectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FilterItemSelectViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}