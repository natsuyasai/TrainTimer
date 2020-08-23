package com.nyasai.traintimer.routesearch

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.nyasai.traintimer.R

/**
 * 駅選択ダイアログ
 */
class StationSelectDialogFragment: DialogFragment() {

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: (() -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: (() -> Unit)? = null

    /**
     * ダイアログ生成
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.route_list_item_delete_confirm_message)
                .setPositiveButton(
                    R.string.route_list_item_delete_confirm_yes
                ) { dialogInterface, id ->
                    Log.d("Debug", "Yes")
                    onClickPositiveButtonCallback?.invoke()
                }
                .setNegativeButton(
                    R.string.route_list_item_delete_confirm_no
                ) { dialogInterface, id ->
                    Log.d("Debug", "No")
                    onClickNegativeButtonCallback?.invoke()
                }
            builder.create()
        }!!
    }

}