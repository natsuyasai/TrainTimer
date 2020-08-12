package com.nyasai.traintimer.routelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.databinding.ListItemRouteListBinding

class RouteListAdapter : ListAdapter<RouteListItem, RouteListAdapter.ViewHolder>(RouteListItemDiffCallback()){

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        private val binding: ListItemRouteListBinding): RecyclerView.ViewHolder(binding.root) {

        /**
         * バインド実行
         */
        fun bind(item: RouteListItem) {
            binding.routeListItem = item
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