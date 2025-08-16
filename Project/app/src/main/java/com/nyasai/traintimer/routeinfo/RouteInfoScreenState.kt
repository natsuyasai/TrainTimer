package com.nyasai.traintimer.routeinfo

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDetail

/**
 * RouteInfoScreenの状態管理クラス
 * State Hoistingパターンに基づいて状態を分離
 */

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

/**
 * RouteInfoScreen用の状態ホルダー
 */
@Stable
class RouteInfoScreenState {
    var filterDialogState by mutableStateOf(FilterDialogState())
        private set
    
    var countdownState by mutableStateOf(CountdownState())
        private set
    
    var filterUpdateTrigger by mutableStateOf(0)
        private set
    
    // SnapshotStateListを使用してCompose最適化
    val displayRouteDetails: SnapshotStateList<RouteDetail> = mutableStateListOf()
    
    // フィルタダイアログアクション
    val filterDialogActions = object : FilterDialogActions {
        override fun showFilterDialog(filterItems: List<FilterInfo>) {
            filterDialogState = filterDialogState.copy(
                showFilterDialog = true,
                localFilterItems = filterItems.toList() // 不変コピーを作成
            )
        }
        
        override fun hideFilterDialog() {
            filterDialogState = filterDialogState.copy(showFilterDialog = false)
        }
        
        override fun updateLocalFilterItems(items: List<FilterInfo>) {
            filterDialogState = filterDialogState.copy(localFilterItems = items)
        }
    }
    
    // カウントダウンアクション
    val countdownActions = object : CountdownActions {
        override fun updateCountdown(text: String, info: String) {
            countdownState = countdownState.copy(
                countdownText = text,
                nextTimeInfo = info
            )
        }
        
        override fun startCountdown() {
            countdownState = countdownState.copy(isActive = true)
        }
        
        override fun stopCountdown() {
            countdownState = countdownState.copy(
                isActive = false,
                countdownText = "--:--",
                nextTimeInfo = ""
            )
        }
    }
    
    /**
     * フィルタ更新トリガーをインクリメント
     */
    fun incrementFilterUpdateTrigger() {
        filterUpdateTrigger++
    }
    
    /**
     * 表示用路線詳細リストを更新
     */
    fun updateDisplayRouteDetails(newDetails: List<RouteDetail>) {
        displayRouteDetails.clear()
        displayRouteDetails.addAll(newDetails)
    }
    
    /**
     * 表示用路線詳細リストをクリア
     */
    fun clearDisplayRouteDetails() {
        displayRouteDetails.clear()
    }
}

/**
 * 改善されたカウントダウン効果
 * 無限ループを適切に管理し、メモリリークを防ぐ
 */
@Composable
fun CountdownEffect(
    currentCountItem: RouteDetail?,
    onCountdownUpdate: (String, String) -> Unit,
    getDiffTimeSeconds: () -> Long,
    formatCountdownTime: (Long) -> String,
    buildNextTimeInfo: (RouteDetail) -> String
) {
    LaunchedEffect(currentCountItem) {
        if (currentCountItem != null) {
            // アクティブな状態でのみタイマーを実行
            while (currentCountItem != null) {
                val diffSeconds = getDiffTimeSeconds()
                val countdownText = formatCountdownTime(diffSeconds)
                val nextTimeInfo = buildNextTimeInfo(currentCountItem)
                onCountdownUpdate(countdownText, nextTimeInfo)
                
                kotlinx.coroutines.delay(1000) // 1秒待機
            }
        } else {
            // アイテムがnullの場合はクリア
            onCountdownUpdate("--:--", "")
        }
    }
}

/**
 * 改善された状態同期効果
 * 条件付きで状態を同期する
 */
@Composable
fun StateSyncEffect(
    sourceList: List<RouteDetail>,
    isDragging: Boolean,
    onLocalListUpdate: (List<RouteDetail>) -> Unit
) {
    LaunchedEffect(sourceList) {
        if (!isDragging) {
            onLocalListUpdate(sourceList)
        }
    }
}

/**
 * RouteInfoScreenStateを作成するComposable関数
 */
@Composable
fun rememberRouteInfoScreenState(): RouteInfoScreenState {
    return remember { RouteInfoScreenState() }
}