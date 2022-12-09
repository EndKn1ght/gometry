package com.capstone.gometry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.gometry.R
import com.capstone.gometry.databinding.CardRankingBinding
import com.capstone.gometry.model.User
import com.capstone.gometry.utils.ViewExtensions.setImageFromUrl

class LeaderboardAdapter : ListAdapter<User, LeaderboardAdapter.ViewHolder>(DiffUtilCallback) {
    private lateinit var onStartActivityCallback: OnStartActivityCallback

    inner class ViewHolder(private var binding: CardRankingBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(context: Context, user: User) {
                binding.apply {
                    ivPhoto.setImageFromUrl(context, user.photoUrl!!)
                    tvName.text = user.displayName!!
                    tvPoint.text = String.format(context.getString(R.string.current_point), if (user.point == null) "0" else user.point.toString())
                    root.setOnClickListener {
                        onStartActivityCallback.onStartActivityCallback(user)
                    }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(holder.itemView.context, user)
    }

    fun setOnStartActivityCallback(onStartActivityCallback: OnStartActivityCallback) {
        this.onStartActivityCallback = onStartActivityCallback
    }

    interface OnStartActivityCallback {
        fun onStartActivityCallback(user: User)
    }

    companion object {
        private val DiffUtilCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean  =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem
        }
    }
}