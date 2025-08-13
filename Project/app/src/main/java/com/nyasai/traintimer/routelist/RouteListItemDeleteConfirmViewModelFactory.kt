package com.nyasai.traintimer.routelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * RouteListItemDeleteConfirmViewModelのファクトリークラス
 */
class RouteListItemDeleteConfirmViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RouteListItemDeleteConfirmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RouteListItemDeleteConfirmViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}