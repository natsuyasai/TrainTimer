package com.nyasai.traintimer.routelist

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.*
import com.nyasai.traintimer.databinding.FragmentRouteListBinding
import com.nyasai.traintimer.define.Define
import com.nyasai.traintimer.routesearch.ListItemSelectDialogFragment
import com.nyasai.traintimer.routesearch.SearchTargetInputDialogFragment
import com.nyasai.traintimer.util.FragmentUtil
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import kotlinx.android.synthetic.main.common_loading.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

/**
 * 路線一覧表示フラグメント
 */
class RouteListFragment : Fragment() {

    // 路線リストアイテム削除確認ダイアログタグ
    private val ROUTE_LIST_DELETE_CONFIRM_DLG_TAG = "RouteListItemDeleteConfirm"
    // 路線検索ダイアログタグ
    private val SEARCH_TARGET_INPUT_DLG_TAG = "SearchTargetInput"
    // 駅選択ダイアログ
    private val SELECT_LIST_DLG_TAG = "SelectList"

    // DBDao
    private lateinit var _routeDatabaseDao: RouteDatabaseDao

    // 路線リストViewModel
    private val _routeListViewModel: RouteListViewModel by lazy {
        ViewModelProvider(
            this,
            RouteListViewModelFactory(_routeDatabaseDao, requireNotNull(this.activity).application)
        ).get(RouteListViewModel::class.java)
    }

    // Yahoo路線情報取得用
    private val _yahooRouteInfoGetter = YahooRouteInfoGetter()

    // 検索情報保持領域
    private var _searchRouteListItem: RouteListItem? = null

    // UI実行用ハンドラ
    private val _handler = Handler()

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

        val application = requireNotNull(this.activity).application

        _routeDatabaseDao = RouteDatabase.getInstance(application).routeDatabaseDao

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
        val searchTargetInputDialog = parentFragmentManager.findFragmentByTag(SEARCH_TARGET_INPUT_DLG_TAG)
        if(searchTargetInputDialog != null && searchTargetInputDialog is SearchTargetInputDialogFragment){
            searchTargetInputDialog.onClickPositiveButtonCallback = {
                searchStation(searchTargetInputDialog.getInputText())
            }
            searchTargetInputDialog.onClickNegativeButtonCallback = {
            }
        }
        val selectListDialog = parentFragmentManager.findFragmentByTag(SELECT_LIST_DLG_TAG)
        if(selectListDialog != null && selectListDialog is ListItemSelectDialogFragment){
            selectListDialog.onClickPositiveButtonCallback = {
            }
            selectListDialog.onClickNegativeButtonCallback = {
            }
            selectListDialog.onSelectItem = {
            }
            selectListDialog.itemList = arrayOf()
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
        val dialog = SearchTargetInputDialogFragment()
        dialog.onClickPositiveButtonCallback = {
            Log.d("Debug", dialog.getInputText())
            searchStation(dialog.getInputText())
        }
        dialog.onClickNegativeButtonCallback = {
        }
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
        val dialog = ListItemSelectDialogFragment(itemsMap.keys.toTypedArray())
        dialog.onClickPositiveButtonCallback = {
            searchDirectionFromUrl(itemsMap, dialog.selectItem)
        }
        dialog.onClickNegativeButtonCallback = {
            _searchRouteListItem = null
        }
        dialog.showNow(parentFragmentManager, SELECT_LIST_DLG_TAG)
    }

    /**
     * 行先選択ダイアログ表示
     * @param itemsMap 行先一覧(key: 路線名::行先, value: URL)
     */
    private fun showDirectionSelectDialog(itemsMap: Map<String, String>) {
        // 前回分削除
        FragmentUtil.deletePrevDialog(SELECT_LIST_DLG_TAG, parentFragmentManager)

        // ダイアログ表示
        val dialog = ListItemSelectDialogFragment(itemsMap.keys.toTypedArray())
        dialog.onClickPositiveButtonCallback = {
            addRouteInfo(itemsMap, dialog.selectItem)
        }
        dialog.onClickNegativeButtonCallback = {
            _searchRouteListItem = null
        }
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
        GlobalScope.async {
            _routeDatabaseDao.deleteRouteListItem(targetDataId)
            _routeDatabaseDao.deleteRouteDetailsItemWithParentId(targetDataId)
            _routeDatabaseDao.deleteFilterInfoItemWithParentId(targetDataId)
        }
    }

    /**
     * 駅検索
     * @param stationName 検索対象駅名
     */
    private fun searchStation(stationName: String) {
        common_loading.visibility = android.widget.ProgressBar.VISIBLE
        GlobalScope.async {
            // 駅名より検索実行．実行結果から駅名リストダイアログ表示
            val stationListMap = _yahooRouteInfoGetter.getStationList(stationName)
            if(stationListMap.isNotEmpty()) {
                _handler.post {
                    common_loading.visibility = android.widget.ProgressBar.INVISIBLE
                    showStationSelectDialog(stationListMap)
                }
            }
            else{
                _searchRouteListItem = RouteListItem()
                _searchRouteListItem!!.stationName = stationName
                searchDirectionFromStationName(stationName)
            }
        }
    }

    /**
     * 行先一覧検索(駅名)
     * @param stationName 検索対象駅名
     */
    private fun searchDirectionFromStationName(stationName: String) {
        common_loading.visibility = android.widget.ProgressBar.VISIBLE
        GlobalScope.async {
            val directionListMap = _yahooRouteInfoGetter.getDirectionFromStationName(stationName)
            _handler.post {
                common_loading.visibility = android.widget.ProgressBar.INVISIBLE
                showDirectionSelectDialog(directionListMap)
            }
        }
    }

    /**
     * 行先一覧検索(URL)
     * @param stationNameMap 駅名一覧(key: 駅名, value: URL)
     * @param selectStation 選択した駅名
     */
    private fun searchDirectionFromUrl(stationNameMap: Map<String, String>, selectStation: String) {

        if(stationNameMap[selectStation] == null){
            // TODO: エラーハンドリング
            _searchRouteListItem = null
            return
        }
        _searchRouteListItem = RouteListItem()
        _searchRouteListItem!!.stationName = selectStation

        // 行先リストを取得
        common_loading.visibility = android.widget.ProgressBar.VISIBLE
        GlobalScope.async {
            val directionListMap = _yahooRouteInfoGetter.getDirectionFromUrl(stationNameMap.getValue(selectStation))
            _handler.post {
                common_loading.visibility = android.widget.ProgressBar.INVISIBLE
                showDirectionSelectDialog(directionListMap)
            }
        }
    }

    /**
     * 路線情報追加
     * @param directionMap 行先一覧(key: 路線名::行先, value: URL)
     * @param selectDirection 選択した行先
     */
    private fun addRouteInfo(directionMap: Map<String, String>, selectDirection: String) {
        if(directionMap[selectDirection] == null) {
            // TODO: エラーハンドリング
            _searchRouteListItem = null
            return
        }
        loading_text.text = "時刻情報取得中……"
        common_loading.visibility = android.widget.ProgressBar.VISIBLE
        // キーから路線名と行先を分割
        val splitDirectionKey = _yahooRouteInfoGetter.splitDirectionKey(selectDirection)
        _searchRouteListItem!!.routeName = splitDirectionKey.first
        _searchRouteListItem!!.direction = splitDirectionKey.second

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 時刻データを全取得
        Log.d("Debug", "データ取得開始")
        GlobalScope.async {
            val routeInfo = _yahooRouteInfoGetter.getTimeTableInfo(directionMap.getValue(selectDirection))
            _handler.post {
                loading_text.text = "時刻情報登録中……"
            }
            // 時刻データが取得できていれば路線一覧情報をDBに追加
            Log.d("Debug", "データ登録開始")
            var parentDataId = 0L
            if(routeInfo.size == 3 && routeInfo[0].isNotEmpty() && routeInfo[1].isNotEmpty() && routeInfo[2].isNotEmpty()) {
                Log.d("Debug", "一覧データ登録")
                _routeDatabaseDao.insertRouteListItem(_searchRouteListItem!!)
                // 追加したアイテムのIDを取得
                for (item in _routeDatabaseDao.getDestAllRouteListItemsSync()) {
                    if(item.stationName == _searchRouteListItem!!.stationName
                        && item.routeName == _searchRouteListItem!!.routeName
                        && item.direction == _searchRouteListItem!!.direction){
                        parentDataId = item.dataId
                        break
                    }
                }
                _searchRouteListItem = null
            }

            Log.d("Debug", "詳細データ作成")
            val addDataList = mutableListOf<RouteDetails>()
            val filterInfoList = mutableListOf<FilterInfo>()
            val max = routeInfo.size - 1
            for (diagramType in 0..max) {
                // ダイヤ種別毎のアイテム
                for (timeInfo in routeInfo[diagramType]) {
                    // 時刻情報追加
                    addDataList.add(RouteDetails(
                        parentDataId = parentDataId,
                        diagramType = diagramType,
                        departureTime = timeInfo.time,
                        trainType = timeInfo.type,
                        destination = timeInfo.direction
                    ))
                    // フィルタ用情報生成
                    filterInfoList.add(FilterInfo(
                        parentDataId = parentDataId,
                        trainType = timeInfo.type
                    ))
                }
            }

            Log.d("Debug", "詳細データ登録")
            _routeDatabaseDao.insertRouteDetailsItems(addDataList)
            // フィルタ情報から重複削除したデータを老徳
            _routeDatabaseDao.insertFilterInfoItems(filterInfoList.distinctBy{ it.trainType })
            Log.d("Debug", "データ登録完了")
            _handler.post {
                loading_text.text = "読み込み中……"
                common_loading.visibility = android.widget.ProgressBar.INVISIBLE
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

        }
    }


    // endregion ダイアログ関連

}