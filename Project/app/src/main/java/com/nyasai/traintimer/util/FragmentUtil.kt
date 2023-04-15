package com.nyasai.traintimer.util

import androidx.fragment.app.FragmentManager

class FragmentUtil {

    companion object {
        /**
         * 前回分ダイアログ削除
         * @param tag 削除対象ダイアログタグ
         */
        fun deletePrevDialog(tag: String, parentFragmentManager: FragmentManager) {
            val prevDlg = parentFragmentManager.findFragmentByTag(tag)
            if (prevDlg != null) {
                parentFragmentManager.beginTransaction().remove(prevDlg).commit()
            }
            parentFragmentManager.beginTransaction().addToBackStack(null).commit()
        }
    }
}