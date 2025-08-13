package com.nyasai.traintimer.routesearch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Composeダイアログの使用例
 * 
 * 従来のDialogFragmentに代わって、ComposeベースのダイアログをActivityやFragmentから呼び出す例
 */

/**
 * SearchTargetInputDialog の使用例
 */
@Composable
fun SearchTargetInputDialogUsageExample() {
    var showDialog by remember { mutableStateOf(false) }
    val viewModel: SearchTargetInputViewModel = viewModel()
    
    // ダイアログ表示時のコールバック設定
    viewModel.onClickPositiveButtonCallback = {
        // 検索開始処理
        val stationName = viewModel.getStationName()
        // 実際の検索ロジックをここに記述
    }
    
    viewModel.onClickNegativeButtonCallback = {
        // キャンセル処理
        showDialog = false
    }

    if (showDialog) {
        SearchTargetInputDialogWithViewModel(
            isVisible = showDialog,
            onDismiss = { showDialog = false },
            viewModel = viewModel
        )
    }
}

/**
 * ListItemSelectDialog の使用例
 */
@Composable
fun ListItemSelectDialogUsageExample() {
    var showDialog by remember { mutableStateOf(false) }
    val viewModel: ListItemSelectViewModel = viewModel()
    
    // ダイアログ表示前にアイテムリストを設定
    val items = listOf("新宿駅", "渋谷駅", "東京駅", "池袋駅")
    viewModel.updateItems(items)
    
    // コールバック設定
    viewModel.onClickPositiveButtonCallback = {
        // 選択確定処理
        val selectedItem = viewModel.selectedItemState
        // 選択されたアイテムの処理をここに記述
    }
    
    viewModel.onClickNegativeButtonCallback = {
        // キャンセル処理
        showDialog = false
    }
    
    viewModel.onSelectItem = { item ->
        // アイテム選択時の処理
        // 必要に応じてUIの更新などを行う
    }

    if (showDialog) {
        ListItemSelectDialog(
            viewModel = viewModel,
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false }
        )
    }
}

/**
 * 従来のFragmentベースからCompose呼び出しへの移行ガイド
 * 
 * 従来:
 * ```
 * val dialog = SearchTargetInputDialogFragment()
 * dialog.show(supportFragmentManager, "search_input")
 * ```
 * 
 * Compose版:
 * ```
 * @Composable
 * fun MyScreen() {
 *     var showSearchDialog by remember { mutableStateOf(false) }
 *     
 *     // ダイアログ表示トリガー
 *     Button(onClick = { showSearchDialog = true }) {
 *         Text("検索")
 *     }
 *     
 *     // ダイアログ
 *     if (showSearchDialog) {
 *         SearchTargetInputDialogWithViewModel(
 *             isVisible = showSearchDialog,
 *             onDismiss = { showSearchDialog = false },
 *             viewModel = viewModel()
 *         )
 *     }
 * }
 * ```
 */