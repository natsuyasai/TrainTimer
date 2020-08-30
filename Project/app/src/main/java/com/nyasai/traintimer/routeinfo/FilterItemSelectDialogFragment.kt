package com.nyasai.traintimer.routeinfo

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.FilterInfo

/**
 * フィルタ対象選択ダイアログ
 */
class FilterItemSelectDialogFragment(items: List<FilterInfo>): DialogFragment() {
    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: (() -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: (() -> Unit)? = null

    // アイテム一覧
    var filterItemList: List<FilterInfo> = items
    private set

    /**
     * ダイアログ生成
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val typeList = mutableListOf<String>()
            val checkList = arrayListOf<Boolean>()
            for (item in filterItemList) {
                typeList.add(item.trainTypeAndDirection)
                checkList.add(item.isShow)
            }

            builder.setTitle(R.string.select_filter_message)
                .setMultiChoiceItems(typeList.toTypedArray(), checkList.toBooleanArray()) { _, i, isChecked ->
                    Log.d("Debug", "アイテム選択${filterItemList[i]} -> $isChecked")
                    filterItemList[i].isShow = isChecked
                }
                .setPositiveButton(
                    R.string.select_filter_yes
                ) { _, _ ->
                    Log.d("Debug", "Yes")
                    onClickPositiveButtonCallback?.invoke()
                }
                .setNegativeButton(
                    R.string.select_filter_no
                ) { _, _ ->
                    Log.d("Debug", "No")
                    onClickNegativeButtonCallback?.invoke()
                }
            builder.create()
        }!!
    }

}