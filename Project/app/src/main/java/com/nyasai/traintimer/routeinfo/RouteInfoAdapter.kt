package com.nyasai.traintimer.routeinfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nyasai.traintimer.database.RouteDetails
import com.nyasai.traintimer.databinding.ListItemRouteInfoBinding

class RouteInfoAdapter : ListAdapter<RouteDetails, RouteInfoAdapter.ViewHolder>(RouteInfoDiffCallback()){

    // 表示アイテム
    private var _items: List<RouteDetails>? = null

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
    override fun submitList(list: List<RouteDetails>?) {
        super.submitList(list)
        _items = list
    }

    /**
     * アイテム位置取得
     */
    fun indexOf(item: RouteDetails): Int {
        return _items?.indexOf(item) ?: -1
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
        fun bind(item: RouteDetails) {
            binding.routeDetails = item
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
class RouteInfoDiffCallback : DiffUtil.ItemCallback<RouteDetails>() {
    override fun areItemsTheSame(oldItem: RouteDetails, newItem: RouteDetails): Boolean {
        return oldItem.dataId == newItem.dataId
    }

    override fun areContentsTheSame(oldItem: RouteDetails, newItem: RouteDetails): Boolean {
        return oldItem == newItem
    }
}