package com.nyasai.traintimer.routeinfo

import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.databinding.FragmentRouteInfoBinding
import kotlinx.android.synthetic.main.fragment_route_info.*
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*


/**
 * 路線情報表示フラグメント
 */
class RouteInfoFragment : Fragment() {

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

    // タイマ
    private lateinit var _timer: Timer

    // タイマ実処理受け渡し用ハンドラ
    private val _handler = Handler()

    // 文字列装飾用
    private var _spannableStringBuilder = SpannableStringBuilder()

    /**
     * onCreateViewフック
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // データバインド設定
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_route_info, container, false
        )

        // データバインド
        _binding.routeInfoViewModel = _routeInfoViewModel
        _binding.routeInfoFragment = this

        _binding.lifecycleOwner = this


        // 路線詳細用アダプター設定
        _routeInfoAdapter = RouteInfoAdapter()
        _binding.routeInfoView.adapter = _routeInfoAdapter

        // 変更監視
        _routeInfoViewModel.routeItems.observe(viewLifecycleOwner, Observer {
            it?.let {
                // 表示種別に応じたデータを設定
                _routeInfoAdapter.submitList(_routeInfoViewModel.getDisplayRouteDetailsItems())
                _routeInfoViewModel.updateCurrentCountItem(true)
                Log.d("Debug", "詳細データ更新 : $it")
            }
        })

        _routeInfoViewModel.currentCountItem.observe(viewLifecycleOwner, Observer {
            it?.let {

                Log.d("Debug", "詳細データ更新 : $it")
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
                    // 画面に一番近いデータへの残り時間を設定する(1秒毎)
                    if(_routeInfoViewModel.currentCountItem.value == null){
                        _routeInfoViewModel.updateCurrentCountItem()
                    }
                    // データが取得できなければ，ハイフン表示とするために-1を設定
                    val diffTime = when{
                        _routeInfoViewModel.currentCountItem.value != null -> ChronoUnit.SECONDS.between(
                            LocalTime.now(), LocalTime.parse(
                                _routeInfoViewModel.currentCountItem.value?.departureTime
                            )
                        )
                        else -> -1L
                    }
                    if(diffTime <= 0) {
                        // 次のデータへ遷移
                        if(_routeInfoViewModel.currentCountItem.value != null) {
                            // リストに変更通知
                            _routeInfoAdapter.notifyItemChanged(
                                _routeInfoAdapter.indexOf(
                                    _routeInfoViewModel.currentCountItem.value!!
                                )
                            )
                        }
                        _routeInfoViewModel.updateCurrentCountItem()
                    }
                    val planeText = when{
                        diffTime >= 0 -> """Next ${"%0,2d".format((diffTime / 60))} : ${"%0,2d".format((diffTime % 60))}"""
                        else -> "Next -- : --"
                    }
                    // Next部分を小さく表示させる
                    _spannableStringBuilder.clear()
                    _spannableStringBuilder.append(planeText)
                    _spannableStringBuilder.setSpan(RelativeSizeSpan(0.5f),0,4,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    countdown.text = _spannableStringBuilder
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
     * タイトルクリック
     */
    fun onClickTitle(view: View) {
        // 表示ダイア種別を更新して表示データ切り替え
        _routeInfoViewModel.setNextDiagramType()
        _routeInfoAdapter.submitList(_routeInfoViewModel.getDisplayRouteDetailsItems())
    }
}