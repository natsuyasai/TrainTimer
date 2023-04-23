@file:Suppress("SpellCheckingInspection")

package com.nyasai.traintimer.setting

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.nyasai.traintimer.R

/**
 * アプリケーション情報表示用ダイアログ
 */
class AppInfoDialogFragment : DialogFragment() {
    /**
     * ダイアログ生成
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.app_info_dialog_title)
                .setMessage("Twitter:natsuyasai7")
                .create()
        }!!
    }
}