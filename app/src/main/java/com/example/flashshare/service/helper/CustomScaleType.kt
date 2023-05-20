package com.example.flashshare.service.helper

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class CustomScaleType {

    companion object {
        fun applyCustomScaleType(imageView: ImageView, drawable: Drawable) {
            val originalWidth = drawable.intrinsicWidth
            val originalHeight = drawable.intrinsicHeight

            val targetHeight = imageView.height
            val targetWidth = imageView.width

            val scaledDrawable: Drawable

            scaledDrawable = if (originalWidth > originalHeight) {
                val scaleFactor = targetHeight.toFloat() / originalHeight
                val scaledWidth = (originalWidth * scaleFactor).toInt()
                drawable.setBounds(0, 0, scaledWidth, targetHeight)

                drawable

            } else {
                val scaleFactor = targetWidth.toFloat() / originalWidth
                val scaledHeight = (originalHeight * scaleFactor).toInt()
                drawable.setBounds(0, 0, targetWidth, scaledHeight)

                drawable
            }

            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setImageDrawable(scaledDrawable)
        }


    }
}