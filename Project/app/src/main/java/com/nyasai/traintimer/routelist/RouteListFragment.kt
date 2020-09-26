package com.nyasai.traintimer.routelist

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.*
import com.nyasai.traintimer.databinding.FragmentRouteListBinding
import com.nyasai.traintimer.define.Define
import com.nyasai.traintimer.routesearch.*
import com.nyasai.traintimer.util.FragmentUtil
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import kotlinx.android.synthetic.main.common_loading.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * 路線一覧表示フラグメント
 */
class RouteListFragment : Fragment(), CoroutineScope {

    // 路線リストアイテム削除確認ダイアログタグ
    private val ROUTE_LIST_DELETE_CONFIRM_DLG_TAG = "RouteListItemDeleteConfirm"
    // 路線検索ダイアログタグ
    private val SEARCH_TARGET_INPUT_DLG_TAG = "SearchTargetInput"
    // 駅選択ダイアログ
    private val SELECT_LIST_DLG_TAG = "SelectList"

    // 路線リストViewModel
    private val _routeListViewModel: RouteListViewModel by lazy {
        val application = requireNotNull(this.activity).application
        ViewModelProvider(
            this,
            RouteListViewModelFactory(RouteDatabase.getInstance(application).routeDatabaseDao, application)
        ).get(RouteListViewModel::class.java)
    }

    // ViewModel
    private val _searchTargetInputViewModel: SearchTargetInputViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            SearchTargetInputViewModelFactory()
        ).get(SearchTargetInputViewModel::class.java)
    }

    // ViewModel
    private val _istItemSelectViewModel: ListItemSelectViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ListItemSelectViewModelFactory()
        ).get(ListItemSelectViewModel::class.java)
    }

    // 検索情報保持領域
    private var _searchRouteListItem: RouteListItem? = null

    // UI実行用ハンドラ
    private val _handler = Handler()

    // 本フラグメント用job
    private val _job = Job()
    // 本スコープ用のコンテキスト
    override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + _job

    private val _viewModelContext: CoroutineContext
    get()=Dispatchers.Default + _job


    /**
     * onCreateViewフック
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // データバインド設定
        val binding: FragmentRouteListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_route_list, container, false)

        binding.routeListViewModel = _routeListViewModel

        binding.lifecycleOwner = this


        // 路線リスト用アダプター設定
        val adapter = RouteListAdapter()
        binding.routeListView.adapter = adapter
        // 操作イベント登録
        adapter.setOnItemClickListener(object : RouteListAdapter.OnItemClickListener{
            override fun onItemClickListener(view: View, item: RouteListItem) {
                // ページ遷移
                Log.d("Debug", "アイテム選択 : $item")
                view.findNavController().navigate(RouteListFragmentDirections.actionRouteListToRouteInfoFragment(item.dataId))
            }
        })
        adapter.setOnItemLongClickListener(object : RouteListAdapter.OnItemLongClickListener {
            override fun onItemLongClickListener(view: View, item: RouteListItem): Boolean {
                // 長押し
                Log.d("Debug", "アイテム長押し : $item")
                // 削除確認
                showDeleteConfirmDialog(item)
                return true
            }
        })

        // ダイアログ初期化
        initDialog()

        // メニューボタン表示設定
        setHasOptionsMenu(true)

        // 変更監視
        _routeListViewModel.routeList.observe(viewLifecycleOwner, Observer {
            it?.let{
                // リストアイテム設定
                adapter.submitList(it)
                Log.d("Debug", "データ更新 : ${_routeListViewModel.routeList.value.toString()}")
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }

    /**
     * onCreateOptionsMenuフック
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.route_list_option, menu)
    }

    /**
     * onOptionsItemSelectedフック
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // ローディング表示中はメニュー非表示
        if(common_loading.visibility == android.widget.ProgressBar.VISIBLE) {
            return false
        }
        return when (item.itemId) {
            R.id.route_add_menu -> {
                Log.d("Debug", "路線検索ボタン押下")
                showSearchTargetInputDialog()
                true
            }
            R.id.setting_menu -> {
                Log.d("Debug", "設定ボタン押下")
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * onDestroyフック
     */
    override fun onDestroy() {
        _job.cancel()
        super.onDestroy()
    }

    // region ダイアログ関連

    /**
     * ダイアログ初期化
     */
    private fun initDialog() {
        // 画面生成時にダイアログが存在する場合は，コールバックを再登録
        val deleteConfirmDialog = parentFragmentManager.findFragmentByTag(ROUTE_LIST_DELETE_CONFIRM_DLG_TAG)
        if(deleteConfirmDialog != null && deleteConfirmDialog is RouteListItemDeleteConfirmDialogFragment){
            deleteConfirmDialog.onClickPositiveButtonCallback = {
                onClickDeleteConfirmDialogYse(it)
            }
            deleteConfirmDialog.onClickNegativeButtonCallback = {
            }
        }
    }

    /**
     * 削除確認ダイアログ表示
     * @param item 選択対象アイテム
     */
    private fun showDeleteConfirmDialog(item: RouteListItem) {
        // 前回分削除
        FragmentUtil.deletePrevDialog(ROUTE_LIST_DELETE_CONFIRM_DLG_TAG, parentFragmentManager)

        // ダイアログ表示
        val dialog = RouteListItemDeleteConfirmDialogFragment()
        val bundle = Bundle()
        bundle.putLong(Define.ROUTE_LIST_DELETE_CONFIRM_ARGMENT_DATAID, item.dataId)
        dialog.arguments = bundle
        dialog.onClickPositiveButtonCallback = {
            onClickDeleteConfirmDialogYse(it)
        }
        dialog.onClickNegativeButtonCallback = {
        }
        dialog.showNow(parentFragmentManager, ROUTE_LIST_DELETE_CONFIRM_DLG_TAG)
    }

    /**
     * 路線検索ダイアログ表示
     */
    private fun showSearchTargetInputDialog() {
        // 前回分削除
        FragmentUtil.deletePrevDialog(SEARCH_TARGET_INPUT_DLG_TAG, parentFragmentManager)

        // ダイアログ表示
        _searchTargetInputViewModel.onClickPositiveButtonCallback = {
            Log.d("Debug", _searchTargetInputViewModel.getStationName())
            searchStation(_searchTargetInputViewModel.getStationName())
        }
        val dialog = SearchTargetInputDialogFragment()
        dialog.showNow(parentFragmentManager, SEARCH_TARGET_INPUT_DLG_TAG)
    }

    /**
     * 駅選択ダイアログ表示
     * @param itemsMap 駅一覧情報(key: 駅名, value: URL)
     */
    private fun showStationSelectDialog(itemsMap: Map<String, String>) {
        // 前回分削除
        FragmentUtil.deletePrevDialog(SELECT_LIST_DLG_TAG, parentFragmentManager)

        // ダイアログ表示
        _istItemSelectViewModel.setItems(itemsMap.keys.toTypedArray())
        _istItemSelectViewModel.onClickPositiveButtonCallback = {
            searchDestinationFromUrl(itemsMap, _istItemSelectViewModel.selectItem)
        }
        _istItemSelectViewModel.onClickNegativeButtonCallback = {
            _searchRouteListItem = null
        }
        val dialog = ListItemSelectDialogFragment()
        dialog.showNow(parentFragmentManager, SELECT_LIST_DLG_TAG)
    }

    /**
     * 行先選択ダイアログ表示
     * @param itemsMap 行先一覧(key: 路線名::行先, value: URL)
     */
    private fun showDestinationSelectDialog(itemsMap: Map<String, String>) {
        // 前回分削除
        FragmentUtil.deletePrevDialog(SELECT_LIST_DLG_TAG, parentFragmentManager)

        // ダイアログ表示
        _istItemSelectViewModel.setItems(itemsMap.keys.toTypedArray())
        _istItemSelectViewModel.onClickPositiveButtonCallback = {
            addRouteInfo(itemsMap, _istItemSelectViewModel.selectItem)
        }
        _istItemSelectViewModel.onClickNegativeButtonCallback = {
            _searchRouteListItem = null
        }
        val dialog = ListItemSelectDialogFragment()
        dialog.showNow(parentFragmentManager, SELECT_LIST_DLG_TAG)
    }

    /**
     * 削除確認ダイアログYseボタンクリック処理
     * @param targetDataId 対象データID
     */
    private fun onClickDeleteConfirmDialogYse(targetDataId: Long?) {
        if(targetDataId == null){
            return
        }
        _routeListViewModel.deleteListItem(targetDataId)
    }

    /**
     * 駅検索
     * @param stationName 検索対象駅名
     */
    private fun searchStation(stationName: String) {
        common_loading.visibility = android.widget.ProgressBar.VISIBLE
        launch(Dispatchers.Default + _job) {
            // 駅名より検索実行．実行結果から駅名リストダイアログ表示
            val stationListMap = _routeListViewModel.getStationList(stationName)
            if(stationListMap.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    common_loading.visibility = android.widget.ProgressBar.INVISIBLE
                    showStationSelectDialog(stationListMap)
                }
            }
            else{
                _searchRouteListItem = RouteListItem()
                _searchRouteListItem!!.stationName = stationName
                searchDestinationFromStationName(stationName)
            }
        }
    }

    /**
     * 行先一覧検索(駅名)
     * @param stationName 検索対象駅名
     */
    private fun searchDestinationFromStationName(stationName: String) {
        common_loading.visibility = android.widget.ProgressBar.VISIBLE
        launch(_viewModelContext) {
            val destinationListMap = _routeListViewModel.getDestinationFromStationName(stationName)
            withContext(Dispatchers.Main) {
                common_loading.visibility = android.widget.ProgressBar.INVISIBLE
                showDestinationSelectDialog(destinationListMap)
            }
        }
    }

    /**
     * 行先一覧検索(URL)
     * @param stationNameMap 駅名一覧(key: 駅名, value: URL)
     * @param selectStation 選択した駅名
     */
    private fun searchDestinationFromUrl(stationNameMap: Map<String, String>, selectStation: String) {

        if(stationNameMap[selectStation] == null){
            // TODO: エラーハンドリング
            _searchRouteListItem = null
            return
        }
        _searchRouteListItem = RouteListItem()
        _searchRouteListItem!!.stationName = selectStation

        // 行先リストを取得
        common_loading.visibility = android.widget.ProgressBar.VISIBLE
        launch(_viewModelContext) {
            val destinationListMap = _routeListViewModel.getDestinationFromUrl(stationNameMap.getValue(selectStation))
            withContext(Dispatchers.Main) {
                common_loading.visibility = android.widget.ProgressBar.INVISIBLE
                showDestinationSelectDialog(destinationListMap)
            }
        }
    }

    /**
     * 路線情報追加
     * @param destinationMap 行先一覧(key: 路線名::行先, value: URL)
     * @param selectDestination 選択した行先
     */
    private fun addRouteInfo(destinationMap: Map<String, String>, selectDestination: String) {
        if(destinationMap[selectDestination] == null) {
            // TODO: エラーハンドリング
            _searchRouteListItem = null
            return
        }
        loading_text.text = "時刻情報取得中……"
        common_loading.visibility = android.widget.ProgressBar.VISIBLE
        // キーから路線名と行先を分割
        val splitDestinationKey = _routeListViewModel.splitDestinationKey(selectDestination)
        _searchRouteListItem!!.routeName = splitDestinationKey.first
        _searchRouteListItem!!.destination = splitDestinationKey.second

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 時刻データを全取得
        Log.d("Debug", "データ取得開始")
        launch(_viewModelContext) {
            val routeInfo = _routeListViewModel.getTimeTableInfo(destinationMap.getValue(selectDestination))
            _handler.post {
                loading_text.text = "時刻情報登録中……"
            }
            // 時刻データが取得できていれば路線一覧情報をDBに追加
            Log.d("Debug", "データ登録開始")
            var parentDataId = 0L
            if(routeInfo.size == 3 && routeInfo[0].isNotEmpty() && routeInfo[1].isNotEmpty() && routeInfo[2].isNotEmpty()) {
                Log.d("Debug", "一覧データ登録")
                _routeListViewModel.insert(_searchRouteListItem!!)
                // 追加したアイテムのIDを取得
                for (item in _routeListViewModel.getListItemsAsync()) {
                    if(item.stationName == _searchRouteListItem!!.stationName
                        && item.routeName == _searchRouteListItem!!.routeName
                        && item.destination == _searchRouteListItem!!.destination){
                        parentDataId = item.dataId
                        break
                    }
                }
                _searchRouteListItem = null
            }

            Log.d("Debug", "詳細データ作成")
            val addDataList = mutableListOf<RouteDetail>()
            val filterInfoList = mutableListOf<FilterInfo>()
            val max = routeInfo.size - 1
            for (diagramType in 0..max) {
                // ダイヤ種別毎のアイテム
                for (timeInfo in routeInfo[diagramType]) {
                    // 時刻情報追加
                    addDataList.add(RouteDetail(
                        parentDataId = parentDataId,
                        diagramType = diagramType,
                        departureTime = timeInfo.time,
                        trainType = timeInfo.type,
                        destination = timeInfo.destination
                    ))
                    // フィルタ用情報生成
                    filterInfoList.add(FilterInfo(
                        parentDataId = parentDataId,
                        trainTypeAndDestination = FilterInfo.createFilterKey(timeInfo.type, timeInfo.destination)
                    ))
                }
            }

            Log.d("Debug", "詳細データ登録")
            _routeListViewModel.insertRouteDetailItems(addDataList)
            // フィルタ情報から重複削除したデータを登録
            _routeListViewModel.insertFilterInfoItems(filterInfoList.distinctBy{ it.trainTypeAndDestination })
            Log.d("Debug", "データ登録完了")
            withContext(Dispatchers.Main) {
                loading_text.text = "読み込み中……"
                common_loading.visibility = android.widget.ProgressBar.INVISIBLE
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

        }
    }

    // endregion ダイアログ関連

}