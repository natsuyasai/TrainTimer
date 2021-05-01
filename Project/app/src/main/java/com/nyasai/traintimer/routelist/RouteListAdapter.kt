package com.nyasai.traintimer.routelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.databinding.ListItemRouteListBinding

/**
 * 路線一覧用アダプタ
 */
class RouteListAdapter :
    ListAdapter<RouteListItem, RouteListAdapter.ViewHolder>(RouteListItemDiffCallback()) {

    // アイテムクリックリスナ
    private lateinit var clickListener: OnItemClickListener

    private lateinit var longClickListener: OnItemLongClickListener

    /**
     * ViewHolderに表示するデータを設定
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener, longClickListener)
    }

    /**
     * ViewHolder生成
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // region アイテムクリックリスナ

    /**
     * アイテムクリックリスナIF
     */
    interface OnItemClickListener {
        fun onItemClickListener(view: View, item: RouteListItem)
    }

    /**
     * アイテム長押しリスナIF
     */
    interface OnItemLongClickListener {
        fun onItemLongClickListener(view: View, item: RouteListItem): Boolean
    }

    /**
     * アイテムクリックリスナ登録
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    /**
     * アイテム長押しリスナ登録
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        longClickListener = listener
    }

    // endregion アイテムクリックリスナ

    // region ViewHolder
    /**
     * ViewHolder
     */
    class ViewHolder private constructor(
        private val binding: ListItemRouteListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * バインド実行
         */
        fun bind(
            item: RouteListItem,
            clickListener: OnItemClickListener,
            longClickListener: OnItemLongClickListener
        ) {
            binding.routeListItem = item
            // クリックイベント登録
            binding.root.setOnClickListener {
                clickListener.onItemClickListener(it, item)
            }
            binding.root.setOnLongClickListener {
                longClickListener.onItemLongClickListener(it, item)
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

    // endregion ViewHolder
}

/**
 * 路線一覧アイテム比較コールバック
 */
class RouteListItemDiffCallback : DiffUtil.ItemCallback<RouteListItem>() {
    override fun areItemsTheSame(oldItem: RouteListItem, newItem: RouteListItem): Boolean {
        return oldItem.dataId == newItem.dataId
    }

    override fun areContentsTheSame(oldItem: RouteListItem, newItem: RouteListItem): Boolean {
        return oldItem == newItem
    }
}