package com.nyasai.traintimer.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * AppInfoViewModelのファクトリークラス
 */
class AppInfoViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppInfoViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}