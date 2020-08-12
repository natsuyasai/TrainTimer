package com.nyasai.traintimer.routelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.databinding.ListItemRouteListBinding

class RouteListAdapter : ListAdapter<RouteListItem, RouteListAdapter.ViewHolder>(RouteListItemDiffCallback()){

    // アイテムクリックリスナ
    lateinit var clickListener: OnItemClickListener

    /**
     * ViewHolderに表示するデータを設定
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    /**
     * ViewHolder生成
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // region アイテムクリックリスナ
    interface OnItemClickListener{
        fun onItemClickListener(view: View, item: RouteListItem)
    }

    /**
     * アイテムクリックリスナ登録
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    // endregion アイテムクリックリスナ

    class ViewHolder private constructor(
        private val binding: ListItemRouteListBinding): RecyclerView.ViewHolder(binding.root) {

        /**
         * バインド実行
         */
        fun bind(item: RouteListItem, listener: OnItemClickListener) {
            binding.routeListItem = item
            // クリックイベント登録
            binding.root.setOnClickListener{
                listener.onItemClickListener(it, item)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRouteListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class RouteListItemDiffCallback : DiffUtil.ItemCallback<RouteListItem>() {
    override fun areItemsTheSame(oldItem: RouteListItem, newItem: RouteListItem): Boolean {
        return oldItem.dataId == newItem.dataId
    }

    override fun areContentsTheSame(oldItem: RouteListItem, newItem: RouteListItem): Boolean {
        return oldItem == newItem
    }
}