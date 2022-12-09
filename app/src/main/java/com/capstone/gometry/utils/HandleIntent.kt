package com.capstone.gometry.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object HandleIntent {
    fun playAR(context: Context, model3dUrl: String) {
        val sceneViewer = Intent(Intent.ACTION_VIEW)
        val intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.0")
            .buildUpon()
            .appendQueryParameter("file", model3dUrl)
            .appendQueryParameter("mode", "ar_preferred")
            .build()
        sceneViewer.data = intentUri
        sceneViewer.setPackage("com.google.ar.core")
        context.startActivity(sceneViewer)
    }
}