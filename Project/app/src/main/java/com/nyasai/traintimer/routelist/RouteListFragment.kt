package com.nyasai.traintimer.routelist

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetails
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.databinding.FragmentRouteListBinding
import com.nyasai.traintimer.define.Define
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


/**
 * A simple [Fragment] subclass.
 * Use the [RouteListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class RouteListFragment : Fragment() {

    private val ROUTE_LIST_DELETE_CONFIRM_DLG_TAG = "routeListItemDeleteConfirm"

    private lateinit var _routeDatabaseDao: RouteDatabaseDao

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

        val viewModelFactory = RouteListViewModelFactory(_routeDatabaseDao, application)

        val routeListViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(RouteListViewModel::class.java)

        binding.routeListViewModel = routeListViewModel

        binding.setLifecycleOwner(this)


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
        setDummyData()

        // ダイアログ初期化
        initDialog()

        // メニューボタン表示設定
        setHasOptionsMenu(true)

        // 変更監視
        routeListViewModel.routeList.observe(viewLifecycleOwner, Observer {
            it?.let{
                // リストアイテム設定
                adapter.submitList(it)
                Log.d("Debug", "データ更新 : ${routeListViewModel.routeList.value.toString()}")
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }

    /**
     * onCreateOptionsMenuフック
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.route_list_option, menu)
    }

    /**
     * onOptionsItemSelectedフック
     */
    override fun onOptionsItemSelected(item: MenuItem) =
        when(item.itemId) {
            R.id.route_add_menu -> {
                Log.d("Debug","路線検索ボタン押下")
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


    /**
     * ダイアログ初期化
     */
    private fun initDialog() {
        // 画面生成時にダイアログが存在する場合は，コールバックを再登録
        var dialog = requireFragmentManager().findFragmentByTag(ROUTE_LIST_DELETE_CONFIRM_DLG_TAG)
        if(dialog != null && dialog is RouteListItemDeleteConfirmDialogFragment){
            dialog.onClickPositiveButtonCallback = {
                onClickDialogYse(it)
            }
            dialog.onClickNegativeButtonCallback = {
                onClickDialogNo(it)
            }
        }
    }

    /**
     * ダイアログ表示
     * @param item 選択対象アイテム
     */
    private fun showDeleteConfirmDialog(item: RouteListItem){
        // 前回分削除
        var prevDlg = requireFragmentManager().findFragmentByTag(ROUTE_LIST_DELETE_CONFIRM_DLG_TAG)
        if(prevDlg != null){
            requireFragmentManager().beginTransaction().remove(prevDlg)
        }
        requireFragmentManager().beginTransaction().addToBackStack(null)

        // ダイアログ表示
        var dialog = RouteListItemDeleteConfirmDialogFragment()
        val bundle = Bundle()
        bundle.putLong(Define.ROUTE_LIST_DELETE_CONFIRM_ARGMENT_DATAID, item.dataId)
        dialog.arguments = bundle
        dialog.onClickPositiveButtonCallback = {
            onClickDialogYse(it)
        }
        dialog.onClickNegativeButtonCallback = {
            onClickDialogNo(it)
        }
        dialog.showNow(requireFragmentManager(), ROUTE_LIST_DELETE_CONFIRM_DLG_TAG)

    }

    /**
     * 削除確認ダイアログYseボタンクリック処理
     * @param targetDataId 対象データID
     */
    private fun onClickDialogYse(targetDataId: Long?) {
        if(targetDataId == null){
            return
        }
        GlobalScope.async {
            _routeDatabaseDao.deleteRouteListItem(targetDataId)
        }
    }

    /**
     * 削除確認ダイアログNoボタンクリック処理
     * @param targetDataId 対象データID
     */
    private fun onClickDialogNo(targetDataId: Long?) {
    }

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