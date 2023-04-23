package com.nyasai.traintimer.routeinfo

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.nyasai.traintimer.R

/**
 * フィルタ対象選択ダイアログ
 */
class FilterItemSelectDialogFragment : DialogFragment() {

    // ViewModel
    private val _viewModel: FilterItemSelectViewModel by lazy {
        ViewModelProvider(requireActivity())[FilterItemSelectViewModel::class.java]
    }

    /**
     * ダイアログ生成
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val typeList = mutableListOf<String>()
            val checkList = arrayListOf<Boolean>()
            for (item in _viewModel.filterItemList) {
                typeList.add(item.trainTypeAndDestination)
                checkList.add(item.isShow)
            }

            builder.setTitle(R.string.select_filter_message)
                .setMultiChoiceItems(
                    typeList.toTypedArray(),
                    checkList.toBooleanArray()
                ) { _, i, isChecked ->
                    Log.d("Debug", "アイテム選択${_viewModel.filterItemList[i]} -> $isChecked")
                    _viewModel.filterItemList[i].isShow = isChecked
                }
                .setPositiveButton(
                    R.string.select_filter_yes
                ) { _, _ ->
                    Log.d("Debug", "Yes")
                    _viewModel.onClickPositiveButtonCallback?.invoke()
                }
                .setNegativeButton(
                    R.string.select_filter_no
                ) { _, _ ->
                    Log.d("Debug", "No")
                    _viewModel.onClickNegativeButtonCallback?.invoke()
                }
            builder.create()
        }!!
    }

}