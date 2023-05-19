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

    companion object{
        fun loadImageWithApplyCustomScaleType(imageView: ImageView, urlImage: String, context: Context){
            imageView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val targetWidth = imageView.width
                    val targetHeight = imageView.height

                    Glide.with(context)
                        .load(urlImage)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                val originalWidth = resource.intrinsicWidth
                                val originalHeight = resource.intrinsicHeight

                                if (originalWidth > originalHeight) {
                                    val scaleFactor = targetHeight.toFloat() / originalHeight
                                    val scaledWidth = (originalWidth * scaleFactor).toInt()
                                    resource.setBounds(0, 0, scaledWidth, targetHeight)

                                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                                    imageView.setImageDrawable(resource)
                                } else {
                                    val scaleFactor = targetWidth.toFloat() / originalWidth
                                    val scaledHeight = (originalHeight * scaleFactor).toInt()
                                    resource.setBounds(0, 0, targetWidth, scaledHeight)

                                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                                    imageView.setImageDrawable(resource)
                                }

                                val layoutParams = imageView.layoutParams
                                layoutParams.height = imageView.width
                                imageView.layoutParams = layoutParams

                            }
                            override fun onLoadCleared(placeholder: Drawable?) {
                            }

                        })
                }
            })
        }
    }
}