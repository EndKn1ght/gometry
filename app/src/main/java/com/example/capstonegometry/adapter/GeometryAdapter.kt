package com.example.capstonegometry.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.capstonegometry.databinding.CardGeometryBinding
import com.example.capstonegometry.model.Geometry
import com.example.capstonegometry.utils.ViewExtensions.setImageFromResource

class GeometryAdapter(
    private val listOfGeometry: ArrayList<Geometry>
    ): RecyclerView.Adapter<GeometryAdapter.ViewHolder>() {
    private lateinit var onStartActivityCallback: OnStartActivityCallback

    class ViewHolder(var binding: CardGeometryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardGeometryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val geometry = listOfGeometry[position]

        holder.binding.apply {
            tvName.text = geometry.name
            cardGeometry.setCardBackgroundColor(Color.parseColor(geometry.colorScheme))
            ivPreview.setImageFromResource(holder.itemView.context, geometry.preview)
        }
        holder.itemView.setOnClickListener {
            onStartActivityCallback.onStartActivityCallback(geometry)
        }
    }

    override fun getItemCount(): Int = listOfGeometry.size

    fun setOnStartActivityCallback(onStartActivityCallback: OnStartActivityCallback) {
        this.onStartActivityCallback = onStartActivityCallback
    }

    interface OnStartActivityCallback {
        fun onStartActivityCallback(geometry: Geometry)
    }
}