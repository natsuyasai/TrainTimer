package com.nyasai.traintimer.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.nyasai.traintimer.R
import com.nyasai.traintimer.databinding.RouteListLayoutBinding

/**
 * 路線リスト表示データ
 */
data class RouteListItem(val dataNo: Int,
                         val routeName: String,
                         val stationName: String,
                         val destination: String,
                         val srcUrl: String,
                         val detailDataName: String)

/**
 * 路線リストアダプタ
 */
class RouteListAdapter(context: Context): ArrayAdapter<RouteListItem>(context, 0) {

    /**
     * ビュー取得
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: RouteListLayoutBinding
        if(convertView === null){
            // 未生成なら新規設定
            binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.route_list_layout, parent, false)
            binding.root.tag = binding
        }
        else{
            // すでに設定済みなら設定済みのものを取得
            binding = convertView.tag as RouteListLayoutBinding
        }
        // 指定位置のアイテム取得
        binding.item = getItem(position)
        return binding.root
    }

}