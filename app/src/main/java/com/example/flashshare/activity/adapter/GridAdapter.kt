package com.example.flashshare.activity.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.flashshare.R
import com.example.flashshare.databinding.GridPostItemBinding
import com.example.flashshare.model.PostModel
import com.example.flashshare.service.helper.CustomScaleType
import com.example.flashshare.service.listener.GridListener

class GridAdapter(
    private val context: Context,
    private var listPosts: List<PostModel>,
    private val listener: GridListener
) : RecyclerView.Adapter<GridAdapter.ItemGridViewHolder>() {

    class ItemGridViewHolder(
        private val itemBinding: GridPostItemBinding,
        private val listener: GridListener
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(postModel: PostModel, context: Context) {

            val requestListener = object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    itemBinding.postImageView.setImageResource(R.drawable.ic_logo)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        CustomScaleType.applyCustomScaleType(itemBinding.postImageView, resource)
                    }
                    return false
                }
            }

            Glide.with(context)
                .load(postModel.urlPhotoPost)
                .addListener(requestListener)
                .into(itemBinding.postImageView)

            itemView.setOnClickListener {
                listener.onClick(postModel.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemGridViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = GridPostItemBinding.inflate(inflater, parent, false)
        return ItemGridViewHolder(itemBinding, listener)
    }

    override fun getItemCount(): Int {
        return listPosts.size
    }

    override fun onBindViewHolder(holder: ItemGridViewHolder, position: Int) {
        holder.bind(listPosts[position], context)
    }

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val itemBinding: GridPostItemBinding
//        if (convertView == null) {
//            itemBinding = GridPostItemBinding.inflate(LayoutInflater.from(context), parent, false)
//            itemBinding.root.tag = itemBinding
//        } else {
//            itemBinding = convertView.tag as GridPostItemBinding
//        }
//
//        val currentItem = listPosts[position]
////        itemBinding.postImageView.minimumHeight = 300
////        itemBinding.postImageView.minimumWidth = 300
//
//        itemBinding.postImageView.setOnClickListener {
//            listener.onClick(currentItem.id)
//        }
//
//        val imageView = itemBinding.postImageView
//        val urlImage = currentItem.urlPhotoPost
//
//        imageView.tag = urlImage
//
//        if (currentItem.urlPhotoPost != "") {
//
//            CustomScaleType.loadImageWithApplyCustomScaleType(
//                imageView,
//                urlImage,
//                context
//            )
//
//        }
//        return itemBinding.root
//    }

    fun updateItems(newListPosts: List<PostModel>) {
        listPosts = newListPosts
        notifyDataSetChanged()
    }


}