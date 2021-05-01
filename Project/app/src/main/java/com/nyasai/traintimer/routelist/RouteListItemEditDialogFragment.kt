package com.nyasai.traintimer.routelist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.nyasai.traintimer.R
import com.nyasai.traintimer.define.Define

/**
 * 路線一覧アイテム編集ダイアログ
 * アイテムの更新/削除を選択させて実行する
 */
class RouteListItemEditDialogFragment : DialogFragment() {

    /**
     * 編集種別
     */
    enum class EditType {
        None,

        // 更新
        Update,

        // 削除
        Delete
    }

    private val Types: Array<String> = arrayOf("更新", "削除")

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: ((type: EditType, dataId: Long?) -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: ((type: EditType, dataId: Long?) -> Unit)? = null

    /**
     * ダイアログ生成
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val targetDataId = arguments?.getLong(Define.RouteListDeleteConfirmArgentDataId)
            val builder = AlertDialog.Builder(it)
            var selectItem = EditType.Update
            builder.setSingleChoiceItems(Types, 0) { _, which ->
                selectItem = when (which) {
                    0 -> EditType.Update
                    1 -> EditType.Delete
                    else -> EditType.None
                }
            }
                .setPositiveButton(R.string.route_list_item_edit_yes) { _, _ ->
                    Log.d("Debug", "Yes")
                    onClickPositiveButtonCallback?.invoke(selectItem, targetDataId)
                }
                .setNegativeButton(R.string.route_list_item_edit_no) { _, _ ->
                    Log.d("Debug", "No")
                    onClickNegativeButtonCallback?.invoke(selectItem, targetDataId)
                }
            builder.create()
        }!!
    }

}