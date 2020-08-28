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

    // アイテム一覧
    var itemList: Array<String> = items

    /**
     * ダイアログ生成
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.select_station_message)
                .setSingleChoiceItems(itemList,0) { dialogInterface, i ->
                    Log.d("Debug", "アイテム選択${itemList[i]}")
                }
                .setPositiveButton(
                    R.string.select_station_yes
                ) { dialogInterface, id ->
                    Log.d("Debug", "Yes")
                    onClickPositiveButtonCallback?.invoke()
                }
                .setNegativeButton(
                    R.string.select_station_no
                ) { dialogInterface, id ->
                    Log.d("Debug", "No")
                    onClickNegativeButtonCallback?.invoke()
                }
            builder.create()
        }!!
    }

}