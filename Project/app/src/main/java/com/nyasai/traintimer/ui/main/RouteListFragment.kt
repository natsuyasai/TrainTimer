package com.nyasai.traintimer.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.ListFragment
import com.nyasai.traintimer.R
import com.nyasai.traintimer.databinding.RouteListFragmentBinding

class RouteListFragment: ListFragment() {
    companion object {
        fun newInstance() = RouteListFragment()
    }

    // binding
    private lateinit var _binding: RouteListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate<RouteListFragmentBinding>(inflater, R.layout.route_list_fragment, container, false)
        _binding.setLifecycleOwner(this)
        val adapter = this.context?.let {
            RouteListAdapter(it).apply {
                add(
                    RouteListItem(
                        0,
                        "RouteName1",
                        "StationName1",
                        "Destination1",
                        "SrcUrl1",
                        "DetailDataName1"
                    )
                )
                add(
                    RouteListItem(
                        1,
                        "RouteName2",
                        "StationName2",
                        "Destination2",
                        "SrcUrl2",
                        "DetailDataName2"
                    )
                )
                add(
                    RouteListItem(
                        2,
                        "RouteName3",
                        "StationName3",
                        "Destination3",
                        "SrcUrl3",
                        "DetailDataName3"
                    )
                )
                add(
                    RouteListItem(
                        3,
                        "RouteName4",
                        "StationName4",
                        "Destination4",
                        "SrcUrl4",
                        "DetailDataName4"
                    )
                )
                add(
                    RouteListItem(
                        4,
                        "RouteName5",
                        "StationName5",
                        "Destination5",
                        "SrcUrl5",
                        "DetailDataName5"
                    )
                )
                add(
                    RouteListItem(
                        5,
                        "RouteName6",
                        "StationName6",
                        "Destination6",
                        "SrcUrl6",
                        "DetailDataName6"
                    )
                )
            }
        }
        // リストビューにアダプタ設定
        _binding.routeListView.adapter = adapter
        _binding.setOnClickRouteItem { parent, view, position, id ->
            Log.d("Log", position.toString())
        }
        return _binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        // 表示データ読み込み

    }
}