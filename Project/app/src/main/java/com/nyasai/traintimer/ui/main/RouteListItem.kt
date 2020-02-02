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
class RouteListAdapter(context: Context, items: Array<RouteListItem>): ArrayAdapter<RouteListItem>(context, 0) {

    /**
     * 表示アイテム
     */
    private val mRouteItems: Array<RouteListItem>

    /**
     * 表示コンテキスト
     */
    private var mContext: Context

    init {
        mContext = context
        mRouteItems = items
    }

    /**
     * アイテム数取得
     */
    override fun getCount(): Int {
        return mRouteItems.size
    }

    /**
     * アイテム取得
     */
    override fun getItem(position: Int): RouteListItem? {
        return mRouteItems[position]
    }

    /**
     * アイテムID取得
     */
    override fun getItemId(position: Int): Long {
        return mRouteItems[position].dataNo.toLong()
    }

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