package com.nyasai.traintimer.routesearch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.routelist.RouteListItemEditDialogWithViewModel
import com.nyasai.traintimer.routelist.RouteListItemEditViewModel
import com.nyasai.traintimer.routelist.RouteListItemDeleteConfirmDialogWithViewModel
import com.nyasai.traintimer.routelist.RouteListItemDeleteConfirmViewModel
import com.nyasai.traintimer.routeinfo.FilterItemSelectDialogWithViewModel
import com.nyasai.traintimer.routeinfo.FilterItemSelectViewModel
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.setting.AppInfoDialogWithViewModel
import com.nyasai.traintimer.setting.AppInfoViewModel

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
 * RouteListItemEditDialog の使用例
 */
@Composable
fun RouteListItemEditDialogUsageExample() {
    var showDialog by remember { mutableStateOf(false) }
    val viewModel: RouteListItemEditViewModel = viewModel()
    val testDataId = 123L
    
    // コールバック設定
    viewModel.onClickPositiveButtonCallback = { editType, dataId ->
        // 選択確定処理
        when (editType) {
            RouteListItemEditViewModel.EditType.Update -> {
                // 更新処理をここに記述
            }
            RouteListItemEditViewModel.EditType.Delete -> {
                // 削除処理をここに記述
            }
            RouteListItemEditViewModel.EditType.None -> {
                // 何もしない
            }
        }
        showDialog = false
    }
    
    viewModel.onClickNegativeButtonCallback = { _, _ ->
        // キャンセル処理
        showDialog = false
    }

    if (showDialog) {
        RouteListItemEditDialogWithViewModel(
            isVisible = showDialog,
            targetDataId = testDataId,
            onDismiss = { showDialog = false },
            viewModel = viewModel
        )
    }
}

/**
 * RouteListItemDeleteConfirmDialog の使用例
 */
@Composable
fun RouteListItemDeleteConfirmDialogUsageExample() {
    var showDialog by remember { mutableStateOf(false) }
    val viewModel: RouteListItemDeleteConfirmViewModel = viewModel()
    val testDataId = 999L
    
    // コールバック設定
    viewModel.onClickPositiveButtonCallback = { dataId ->
        // 削除実行処理
        // dataId を使って削除処理をここに記述
        showDialog = false
    }
    
    viewModel.onClickNegativeButtonCallback = { _ ->
        // キャンセル処理
        showDialog = false
    }

    if (showDialog) {
        RouteListItemDeleteConfirmDialogWithViewModel(
            isVisible = showDialog,
            targetDataId = testDataId,
            onDismiss = { showDialog = false },
            viewModel = viewModel
        )
    }
}

/**
 * FilterItemSelectDialog の使用例
 */
@Composable
fun FilterItemSelectDialogUsageExample() {
    var showDialog by remember { mutableStateOf(false) }
    val viewModel: FilterItemSelectViewModel = viewModel()
    
    // ダイアログ表示前にフィルタアイテムを設定
    val filterItems = listOf(
        FilterInfo(1L, 100L, "普通 - 新宿", true),
        FilterInfo(2L, 100L, "快速 - 池袋", false),
        FilterInfo(3L, 100L, "特急 - 横浜", true)
    )
    viewModel.updateFilterItems(filterItems)
    
    // コールバック設定
    viewModel.onClickPositiveButtonCallback = {
        // フィルタ設定の保存処理
        // viewModel.filterItemsState を使って設定を保存
        showDialog = false
    }
    
    viewModel.onClickNegativeButtonCallback = {
        // キャンセル処理
        showDialog = false
    }

    if (showDialog) {
        FilterItemSelectDialogWithViewModel(
            isVisible = showDialog,
            onDismiss = { showDialog = false },
            viewModel = viewModel
        )
    }
}

/**
 * AppInfoDialog の使用例
 */
@Composable
fun AppInfoDialogUsageExample() {
    var showDialog by remember { mutableStateOf(false) }
    val viewModel: AppInfoViewModel = viewModel()
    
    // コールバック設定
    viewModel.onCloseCallback = {
        // 閉じる処理
        showDialog = false
    }

    if (showDialog) {
        AppInfoDialogWithViewModel(
            isVisible = showDialog,
            onDismiss = { showDialog = false },
            viewModel = viewModel
        )
    }
}

/**
 * 従来のFragmentベースからCompose呼び出しへの移行ガイド
 * 
 * SearchTargetInputDialog 従来版:
 * ```
 * val dialog = SearchTargetInputDialogFragment()
 * dialog.show(supportFragmentManager, "search_input")
 * ```
 * 
 * SearchTargetInputDialog Compose版:
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
 * 
 * RouteListItemEditDialog 従来版:
 * ```
 * val dialog = RouteListItemEditDialogFragment()
 * val bundle = Bundle()
 * bundle.putLong(Define.RouteListDeleteConfirmArgentDataId, item.dataId)
 * dialog.arguments = bundle
 * dialog.onClickPositiveButtonCallback = { editType, dataId ->
 *     // 処理
 * }
 * dialog.show(supportFragmentManager, "edit_dialog")
 * ```
 * 
 * RouteListItemEditDialog Compose版:
 * ```
 * @Composable
 * fun MyScreen() {
 *     var showEditDialog by remember { mutableStateOf(false) }
 *     val viewModel: RouteListItemEditViewModel = viewModel()
 *     
 *     // コールバック設定
 *     viewModel.onClickPositiveButtonCallback = { editType, dataId ->
 *         // 処理
 *     }
 *     
 *     // ダイアログ
 *     if (showEditDialog) {
 *         RouteListItemEditDialogWithViewModel(
 *             isVisible = showEditDialog,
 *             targetDataId = item.dataId,
 *             onDismiss = { showEditDialog = false },
 *             viewModel = viewModel
 *         )
 *     }
 * }
 * ```
 * 
 * RouteListItemDeleteConfirmDialog 従来版:
 * ```
 * val dialog = RouteListItemDeleteConfirmDialogFragment()
 * val bundle = Bundle()
 * bundle.putLong(Define.RouteListDeleteConfirmArgentDataId, item.dataId)
 * dialog.arguments = bundle
 * dialog.onClickPositiveButtonCallback = { dataId ->
 *     // 削除処理
 * }
 * dialog.show(supportFragmentManager, "delete_confirm")
 * ```
 * 
 * RouteListItemDeleteConfirmDialog Compose版:
 * ```
 * @Composable
 * fun MyScreen() {
 *     var showDeleteDialog by remember { mutableStateOf(false) }
 *     val viewModel: RouteListItemDeleteConfirmViewModel = viewModel()
 *     
 *     // コールバック設定
 *     viewModel.onClickPositiveButtonCallback = { dataId ->
 *         // 削除処理
 *     }
 *     
 *     // ダイアログ
 *     if (showDeleteDialog) {
 *         RouteListItemDeleteConfirmDialogWithViewModel(
 *             isVisible = showDeleteDialog,
 *             targetDataId = item.dataId,
 *             onDismiss = { showDeleteDialog = false },
 *             viewModel = viewModel
 *         )
 *     }
 * }
 * ```
 * 
 * FilterItemSelectDialog 従来版:
 * ```
 * val viewModel = ViewModelProvider(requireActivity())[FilterItemSelectViewModel::class.java]
 * viewModel.filterItemList = mutableListOf(/* フィルタアイテム */)
 * viewModel.onClickPositiveButtonCallback = {
 *     // フィルタ設定処理
 * }
 * val dialog = FilterItemSelectDialogFragment()
 * dialog.show(supportFragmentManager, "filter_select")
 * ```
 * 
 * FilterItemSelectDialog Compose版:
 * ```
 * @Composable
 * fun MyScreen() {
 *     var showFilterDialog by remember { mutableStateOf(false) }
 *     val viewModel: FilterItemSelectViewModel = viewModel()
 *     
 *     // フィルタアイテムを設定
 *     LaunchedEffect(Unit) {
 *         viewModel.updateFilterItems(filterItems)
 *     }
 *     
 *     // コールバック設定
 *     viewModel.onClickPositiveButtonCallback = {
 *         // フィルタ設定処理
 *         // viewModel.filterItemsState を使用
 *     }
 *     
 *     // ダイアログ
 *     if (showFilterDialog) {
 *         FilterItemSelectDialogWithViewModel(
 *             isVisible = showFilterDialog,
 *             onDismiss = { showFilterDialog = false },
 *             viewModel = viewModel
 *         )
 *     }
 * }
 * ```
 * 
 * AppInfoDialog 従来版:
 * ```
 * val dialog = AppInfoDialogFragment()
 * dialog.show(supportFragmentManager, "app_info")
 * ```
 * 
 * AppInfoDialog Compose版:
 * ```
 * @Composable
 * fun MyScreen() {
 *     var showAppInfoDialog by remember { mutableStateOf(false) }
 *     val viewModel: AppInfoViewModel = viewModel()
 *     
 *     // コールバック設定
 *     viewModel.onCloseCallback = {
 *         // 閉じる処理
 *     }
 *     
 *     // ダイアログ
 *     if (showAppInfoDialog) {
 *         AppInfoDialogWithViewModel(
 *             isVisible = showAppInfoDialog,
 *             onDismiss = { showAppInfoDialog = false },
 *             viewModel = viewModel
 *         )
 *     }
 * }
 * ```
 */