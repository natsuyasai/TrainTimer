package com.nyasai.traintimer.routelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * RouteListItemEditViewModelのファクトリークラス
 */
class RouteListItemEditViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RouteListItemEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RouteListItemEditViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}