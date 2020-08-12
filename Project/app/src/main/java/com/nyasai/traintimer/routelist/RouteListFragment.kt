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
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteDatabase
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

        // TODO:リストアイテム選択時の移動
        //sleepTrackerViewModel.navigateTo

        val adapter = RouteListAdapter()
        binding.routeListView.adapter = adapter

        // region *************仮データ挿入*************
        val tmp1 = RouteListItem(0,"JR 宝塚線", "草野駅", "大阪方面")
        GlobalScope.async{
            dataSource.insertRouteListItem(tmp1)
        }


        // endregion *************仮データ挿入*************

        // 変更監視
        routeListViewModel.routeList.observe(viewLifecycleOwner, Observer {
            it?.let{
                adapter.submitList(it)
                Log.d("Debug", routeListViewModel.routeList.value.toString())
            }
        })

        Log.d("Debug", routeListViewModel.routeList.value.toString())
        // Inflate the layout for this fragment
        return binding.root
    }
}