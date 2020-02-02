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

    /**
     * binding
     */
    private lateinit var mBinding: RouteListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // バインディングレイアウト取得
        mBinding = DataBindingUtil.inflate<RouteListFragmentBinding>(inflater, R.layout.route_list_fragment, container, false)
        mBinding.setLifecycleOwner(this)
        // アダプタ設定
        val adapter = this.context?.let {
            RouteListAdapter(it,
                arrayOf(
                    RouteListItem(0,"Route0","Station0","Destination0","Url0","Detail0"),
                    RouteListItem(1,"Route1","Station1","Destination1","Url1","Detail1"),
                    RouteListItem(2,"Route2","Station2","Destination2","Url2","Detail2"),
                    RouteListItem(3,"Route3","Station3","Destination3","Url3","Detail3"),
                    RouteListItem(4,"Route4","Station4","Destination4","Url4","Detail4"),
                    RouteListItem(5,"Route5","Station5","Destination5","Url5","Detail5"),
                    RouteListItem(6,"Route6","Station6","Destination6","Url6","Detail6")
                ))
        }
        // リストビューにアダプタ設定
        mBinding.routeListView.adapter = adapter
        mBinding.setOnClickRouteItem { parent, view, position, id ->
            Log.d("Log", position.toString())
        }
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}