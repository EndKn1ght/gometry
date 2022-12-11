package com.capstone.gometry.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.gometry.R
import com.capstone.gometry.databinding.ItemOptionBinding
import com.capstone.gometry.utils.ViewExtensions.setBold

class OptionAdapter(private val options: List<String>) : RecyclerView.Adapter<OptionAdapter.ViewHolder>() {
    var answer: String = ""
    var selectedOption: String = ""
    var isChecked: Boolean = false

    inner class ViewHolder(private var binding: ItemOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(option: String) {
            binding.tvOption.apply {
                text = option
                setBold(selectedOption == option)
            }
            binding.root.apply {
                setBackgroundResource(
                    if (selectedOption == option) R.drawable.background_purple_200
                    else R.drawable.background_white
                )
                setOnClickListener {
                    if (!isChecked) {
                        selectedOption = option
                        updateView()
                    }
                }
            }

            if (isChecked && option == answer) {
                binding.apply {
                    tvOption.setBold(true)
                    root.setBackgroundResource(R.drawable.background_teal_200)
                }
            }

            if (isChecked && selectedOption == option && option != answer) {
                binding.apply {
                    tvOption.setBold(true)
                    root.setBackgroundResource(R.drawable.background_red_200)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.bind(option)
    }

    override fun getItemCount(): Int = options.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateView() { notifyDataSetChanged() }
}