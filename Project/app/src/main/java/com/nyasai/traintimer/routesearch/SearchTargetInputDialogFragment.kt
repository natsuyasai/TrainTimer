package com.nyasai.traintimer.routesearch

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import com.nyasai.traintimer.R

/**
 * 検索対象入力用ダイアログ
 */
class SearchTargetInputDialogFragment: DialogFragment() {

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
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.dialog_search_target_input, null))
                .setMessage(R.string.search_input_message)
                .setPositiveButton(
                    R.string.search_input_yes
                ) { dialogInterface, id ->
                    Log.d("Debug", "Yes")
                    onClickPositiveButtonCallback?.invoke()
                }
                .setNegativeButton(
                    R.string.search_input_no
                ) { dialogInterface, id ->
                    Log.d("Debug", "No")
                    onClickNegativeButtonCallback?.invoke()
                }
            builder.create()
        }!!
    }

}