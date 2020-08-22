package com.nyasai.traintimer.routeinfo

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
import com.nyasai.traintimer.databinding.FragmentRouteInfoBinding
import com.nyasai.traintimer.define.Define


/**
 * A simple [Fragment] subclass.
 * Use the [RouteInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RouteInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // データバインド設定
        val binding: FragmentRouteInfoBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_route_info, container, false)

        val application = requireNotNull(this.activity).application

        // パラメータ取得
        val arguments = RouteInfoFragmentArgs.fromBundle(requireArguments())

        val dataSource = RouteDatabase.getInstance(application).routeDatabaseDao

        val viewModelFactory = RouteInfoViewModelFactory(dataSource, application, arguments.parentDataId)

        val routeInfoViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(RouteInfoViewModel::class.java)

        binding.routeInfoViewModel = routeInfoViewModel

        binding.lifecycleOwner = this


        // 路線詳細用アダプター設定
        val adapter = RouteInfoAdapter()
        binding.routeInfoView.adapter = adapter

        // 変更監視
        routeInfoViewModel.routeItems.observe(viewLifecycleOwner, Observer {
            it?.let{
                // 表示種別に応じたデータを設定
                adapter.submitList(routeInfoViewModel.getDisplayRouteDetailsItems())
                Log.d("Debug", "詳細データ更新 : ${routeInfoViewModel.routeItems.value.toString()}")
            }
        })

        routeInfoViewModel.routeInfo.observe(viewLifecycleOwner, Observer {
            it?.let{
                Log.d("Debug", "路線データ更新 : ${routeInfoViewModel.routeInfo.value.toString()}")
            }
        })

        return binding.root
    }

}