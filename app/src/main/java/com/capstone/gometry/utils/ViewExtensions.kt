package com.capstone.gometry.utils

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

object ViewExtensions {
    fun View.setVisible(state: Boolean) {
        this.visibility = if (state) View.VISIBLE else View.GONE
    }

    fun ImageView.setImageFromResource(context: Context, res: Int) {
        Glide
            .with(context)
            .load(res)
            .into(this)
    }

    fun ImageView.setImageFromUrl(context: Context, url: String) {
        Glide
            .with(context)
            .load(url)
            .into(this)
    }

    fun TextView.setBold(state: Boolean) {
        this.setTypeface(null, if (state) Typeface.BOLD else Typeface.NORMAL)
    }
}