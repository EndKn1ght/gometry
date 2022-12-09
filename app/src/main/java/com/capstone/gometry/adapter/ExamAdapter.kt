package com.capstone.gometry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.gometry.R
import com.capstone.gometry.databinding.CardExamBinding
import com.capstone.gometry.model.Exam
import com.capstone.gometry.utils.ViewExtensions.setImageFromResource
import com.capstone.gometry.utils.ViewExtensions.setVisible

class ExamAdapter : ListAdapter<Exam, ExamAdapter.ViewHolder>(DiffUtilCallback) {
    private lateinit var onStartActivityCallback: OnStartActivityCallback

    inner class ViewHolder(private var binding: CardExamBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(context: Context, exam: Exam) {
                binding.apply {
                    ivIcon.setImageFromResource(context, exam.icon)
                    tvLevel.text = exam.level
                    tvPoint.text = String.format(context.getString(R.string.point_will_get), exam.point)
                    ivLocked.setVisible(exam.locked)
                    root.setOnClickListener {
                        onStartActivityCallback.onStartActivityCallback(exam)
                    }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardExamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exam = getItem(position)
        holder.bind(holder.itemView.context, exam)
    }

    fun setOnStartActivityCallback(onStartActivityCallback: OnStartActivityCallback) {
        this.onStartActivityCallback = onStartActivityCallback
    }

    interface OnStartActivityCallback {
        fun onStartActivityCallback(exam: Exam)
    }

    companion object {
        private val DiffUtilCallback = object : DiffUtil.ItemCallback<Exam>() {
            override fun areItemsTheSame(oldItem: Exam, newItem: Exam): Boolean  =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Exam, newItem: Exam): Boolean =
                oldItem == newItem
        }
    }
}