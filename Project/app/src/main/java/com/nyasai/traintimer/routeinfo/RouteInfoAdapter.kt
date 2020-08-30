package com.nyasai.traintimer.routeinfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.databinding.ListItemRouteInfoBinding

/**
 * 路線情報詳細リスト表示用アダプタ
 */
class RouteInfoAdapter : ListAdapter<RouteDetail, RouteInfoAdapter.ViewHolder>(RouteInfoDiffCallback()){

    // 表示アイテム
    private var _item: List<RouteDetail>? = null

    /**
     * ViewHolderに表示するデータを設定
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    /**
     * ViewHolder生成
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /**
     * 表示データ設定
     */
    override fun submitList(list: List<RouteDetail>?) {
        super.submitList(list)
        _item = list
    }

    /**
     * アイテム位置取得
     */
    fun indexOf(item: RouteDetail): Int {
        return _item?.indexOf(item) ?: -1
    }


    /**
     * ViewHolder
     */
    class ViewHolder private constructor(
        private val binding: ListItemRouteInfoBinding
    ): RecyclerView.ViewHolder(binding.root) {

        /**
         * バインド実行
         */
        fun bind(item: RouteDetail) {
            binding.routeDetail = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRouteInfoBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

/**
 * 比較コールバック
 */
class RouteInfoDiffCallback : DiffUtil.ItemCallback<RouteDetail>() {
    override fun areItemsTheSame(oldItem: RouteDetail, newItem: RouteDetail): Boolean {
        return oldItem.dataId == newItem.dataId
    }

    override fun areContentsTheSame(oldItem: RouteDetail, newItem: RouteDetail): Boolean {
        return oldItem == newItem
    }
}