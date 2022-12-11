package com.capstone.gometry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.gometry.databinding.CardAchievementBinding
import com.capstone.gometry.model.Achievement
import com.capstone.gometry.utils.ViewExtensions.setImageFromResource

class AchievementAdapter : ListAdapter<Achievement, AchievementAdapter.ViewHolder>(DiffUtilCallback) {
    inner class ViewHolder(private var binding: CardAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(context: Context, achievement: Achievement) {
                binding.apply {
                    tvName.text = achievement.name
                    ivMedal.setImageFromResource(context, achievement.medal)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val achievement = getItem(position)
        holder.bind(holder.itemView.context, achievement)
    }

    companion object {
        private val DiffUtilCallback = object : DiffUtil.ItemCallback<Achievement>() {
            override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean  =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean =
                oldItem == newItem
        }
    }
}