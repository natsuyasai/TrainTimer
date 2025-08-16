package com.nyasai.traintimer.routeinfo

import androidx.compose.runtime.Stable
import com.nyasai.traintimer.database.FilterInfo

/**
 * フィルタダイアログの状態
 */
@Stable
data class FilterDialogState(
    val showFilterDialog: Boolean = false,
    val localFilterItems: List<FilterInfo> = emptyList()
)

/**
 * カウントダウンタイマーの状態
 */
@Stable
data class CountdownState(
    val countdownText: String = "--:--",
    val nextTimeInfo: String = "",
    val isActive: Boolean = false
)

/**
 * フィルタダイアログ操作用のアクション
 */
interface FilterDialogActions {
    fun showFilterDialog(filterItems: List<FilterInfo>)
    fun hideFilterDialog()
    fun updateLocalFilterItems(items: List<FilterInfo>)
}

/**
 * カウントダウン操作用のアクション
 */
interface CountdownActions {
    fun updateCountdown(text: String, info: String)
    fun startCountdown()
    fun stopCountdown()
}