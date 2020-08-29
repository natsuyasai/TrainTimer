package com.nyasai.traintimer.routesearch

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.nyasai.traintimer.R

/**
 * リストアイテム選択ダイアログ
 */
class ListItemSelectDialogFragment(items: Array<String>): DialogFragment() {

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: (() -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: (() -> Unit)? = null

    // アイテム選択
    var onSelectItem:((item: String) -> Unit)? = null

    // アイテム一覧
    var itemList: Array<String> = items

    // 選択したアイテム
    var selectItem: String = when{
        itemList.isNotEmpty() -> itemList[0] else -> ""}
    private set

    /**
     * ダイアログ生成
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.select_station_message)
                .setSingleChoiceItems(itemList,0) { _, i ->
                    Log.d("Debug", "アイテム選択${itemList[i]}")
                    selectItem = itemList[i]
                    onSelectItem?.invoke(itemList[i])
                }
                .setPositiveButton(
                    R.string.select_station_yes
                ) { _, _ ->
                    Log.d("Debug", "Yes")
                    onClickPositiveButtonCallback?.invoke()
                }
                .setNegativeButton(
                    R.string.select_station_no
                ) { _, _ ->
                    Log.d("Debug", "No")
                    onClickNegativeButtonCallback?.invoke()
                }
            builder.create()
        }!!
    }

}