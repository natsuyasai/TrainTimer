package com.nyasai.traintimer.routesearch

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.nyasai.traintimer.R
import com.nyasai.traintimer.databinding.DialogSearchTargetInputBinding

/**
 * 検索対象入力用ダイアログ
 */
class SearchTargetInputDialogFragment: DialogFragment() {

    // Yesボタン押下時コールバック
    var onClickPositiveButtonCallback: (() -> Unit)? = null

    // Noボタン押下時コールバック
    var onClickNegativeButtonCallback: (() -> Unit)? = null

    // ViewModel
    private lateinit var _searchTargetInputViewModel: SearchTargetInputViewModel

    /**
     * ダイアログ生成
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val binding = DataBindingUtil.inflate<DialogSearchTargetInputBinding>(requireActivity().layoutInflater,
                R.layout.dialog_search_target_input, null, false)
            val viewModelFactory = SearchTargetInputViewModelFactory(requireNotNull(this.activity).application)
            _searchTargetInputViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SearchTargetInputViewModel::class.java)
            binding.searchTargetInputVM = _searchTargetInputViewModel

            builder.setView(binding.root)
                .setMessage(R.string.search_input_message)
                .setPositiveButton(
                    R.string.search_input_yes
                ) { _, _ ->
                    Log.d("Debug", "検索開始")
                    onClickPositiveButtonCallback?.invoke()
                }
                .setNegativeButton(
                    R.string.search_input_no
                ) { _, _ ->
                    Log.d("Debug", "キャンセル")
                    onClickNegativeButtonCallback?.invoke()
                }
            builder.create()
        }!!
    }

    fun getInputText() = _searchTargetInputViewModel.getStationName()

}