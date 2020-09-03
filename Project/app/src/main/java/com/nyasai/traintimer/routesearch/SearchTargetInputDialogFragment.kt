package com.nyasai.traintimer.routesearch

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nyasai.traintimer.R
import com.nyasai.traintimer.databinding.DialogSearchTargetInputBinding

/**
 * 検索対象入力用ダイアログ
 */
class SearchTargetInputDialogFragment: DialogFragment() {

    // ViewModel
    private val _searchTargetInputViewModel: SearchTargetInputViewModel by lazy {
        ViewModelProvider(requireActivity(),
        SearchTargetInputViewModelFactory()
        ).get(SearchTargetInputViewModel::class.java)
    }

    /**
     * ダイアログ生成
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val binding = DataBindingUtil.inflate<DialogSearchTargetInputBinding>(requireActivity().layoutInflater,
                R.layout.dialog_search_target_input, null, false)

            binding.searchTargetInputVM = _searchTargetInputViewModel

            builder.setView(binding.root)
                .setMessage(R.string.search_input_message)
                .setPositiveButton(
                    R.string.search_input_yes
                ) { _, _ ->
                    Log.d("Debug", "検索開始")
                    _searchTargetInputViewModel.onClickPositiveButtonCallback?.invoke()
                }
                .setNegativeButton(
                    R.string.search_input_no
                ) { _, _ ->
                    Log.d("Debug", "キャンセル")
                    _searchTargetInputViewModel.onClickNegativeButtonCallback?.invoke()
                }
            builder.create()
        }!!
    }
}