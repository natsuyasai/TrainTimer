package com.nyasai.traintimer.routelist

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.nyasai.traintimer.R
import com.nyasai.traintimer.commonparts.CommonLoadingViewModel
import com.nyasai.traintimer.commonparts.CommonLoadingViewModelFactory
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.databinding.FragmentRouteListBinding
import com.nyasai.traintimer.define.Define
import com.nyasai.traintimer.routesearch.*
import com.nyasai.traintimer.util.FragmentUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


/**
 * 路線一覧表示フラグメント
 */
class RouteListFragment : Fragment(), CoroutineScope {

    private companion object {
        // 路線リストアイテム削除確認ダイアログタグ
        const val RouteListDeleteConfirmDialogTag = "RouteListItemDeleteConfirm"

        // 路線リストアイテム編集ダイアログ
        const val RouteListItemEditDialogTag = "RouteListItemEdit"

        // 路線検索ダイアログタグ
        const val SearchTargetInputDialogTag = "SearchTargetInput"

        // 駅選択ダイアログ
        const val SelectListDialogTag = "SelectList"
    }

    // 路線リストViewModel
    private val _routeListViewModel: RouteListViewModel by lazy {
        val application = requireNotNull(this.activity).application
        ViewModelProvider(
            this,
            RouteListViewModelFactory(
                RouteDatabase.getInstance(application).routeDatabaseDao,
                application
            )
        ).get(RouteListViewModel::class.java)
    }

    // 検索用インプットダイアログViewModel
    private val _searchTargetInputViewModel: SearchTargetInputViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            SearchTargetInputViewModelFactory()
        ).get(SearchTargetInputViewModel::class.java)
    }

    // リストアイテム選択ViewModel
    private val _istItemSelectViewModel: ListItemSelectViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ListItemSelectViewModelFactory()
        ).get(ListItemSelectViewModel::class.java)
    }

    // 共通ローディングViewModel
    private val _commonLoadingViewModel: CommonLoadingViewModel by lazy {
        ViewModelProvider(requireActivity(), CommonLoadingViewModelFactory()).get(
            CommonLoadingViewModel::class.java
        )
    }

    // 検索情報保持領域
    private var _searchRouteListItem: RouteListItem? = null

    // UI実行用ハンドラ
    private val _handler = Handler(Looper.getMainLooper())

    // 本フラグメント用job
    private val _job = Job()

    // 本スコープ用のコンテキスト
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + _job

    private val _viewModelContext: CoroutineContext
        get() = Dispatchers.Default + _job

    private var _itemTouchHelper: ItemTouchHelper? = null

    private val _routeListAdapter = RouteListAdapter()

    private lateinit var _fragmentRouteListBinding: FragmentRouteListBinding

    /**
     * onCreateViewフック
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // データバインド設定
        _fragmentRouteListBinding = DataBindingUtil.inflate<FragmentRouteListBinding>(
            inflater, R.layout.fragment_route_list, container, false
        )

        _fragmentRouteListBinding.routeListViewModel = _routeListViewModel
        _fragmentRouteListBinding.lifecycleOwner = this
        _fragmentRouteListBinding.commonLoadingViewModel = _commonLoadingViewModel


        // 路線リスト用アダプター設定
        _fragmentRouteListBinding.routeListView.adapter = _routeListAdapter
        setListItemTouchEvent(_routeListAdapter)

        // ダイアログ初期化
        initDialog()

        // メニューボタン表示設定
        initMenuItem()

        // 変更監視
        _routeListViewModel.routeList.observe(viewLifecycleOwner) {
            it?.let {
                // リストアイテム設定
                _routeListAdapter.submitList(it)
                Log.d("Debug", "データ更新 : ${_routeListViewModel.routeList.value.toString()}")
            }
        }

        // Inflate the layout for this fragment
        return _fragmentRouteListBinding.root
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
        val deleteConfirmDialog =
            parentFragmentManager.findFragmentByTag(RouteListDeleteConfirmDialogTag)
        if (deleteConfirmDialog != null && deleteConfirmDialog is RouteListItemDeleteConfirmDialogFragment) {
            deleteConfirmDialog.onClickPositiveButtonCallback = {
                onClickDeleteConfirmDialogYse(it)
            }
            deleteConfirmDialog.onClickNegativeButtonCallback = {
            }
        }
    }

    /**
     * メニュー要素初期化
     */
    private fun initMenuItem() {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.route_list_option, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return onSelectedOptionItem(menuItem)
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * onOptionsItemSelectedフック
     */
    private fun onSelectedOptionItem(item: MenuItem): Boolean {
        // ローディング表示中はメニュー非表示
        if (_commonLoadingViewModel.isVisible.value == true) {
            return false
        }
        return when (item.itemId) {
            R.id.route_add_menu -> {
                Log.d("Debug", "路線検索ボタン押下")
                showSearchTargetInputDialog()
                true
            }
            R.id.route_manual_sort -> {
                Log.d("Debug", "路線ソートボタン押下")
                _routeListViewModel.switchManualSortMode()
                setSortHelper(_fragmentRouteListBinding)
                true
            }
            R.id.setting_menu -> {
                Log.d("Debug", "設定ボタン押下")
                showSettingFragment()
                true
            }
            else -> {
                false
            }
        }
    }

    /**
     * 路線アイテム編集ダイアログ表示
     * @param item 選択対象アイテム
     */
    private fun showItemEditDialog(item: RouteListItem) {
        // 前回分削除
        FragmentUtil.deletePrevDialog(RouteListItemEditDialogTag, parentFragmentManager)

        // ダイアログ表示
        val dialog = RouteListItemEditDialogFragment()
        val bundle = Bundle()
        bundle.putLong(Define.RouteListDeleteConfirmArgentDataId, item.dataId)
        dialog.arguments = bundle
        dialog.onClickPositiveButtonCallback =
            { editType: RouteListItemEditDialogFragment.EditType, _: Long? ->
                when (editType) {
                    RouteListItemEditDialogFragment.EditType.Update -> {
                        updateRouteItemInfo(item)
                    }
                    else -> {
                        showDeleteConfirmDialog(item)
                    }
                }
            }
        dialog.onClickNegativeButtonCallback =
            { _: RouteListItemEditDialogFragment.EditType, _: Long? ->

            }
        dialog.showNow(parentFragmentManager, RouteListItemEditDialogTag)
    }

    /**
     * 削除確認ダイアログ表示
     * @param item 選択対象アイテム
     */
    private fun showDeleteConfirmDialog(item: RouteListItem) {
        // 前回分削除
        FragmentUtil.deletePrevDialog(RouteListDeleteConfirmDialogTag, parentFragmentManager)

        // ダイアログ表示
        val dialog = RouteListItemDeleteConfirmDialogFragment()
        val bundle = Bundle()
        bundle.putLong(Define.RouteListDeleteConfirmArgentDataId, item.dataId)
        dialog.arguments = bundle
        dialog.onClickPositiveButtonCallback = {
            onClickDeleteConfirmDialogYse(it)
        }
        dialog.onClickNegativeButtonCallback = {
        }
        dialog.showNow(parentFragmentManager, RouteListDeleteConfirmDialogTag)
    }

    /**
     * 路線検索ダイアログ表示
     */
    private fun showSearchTargetInputDialog() {
        // 前回分削除
        FragmentUtil.deletePrevDialog(SearchTargetInputDialogTag, parentFragmentManager)

        // ダイアログ表示
        _searchTargetInputViewModel.onClickPositiveButtonCallback = {
            Log.d("Debug", _searchTargetInputViewModel.getStationName())
            searchStation(_searchTargetInputViewModel.getStationName())
        }
        val dialog = SearchTargetInputDialogFragment()
        dialog.showNow(parentFragmentManager, SearchTargetInputDialogTag)
    }

    /**
     * 駅選択ダイアログ表示
     * @param itemsMap 駅一覧情報(key: 駅名, value: URL)
     */
    private fun showStationSelectDialog(itemsMap: Map<String, String>) {
        // 前回分削除
        FragmentUtil.deletePrevDialog(SelectListDialogTag, parentFragmentManager)

        // ダイアログ表示
        _istItemSelectViewModel.setItems(itemsMap.keys.toTypedArray())
        _istItemSelectViewModel.onClickPositiveButtonCallback = {
            searchDestinationFromUrl(itemsMap, _istItemSelectViewModel.selectItem)
        }
        _istItemSelectViewModel.onClickNegativeButtonCallback = {
            _searchRouteListItem = null
        }
        val dialog = ListItemSelectDialogFragment()
        dialog.showNow(parentFragmentManager, SelectListDialogTag)
    }

    /**
     * 行先選択ダイアログ表示
     * @param itemsMap 行先一覧(key: 路線名::行先, value: URL)
     */
    private fun showDestinationSelectDialog(itemsMap: Map<String, String>) {
        // 前回分削除
        FragmentUtil.deletePrevDialog(SelectListDialogTag, parentFragmentManager)

        // ダイアログ表示
        _istItemSelectViewModel.setItems(itemsMap.keys.toTypedArray())
        _istItemSelectViewModel.onClickPositiveButtonCallback = {
            addRouteInfo(itemsMap, _istItemSelectViewModel.selectItem)
        }
        _istItemSelectViewModel.onClickNegativeButtonCallback = {
            _searchRouteListItem = null
        }
        val dialog = ListItemSelectDialogFragment()
        dialog.showNow(parentFragmentManager, SelectListDialogTag)
    }

    /**
     * 削除確認ダイアログYseボタンクリック処理
     * @param targetDataId 対象データID
     */
    private fun onClickDeleteConfirmDialogYse(targetDataId: Long?) {
        if (targetDataId == null) {
            return
        }
        _routeListViewModel.deleteListItem(targetDataId)
    }

    /**
     * 駅検索
     * @param stationName 検索対象駅名
     */
    private fun searchStation(stationName: String) {
        _commonLoadingViewModel.showLoading()
        launch(Dispatchers.Default + _job) {
            // 駅名より検索実行．実行結果から駅名リストダイアログ表示
            val stationListMap = _routeListViewModel.getStationList(stationName)
            if (stationListMap?.isNotEmpty() == true) {
                withContext(Dispatchers.Main) {
                    _commonLoadingViewModel.closeLoading()
                    showStationSelectDialog(stationListMap)
                }
            } else {
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
        launch(Dispatchers.Main) {
            _commonLoadingViewModel.showLoading()
        }
        launch(_viewModelContext) {
            val destinationListMap = _routeListViewModel.getDestinationFromStationName(stationName)
            withContext(Dispatchers.Main) {
                _commonLoadingViewModel.closeLoading()
                showDestinationSelectDialog(destinationListMap)
            }
        }
    }

    /**
     * 行先一覧検索(URL)
     * @param stationNameMap 駅名一覧(key: 駅名, value: URL)
     * @param selectStation 選択した駅名
     */
    private fun searchDestinationFromUrl(
        stationNameMap: Map<String, String>,
        selectStation: String
    ) {

        if (stationNameMap[selectStation] == null) {
            // TODO: エラーハンドリング
            _searchRouteListItem = null
            Toast.makeText(context, "行先一覧に失敗しました", Toast.LENGTH_SHORT).show()
            return
        }
        _searchRouteListItem = RouteListItem()
        _searchRouteListItem!!.stationName = selectStation

        // 行先リストを取得
        _commonLoadingViewModel.showLoading()
        launch(_viewModelContext) {
            val destinationListMap =
                _routeListViewModel.getDestinationFromUrl(stationNameMap.getValue(selectStation))
            withContext(Dispatchers.Main) {
                _commonLoadingViewModel.closeLoading()
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
        if (destinationMap[selectDestination] == null) {
            // TODO: エラーハンドリング
            _searchRouteListItem = null
            Toast.makeText(context, "時刻表の取得に失敗しました", Toast.LENGTH_SHORT).show()
            return
        }
        _commonLoadingViewModel.showLoading("時刻情報取得中")
        // キーから路線名と行先を分割
        val splitDestinationKey = _routeListViewModel.splitDestinationKey(selectDestination)
        _searchRouteListItem!!.routeName = splitDestinationKey.first
        _searchRouteListItem!!.destination = splitDestinationKey.second

        setKeepScreenOn()
        // 時刻データを全取得
        Log.d("Debug", "データ取得開始")
        launch(_viewModelContext) {
            val routeInfo =
                _routeListViewModel.getTimeTableInfo(
                    destinationMap.getValue(selectDestination),
                    { _commonLoadingViewModel.incrementMaxCountFromBackgroundTask(it) },
                    { _commonLoadingViewModel.incrementCurrentCountFromBackgroundTask(1) })
            _handler.post {
                _commonLoadingViewModel.changeText("時刻情報登録中")
            }
            // 時刻データが取得できていれば路線一覧情報をDBに追加
            val parentDataId =
                _routeListViewModel.registerRouteListItem(routeInfo, _searchRouteListItem!!)
            _searchRouteListItem = null
            _routeListViewModel.registerRouteInfoDetailItems(routeInfo, parentDataId)

            Log.d("Debug", "データ登録完了")
            withContext(Dispatchers.Main) {
                _commonLoadingViewModel.closeLoading()
                setKeepScreenOff()
            }
        }
    }

    /**
     * スクリーンON維持
     */
    private fun setKeepScreenOn() {
        requireActivity().window!!.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * スクリーンON維持解除
     */
    private fun setKeepScreenOff() {
        requireActivity().window!!.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    // endregion ダイアログ関連


    private fun setListItemTouchEvent(adapter: RouteListAdapter) {
        // 操作イベント登録
        adapter.setOnItemClickListener(object : RouteListAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, item: RouteListItem) {
                // ページ遷移
                Log.d("Debug", "アイテム選択 : $item")
                view.findNavController()
                    .navigate(RouteListFragmentDirections.actionRouteListToRouteInfoFragment(item.dataId))
            }
        })
        adapter.setOnItemLongClickListener(object :
            RouteListAdapter.OnItemLongClickListener {
            override fun onItemLongClickListener(view: View, item: RouteListItem): Boolean {
                // 長押し
                Log.d("Debug", "アイテム長押し : $item")
                if (_routeListViewModel.isManualSortMode.value == null || _routeListViewModel.isManualSortMode.value == false) {
                    // 編集操作選択
                    showItemEditDialog(item)
                }
                return true
            }
        })
    }

    /**
     * ソート用タッチヘルパ実装
     */
    private fun setSortHelper(binding: FragmentRouteListBinding) {
        _itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.ACTION_STATE_IDLE
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    // 並び替え
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    Log.d("Debug", "from: $from  to: $to")
                    // 結果を保存
                    _routeListViewModel.updateSortIndex(from, to)
                    _routeListAdapter.notifyItemMoved(from, to)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // スワイプ
                }
            })
        if (_routeListViewModel.isManualSortMode.value == true) {
            _itemTouchHelper!!.attachToRecyclerView(binding.routeListView)
        } else {
            _itemTouchHelper!!.attachToRecyclerView(null)
        }
    }

    /**
     * アイテム情報更新
     * @param item 選択対象アイテム
     */
    private fun updateRouteItemInfo(item: RouteListItem) {
        Log.d("Debug", "Update" + item.routeName)
        _commonLoadingViewModel.showLoading("時刻情報更新中")
        setKeepScreenOn()

        launch(_viewModelContext) {
            val ret = _routeListViewModel.updateRouteInfo(
                item,
                { _commonLoadingViewModel.incrementMaxCountFromBackgroundTask(it) },
                { _commonLoadingViewModel.incrementCurrentCountFromBackgroundTask(1) })
            withContext(Dispatchers.Main) {
                _commonLoadingViewModel.closeLoading()
                setKeepScreenOff()
                if (!ret) {
                    Toast.makeText(context, "更新に失敗しました", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 設定フラグメント表示
     */
    private fun showSettingFragment() {
        findNavController().navigate(RouteListFragmentDirections.actionRouteListToPreferenceFragment())
    }

}