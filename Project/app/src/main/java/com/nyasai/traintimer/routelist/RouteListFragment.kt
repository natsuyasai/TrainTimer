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
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetails
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.databinding.FragmentRouteListBinding
import com.nyasai.traintimer.define.Define
import com.nyasai.traintimer.routesearch.ListItemSelectDialogFragment
import com.nyasai.traintimer.routesearch.SearchTargetInputDialogFragment
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
     * ビュー生成
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
                Log.d("Debug", "アイテム選択 : ${item.toString()}")
                view.findNavController().navigate(RouteListFragmentDirections.actionRouteListToRouteInfoFragment(item.dataId))
            }
        })
        adapter.setOnItemLongClickListener(object : RouteListAdapter.OnItemLongClickListener {
            override fun onItemLongClickListener(view: View, item: RouteListItem): Boolean {
                // 長押し
                Log.d("Debug", "アイテム長押し : ${item.toString()}")
                // 削除確認
                showDeleteConfirmDialog(item)
                return true
            }
        })

        // ダミーデータ挿入
        //setDummyData()

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
    override fun onOptionsItemSelected(item: MenuItem) =
        when(item.itemId) {
            R.id.route_add_menu -> {
                Log.d("Debug","路線検索ボタン押下")
                showSearchTargetInputDialog()
                true
            }
            R.id.setting_menu -> {
                Log.d("Debug","設定ボタン押下")
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
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
        deletePrevDialog(ROUTE_LIST_DELETE_CONFIRM_DLG_TAG)

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
        deletePrevDialog(SEARCH_TARGET_INPUT_DLG_TAG)

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
     */
    private fun showStationSelectDialog(itemsMap: Map<String, String>) {
        // 前回分削除
        deletePrevDialog(SELECT_LIST_DLG_TAG)


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
     */
    private fun showDirectionSelectDialog(itemsMap: Map<String, String>) {
        // 前回分削除
        deletePrevDialog(SELECT_LIST_DLG_TAG)

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
     * 前回分ダイアログ削除
     */
    private fun deletePrevDialog(tag: String) {
        val prevDlg = parentFragmentManager.findFragmentByTag(tag)
        if(prevDlg != null){
            parentFragmentManager.beginTransaction().remove(prevDlg)
        }
        parentFragmentManager.beginTransaction().addToBackStack(null)
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
        }
    }

    /**
     * 駅検索
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
            for (diagramType in 0..routeInfo.size) {
                // ダイヤ種別毎のアイテム
                for (timeInfo in routeInfo[diagramType]) {
                    // TODO: まとめて登録
                    // 時刻情報追加
                    val item = RouteDetails()
                    item.parentDataId = parentDataId
                    item.diagramType = diagramType
                    item.departureTime = timeInfo.time
                    item.trainType = timeInfo.type
                    item.destination = timeInfo.direction
                    _routeDatabaseDao.insertRouteDetailsItem(item)
                }
            }
            Log.d("Debug", "データ登録完了")
            _handler.post {
                loading_text.text = ""
                common_loading.visibility = android.widget.ProgressBar.INVISIBLE
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

        }
    }


    // endregion ダイアログ関連

    /**
     * ダミーデータ設定
     */
    private fun setDummyData() {
        // region *************仮データ挿入*************
        val tmp1 = RouteListItem(0,"1JR Hoge線", "Fuga駅", "大阪方面")
        val tmp2 = RouteListItem(0,"2JR Hoge線", "Fuga駅", "大阪方面")
        val tmp3 = RouteListItem(0,"3JR Hoge線", "Fuga駅", "大阪方面")
        val tmp4 = RouteListItem(0,"4JR Hoge線", "Fuga駅", "大阪方面")
        val tmp5 = RouteListItem(0,"5JR Hoge線", "Fuga駅", "大阪方面")
        val tmp6 = RouteListItem(0,"6JR Hoge線", "Fuga駅", "大阪方面")
        val tmp7 = RouteListItem(0,"7JR Hoge線", "Fuga駅", "大阪方面")
        val tmp8 = RouteListItem(0,"8JR Hoge線", "Fuga駅", "大阪方面")
        val tmp9 = RouteListItem(0,"9JR Hoge線", "Fuga駅", "大阪方面")
        val tmp10 = RouteListItem(0,"10JR Hoge線", "Fuga駅", "大阪方面")
        GlobalScope.async{
            _routeDatabaseDao.clearAllRouteListItem()
            _routeDatabaseDao.insertRouteListItem(tmp1)
            _routeDatabaseDao.insertRouteListItem(tmp2)
            _routeDatabaseDao.insertRouteListItem(tmp3)
            _routeDatabaseDao.insertRouteListItem(tmp4)
            _routeDatabaseDao.insertRouteListItem(tmp5)
            _routeDatabaseDao.insertRouteListItem(tmp6)
            _routeDatabaseDao.insertRouteListItem(tmp7)
            _routeDatabaseDao.insertRouteListItem(tmp8)
            _routeDatabaseDao.insertRouteListItem(tmp9)
            _routeDatabaseDao.insertRouteListItem(tmp10)

            val routeListItems = _routeDatabaseDao.getAllRouteListItemsSync()
            val tmpDet1 = RouteDetails(0,routeListItems[0].dataId, 0,"01:00","普通", "大阪", "url")
            val tmpDet2 = RouteDetails(0,routeListItems[0].dataId, 1,"02:00","普通", "大阪", "url")
            val tmpDet3 = RouteDetails(0,routeListItems[0].dataId, 2,"03:00","普通", "大阪", "url")
            val tmpDet4 = RouteDetails(0,routeListItems[0].dataId, 0,"04:00","普通", "大阪", "url")
            val tmpDet5 = RouteDetails(0,routeListItems[0].dataId, 1,"05:00","普通", "大阪", "url")
            val tmpDet6 = RouteDetails(0,routeListItems[0].dataId, 2,"06:00","普通", "大阪", "url")
            val tmpDet7 = RouteDetails(0,routeListItems[0].dataId, 0,"07:00","普通", "大阪", "url")
            val tmpDet8 = RouteDetails(0,routeListItems[0].dataId, 1,"08:00","普通", "大阪", "url")
            val tmpDet9 = RouteDetails(0,routeListItems[0].dataId, 2,"09:00","普通", "大阪", "url")
            val tmpDet10 = RouteDetails(0,routeListItems[0].dataId, 0,"15:00","普通", "大阪", "url")
            val tmpDet11 = RouteDetails(0,routeListItems[0].dataId, 1,"16:00","普通", "大阪", "url")
            val tmpDet12 = RouteDetails(0,routeListItems[0].dataId, 2,"17:00","普通", "大阪", "url")
            val tmpDet13 = RouteDetails(0,routeListItems[0].dataId, 0,"18:00","普通", "大阪", "url")
            val tmpDet14 = RouteDetails(0,routeListItems[0].dataId, 1,"19:00","普通", "大阪", "url")
            val tmpDet15 = RouteDetails(0,routeListItems[0].dataId, 2,"20:00","普通", "大阪", "url")
            val tmpDet16 = RouteDetails(0,routeListItems[0].dataId, 0,"23:00","普通", "大阪", "url")
            val tmpDet17 = RouteDetails(0,routeListItems[0].dataId, 1,"23:00","普通", "大阪", "url")
            val tmpDet18 = RouteDetails(0,routeListItems[0].dataId, 2,"23:00","普通", "大阪", "url")

            val tmpDet00 = RouteDetails(0,routeListItems[0].dataId, 1,"16:25","普通", "大阪", "url")

            _routeDatabaseDao.clearAllRouteDetailsItem()
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet1)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet2)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet3)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet4)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet5)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet6)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet7)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet8)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet9)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet10)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet11)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet12)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet13)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet14)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet15)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet16)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet17)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet18)
            _routeDatabaseDao.insertRouteDetailsItem(tmpDet00)
        }

        // endregion *************仮データ挿入*************
    }
}