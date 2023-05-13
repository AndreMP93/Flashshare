package com.example.flashshare.activity.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.flashshare.R
import com.example.flashshare.databinding.GridPostItemBinding
import com.example.flashshare.model.PostModel


class GridAdapter(private val context: Context, private var listPosts: List<PostModel>):
        ArrayAdapter<PostModel>(context, R.layout.grid_post_item, listPosts){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        println("TESTE: -> getView ${position}")
        val itemBinding: GridPostItemBinding
        if (convertView == null) {
            itemBinding = GridPostItemBinding.inflate(LayoutInflater.from(context), parent, false)
            itemBinding.root.tag = itemBinding
        } else {
            itemBinding = convertView.tag as GridPostItemBinding
        }

        val currentItem = listPosts[position] //getItem(position)
        itemBinding.postImageView.contentDescription = currentItem?.description
        if (currentItem?.urlPhotoPost != ""){
            Glide.with(context)
                .load(currentItem?.urlPhotoPost)
                .listener(object: RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Toast.makeText(context,e?.message ,Toast.LENGTH_LONG).show()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        itemBinding.progressGridItem.visibility = View.GONE
                        return false
                    }
                })
                .into(itemBinding.postImageView)
        }
        return itemBinding.root
    }

    fun updateItems(newListPosts: List<PostModel>){
        listPosts = newListPosts
        notifyDataSetChanged()
    }
}