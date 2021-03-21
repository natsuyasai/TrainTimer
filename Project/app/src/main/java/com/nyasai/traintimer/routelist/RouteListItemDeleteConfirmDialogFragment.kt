package com.nyasai.traintimer.routelist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.nyasai.traintimer.R
import com.nyasai.traintimer.define.Define


/**
 * 路線リストアイテム削除確認ダイアログ
 */
class RouteListItemDeleteConfirmDialogFragment : DialogFragment() {

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: ((dataId: Long?) -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: ((dataId: Long?) -> Unit)? = null

    /**
     * ダイアログ生成
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val targetDataId = arguments?.getLong(Define.ROUTE_LIST_DELETE_CONFIRM_ARGMENT_DATAID)
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.route_list_item_delete_confirm_message)
                .setPositiveButton(R.string.route_list_item_delete_confirm_yes) { _, _ ->
                    Log.d("Debug", "Yes")
                    onClickPositiveButtonCallback?.invoke(targetDataId)
                }
                .setNegativeButton(R.string.route_list_item_delete_confirm_no) { _, _ ->
                    Log.d("Debug", "No")
                    onClickNegativeButtonCallback?.invoke(targetDataId)
                }
            builder.create()
        }!!
    }

}