package com.capstone.gometry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.gometry.databinding.CardGeometryArBinding
import com.capstone.gometry.model.GeometryAR
import com.capstone.gometry.utils.ViewExtensions.setImageFromResource

class GeometryARAdapter : ListAdapter<GeometryAR, GeometryARAdapter.ViewHolder>(DiffUtilCallback) {
    private lateinit var onStartActivityCallback: OnStartActivityCallback

    inner class ViewHolder(private var binding: CardGeometryArBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(context: Context, geometryAR: GeometryAR) {
                binding.apply {
                    ivPreview.setImageFromResource(context, geometryAR.preview)
                    root.setOnClickListener {
                        onStartActivityCallback.onStartActivityCallback(geometryAR)
                    }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardGeometryArBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val geometryAr = getItem(position)
        holder.bind(holder.itemView.context, geometryAr)
    }

    fun setOnStartActivityCallback(onStartActivityCallback: OnStartActivityCallback) {
        this.onStartActivityCallback = onStartActivityCallback
    }

    interface OnStartActivityCallback {
        fun onStartActivityCallback(geometryAR: GeometryAR)
    }

    companion object {
        private val DiffUtilCallback = object : DiffUtil.ItemCallback<GeometryAR>() {
            override fun areItemsTheSame(oldItem: GeometryAR, newItem: GeometryAR): Boolean  =
                oldItem.preview == newItem.preview

            override fun areContentsTheSame(oldItem: GeometryAR, newItem: GeometryAR): Boolean =
                oldItem == newItem
        }
    }
}