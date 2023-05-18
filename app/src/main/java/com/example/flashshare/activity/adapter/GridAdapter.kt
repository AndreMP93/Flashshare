package com.example.flashshare.activity.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.flashshare.R
import com.example.flashshare.databinding.GridPostItemBinding
import com.example.flashshare.model.PostModel
import com.example.flashshare.service.listener.GridListener


class GridAdapter(private val context: Context, private var listPosts: List<PostModel>, private val listener: GridListener):
        ArrayAdapter<PostModel>(context, R.layout.grid_post_item, listPosts){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemBinding: GridPostItemBinding
        if (convertView == null) {
            itemBinding = GridPostItemBinding.inflate(LayoutInflater.from(context), parent, false)
            itemBinding.root.tag = itemBinding
        } else {
            itemBinding = convertView.tag as GridPostItemBinding
        }

        val currentItem = listPosts[position] //getItem(position)
//        itemBinding.postImageView.layoutParams.height = itemBinding.postImageView.width
//        itemBinding.postImageView.requestLayout()
        itemBinding.postImageView.minimumHeight = 300
        itemBinding.postImageView.minimumWidth = 300
        itemBinding.postImageView.contentDescription = currentItem?.description
        itemBinding.postImageView.setOnClickListener {
            listener.onClick(currentItem.id)
        }

        if (currentItem.urlPhotoPost != ""){

            itemBinding.postImageView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    itemBinding.postImageView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val targetWidth = itemBinding.postImageView.width
                    val targetHeight = itemBinding.postImageView.height

                    Glide.with(context)
                        .load(currentItem.urlPhotoPost)
                        .into(object : CustomTarget<Drawable>(){
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

                                    itemBinding.postImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                                    itemBinding.postImageView.setImageDrawable(resource)
                                }else {
                                    val scaleFactor = targetWidth.toFloat() / originalWidth
                                    val scaledHeight = (originalHeight * scaleFactor).toInt()
                                    resource.setBounds(0, 0, targetWidth, scaledHeight)

                                    itemBinding.postImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                                    itemBinding.postImageView.setImageDrawable(resource)
                                }

                                // Após definir a escala do ImageView como FIT_XY
                                val layoutParams = itemBinding.root.layoutParams
                                layoutParams.height = itemBinding.postImageView.width
                                itemBinding.root.layoutParams = layoutParams

                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }

                        })
                }
            })

        }
        return itemBinding.root
    }

    fun updateItems(newListPosts: List<PostModel>){
        listPosts = newListPosts
        notifyDataSetChanged()
    }
}