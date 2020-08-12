package com.nyasai.traintimer.routelist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.database.RouteDetails
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.databinding.FragmentRouteListBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


/**
 * A simple [Fragment] subclass.
 * Use the [RouteList.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class RouteList : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // データバインド設定
        val binding: FragmentRouteListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_route_list, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = RouteDatabase.getInstance(application).routeDatabaseDao

        val viewModelFactory = RouteListViewModelFactory(dataSource, application)

        val routeListViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(RouteListViewModel::class.java)

        binding.routeListViewModel = routeListViewModel

        binding.setLifecycleOwner(this)


        // 路線リスト用アダプター設定
        val adapter = RouteListAdapter()
        binding.routeListView.adapter = adapter
        adapter.setOnItemClickListener(object : RouteListAdapter.OnItemClickListener{
            override fun onItemClickListener(view: View, item: RouteListItem) {
                // ページ遷移
                Log.d("Debug", "アイテム選択 : ${item.toString()}")
                view.findNavController().navigate(RouteListDirections.actionRouteListToRouteInfoFragment(item.dataId))
            }
        })

        // region *************仮データ挿入*************
        val tmp1 = RouteListItem(0,"JR Hoge線", "Fuga駅", "大阪方面")
        val tmp2 = RouteListItem(0,"JR Hoge線", "Fuga駅", "大阪方面")
        val tmp3 = RouteListItem(0,"JR Hoge線", "Fuga駅", "大阪方面")
        val tmp4 = RouteListItem(0,"JR Hoge線", "Fuga駅", "大阪方面")
        val tmp5 = RouteListItem(0,"JR Hoge線", "Fuga駅", "大阪方面")
        val tmp6 = RouteListItem(0,"JR Hoge線", "Fuga駅", "大阪方面")
        val tmp7 = RouteListItem(0,"JR Hoge線", "Fuga駅", "大阪方面")
        val tmp8 = RouteListItem(0,"JR Hoge線", "Fuga駅", "大阪方面")
        val tmp9 = RouteListItem(0,"JR Hoge線", "Fuga駅", "大阪方面")
        val tmp10 = RouteListItem(0,"JR Hoge線", "Fuga駅", "大阪方面")
        GlobalScope.async{
            dataSource.clearAllRouteListItem()
            dataSource.insertRouteListItem(tmp1)
            dataSource.insertRouteListItem(tmp2)
            dataSource.insertRouteListItem(tmp3)
            dataSource.insertRouteListItem(tmp4)
            dataSource.insertRouteListItem(tmp5)
            dataSource.insertRouteListItem(tmp6)
            dataSource.insertRouteListItem(tmp7)
            dataSource.insertRouteListItem(tmp8)
            dataSource.insertRouteListItem(tmp9)
            dataSource.insertRouteListItem(tmp10)

            val routeListItems = dataSource.getAllRouteListItemsSync()
            val tmpDet1 = RouteDetails(0,routeListItems[0].dataId, 0,"01:00","普通", "大阪", "url")
            val tmpDet2 = RouteDetails(0,routeListItems[0].dataId, 0,"02:00","普通", "大阪", "url")
            val tmpDet3 = RouteDetails(0,routeListItems[0].dataId, 0,"03:00","普通", "大阪", "url")
            val tmpDet4 = RouteDetails(0,routeListItems[0].dataId, 0,"04:00","普通", "大阪", "url")
            val tmpDet5 = RouteDetails(0,routeListItems[0].dataId, 0,"05:00","普通", "大阪", "url")
            val tmpDet6 = RouteDetails(0,routeListItems[0].dataId, 0,"06:00","普通", "大阪", "url")
            val tmpDet7 = RouteDetails(0,routeListItems[0].dataId, 0,"07:00","普通", "大阪", "url")
            val tmpDet8 = RouteDetails(0,routeListItems[0].dataId, 0,"08:00","普通", "大阪", "url")
            val tmpDet9 = RouteDetails(0,routeListItems[0].dataId, 0,"09:00","普通", "大阪", "url")
            val tmpDet10 = RouteDetails(0,routeListItems[0].dataId, 0,"10:00","普通", "大阪", "url")

            dataSource.clearAllRouteDetailsItem()
            dataSource.insertRouteDetailsItem(tmpDet1)
            dataSource.insertRouteDetailsItem(tmpDet2)
            dataSource.insertRouteDetailsItem(tmpDet3)
            dataSource.insertRouteDetailsItem(tmpDet4)
            dataSource.insertRouteDetailsItem(tmpDet5)
            dataSource.insertRouteDetailsItem(tmpDet6)
            dataSource.insertRouteDetailsItem(tmpDet7)
            dataSource.insertRouteDetailsItem(tmpDet8)
            dataSource.insertRouteDetailsItem(tmpDet9)
            dataSource.insertRouteDetailsItem(tmpDet10)
        }


        // endregion *************仮データ挿入*************

        // 変更監視
        routeListViewModel.routeList.observe(viewLifecycleOwner, Observer {
            it?.let{
                adapter.submitList(it)
                Log.d("Debug", "データ更新 : ${routeListViewModel.routeList.value.toString()}")
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }
}