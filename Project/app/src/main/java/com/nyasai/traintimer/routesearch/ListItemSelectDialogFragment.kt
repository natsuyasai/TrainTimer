package com.nyasai.traintimer.routesearch

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.nyasai.traintimer.R

/**
 * リストアイテム選択ダイアログ
 */
class ListItemSelectDialogFragment: DialogFragment() {

    // ViewModel
    private val _viewModel: ListItemSelectViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ListItemSelectViewModelFactory()
        ).get(ListItemSelectViewModel::class.java)
    }

    /**
     * ダイアログ生成
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.select_station_message)
                .setSingleChoiceItems(_viewModel.getItems(),-1) { _, i ->
                    Log.d("Debug", "アイテム選択${_viewModel.getItems()[i]}")
                    _viewModel.selectItem = _viewModel.getItems()[i]
                    _viewModel.onSelectItem?.invoke(_viewModel.getItems()[i])
                }
                .setPositiveButton(
                    R.string.select_station_yes
                ) { _, _ ->
                    Log.d("Debug", "Yes")
                    _viewModel.onClickPositiveButtonCallback?.invoke()
                }
                .setNegativeButton(
                    R.string.select_station_no
                ) { _, _ ->
                    Log.d("Debug", "No")
                    _viewModel.onClickNegativeButtonCallback?.invoke()
                }
            builder.create()
        }!!
    }

}