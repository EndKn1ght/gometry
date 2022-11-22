package com.example.capstonegometry.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

object ViewExtensions {
    fun ImageView.setImageFromResource(context: Context, res: Int) {
        Glide
            .with(context)
            .load(res)
            .into(this)
    }
}