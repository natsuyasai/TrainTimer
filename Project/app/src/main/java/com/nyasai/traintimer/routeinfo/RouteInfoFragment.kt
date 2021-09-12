package com.nyasai.traintimer.routeinfo

import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.databinding.FragmentRouteInfoBinding
import com.nyasai.traintimer.util.FragmentUtil
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext


/**
 * 路線情報表示フラグメント
 */
class RouteInfoFragment : Fragment(), CoroutineScope {

    companion object{
        // フィルタ選択ダイアログ
        const val SelectFilterDialogTag = "SelectList"
    }


    // バインド情報
    private lateinit var _binding: FragmentRouteInfoBinding

    // 詳細リストアダプタ
    private lateinit var _routeInfoAdapter: RouteInfoAdapter

    // 路線情報ViewModel
    private val _routeInfoViewModel: RouteInfoViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val viewModelFactory = RouteInfoViewModelFactory(
            RouteDatabase.getInstance(application).routeDatabaseDao,
            application,
            RouteInfoFragmentArgs.fromBundle(requireArguments()).parentDataId
        )
        ViewModelProvider(
            this,
            viewModelFactory
        ).get(RouteInfoViewModel::class.java)
    }

    // ViewModel
    private val _filterItemSelectViewModel: FilterItemSelectViewModel by lazy {
        ViewModelProvider(requireActivity()).get(FilterItemSelectViewModel::class.java)
    }

    // タイマ
    private lateinit var _timer: Timer

    // タイマ実処理受け渡し用ハンドラ
    private val _handler = Handler()

    // 文字列装飾用
    private var _spannableStringBuilder = SpannableStringBuilder()

    // 本フラグメント用job
    private val _job = Job()

    // 本スコープ用のコンテキスト
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + _job

    /**
     * onCreateViewフック
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // データバインド設定
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_route_info, container, false
        )

        // データバインド
        _binding.routeInfoViewModel = _routeInfoViewModel
        _binding.routeInfoFragment = this
        _binding.lifecycleOwner = this

        // 祝日の場合設定
        launch (Dispatchers.Default + _job){
            _routeInfoViewModel.initializeAsync()
            withContext(Dispatchers.Main){
                _routeInfoAdapter.submitList(_routeInfoViewModel.getDisplayRouteDetailItems())
                _routeInfoViewModel.updateCurrentCountItem(true)
            }
        }

        // 路線詳細用アダプター設定
        _routeInfoAdapter = RouteInfoAdapter()
        _binding.routeInfoView.adapter = _routeInfoAdapter

        // ダイアログ初期化
        initDialog()

        // メニューボタン表示設定
        setHasOptionsMenu(true)

        // 変更監視
        _routeInfoViewModel.routeItems.observe(viewLifecycleOwner, {
            it?.let {
                // 表示種別に応じたデータを設定
                _routeInfoAdapter.submitList(_routeInfoViewModel.getDisplayRouteDetailItems())
                _routeInfoViewModel.updateCurrentCountItem(true)
                Log.d("Debug", "詳細データ更新 : $it")
            }
        })

        _routeInfoViewModel.currentCountItem.observe(viewLifecycleOwner, {
            it?.let {
                updateCountdownTargetInfo()
                Log.d("Debug", "カウントダウン対象データ更新 : $it")
            }
        })

        _routeInfoViewModel.filterInfo.observe(viewLifecycleOwner, {
            it?.let {
                _routeInfoAdapter.submitList(_routeInfoViewModel.getDisplayRouteDetailItems())
                _routeInfoViewModel.updateCurrentCountItem(true)
                Log.d("Debug", "フィルタ更新 : $it")
            }
        })

        return _binding.root
    }

    /**
     * onResumeフック
     */
    override fun onResume() {
        super.onResume()
        // カウントダウンタイマ開始
        _timer = Timer()
        _timer.schedule(object : TimerTask() {
            /**
             * タイマ開始
             */
            override fun run() {
                // UIスレッドで実行
                _handler.post {
                    val diffTime = _routeInfoViewModel.getNextDiffTime()
                    if (diffTime <= 0) {
                        // 次のデータへ遷移
                        if (_routeInfoViewModel.currentCountItem.value != null) {
                            // リストに変更通知
                            _routeInfoAdapter.notifyItemChanged(
                                _routeInfoAdapter.indexOf(
                                    _routeInfoViewModel.currentCountItem.value!!
                                )
                            )
                            _routeInfoViewModel.updateCurrentCountItem()
                        }
                    }
                    val planeText = when {
                        diffTime >= 0 -> """Next ${"%0,2d".format((diffTime / 60))} : ${
                            "%0,2d".format(
                                (diffTime % 60)
                            )
                        }"""
                        else -> "Next -- : --"
                    }
                    // Next部分を小さく表示させる
                    _spannableStringBuilder.clear()
                    _spannableStringBuilder.append(planeText)
                    _spannableStringBuilder.setSpan(
                        RelativeSizeSpan(0.5f),
                        0,
                        4,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    _binding.countdown.text = _spannableStringBuilder
                }
            }

        }, 100, 1000)
    }

    /**
     * onPauseフック
     */
    override fun onPause() {
        super.onPause()
        // カウントダウンタイマ終了
        _timer.cancel()
    }

    /**
     * onDestroyフック
     */
    override fun onDestroy() {
        _job.cancel()
        super.onDestroy()
    }

    /**
     * onCreateOptionsMenuフック
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.route_info_option, menu)
    }

    /**
     * onOptionsItemSelectedフック
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info_filter_menu -> {
                Log.d("Debug", "フィルタボタン押下")
                showFilterSelectDialog()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * タイトルクリック
     */
    fun onClickTitle(view: View) {
        // 表示ダイア種別を更新して表示データ切り替え
        _routeInfoViewModel.setNextDiagramType()
        _routeInfoAdapter.submitList(_routeInfoViewModel.getDisplayRouteDetailItems())
    }

    /**
     * ダイアログ初期化
     */
    private fun initDialog() {
        FragmentUtil.deletePrevDialog(SelectFilterDialogTag, parentFragmentManager)
    }

    /**
     * フィルタ選択ダイアログ表示
     */
    private fun showFilterSelectDialog() {
        // 前回分削除
        FragmentUtil.deletePrevDialog(SelectFilterDialogTag, parentFragmentManager)

        launch(Dispatchers.Default + _job) {
            _filterItemSelectViewModel.filterItemList =
                _routeInfoViewModel.getFilterInfoItemWithParentIdSync().toMutableList()
            _handler.post {
                // ダイアログ表示
                _filterItemSelectViewModel.onClickPositiveButtonCallback = {
                    launch(Dispatchers.Default + _job) {
                        _routeInfoViewModel.updateFilterInfoListItem(_filterItemSelectViewModel.filterItemList)
                    }

                }
                _filterItemSelectViewModel.onClickNegativeButtonCallback = {
                }
                val dialog = FilterItemSelectDialogFragment()
                dialog.showNow(parentFragmentManager, SelectFilterDialogTag)
            }
        }
    }

    /**
     * カウントダウン対象情報を更新
     */
    private fun updateCountdownTargetInfo() {
        // カウントダウン対象の時刻情報を設定
        val departureTimeStr = _routeInfoViewModel.currentCountItem.value?.departureTime ?: "--:--"
        val trainTypeStr = _routeInfoViewModel.currentCountItem.value?.trainType ?: "--"
        val destinationStr = _routeInfoViewModel.currentCountItem.value?.destination ?: "--"
        val text = """|${departureTimeStr}
                      |${trainTypeStr}
                      |${destinationStr}""".trimMargin()
        _binding.nextTimeTable.text = text

        // スクロール位置更新
        updateScrollPosition()
    }

    /**
     * スクロール位置更新
     */
    private fun updateScrollPosition() {
        if (_routeInfoViewModel.currentCountItem.value != null) {
            // リストアイテムの描画を待ってから対象位置までスクロール
            launch(Dispatchers.Default + _job) {
                delay(500)
                withContext(Dispatchers.Main) {
                    (_binding.routeInfoView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        _routeInfoAdapter.indexOf(_routeInfoViewModel.currentCountItem.value!!),
                        0
                    )
                    Log.d(
                        "Debug",
                        _routeInfoAdapter.indexOf(_routeInfoViewModel.currentCountItem.value!!)
                            .toString()
                    )
                }
            }
        }
    }
}