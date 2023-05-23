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
import com.example.flashshare.databinding.FeedItemBinding
import com.example.flashshare.model.PostModel
import com.example.flashshare.service.helper.CustomScaleType
import com.example.flashshare.service.listener.FeedListener

class FeedAdapter(var context: Context, private var postList: List<PostModel>, private var listener: FeedListener):
RecyclerView.Adapter<FeedAdapter.FeedViewHolder>(){


    class FeedViewHolder(private val itemBinding: FeedItemBinding, private val listener: FeedListener)
        : RecyclerView.ViewHolder(itemBinding.root){

        fun bind(post: PostModel, context: Context){
            Glide.with(context)
                .load(post.urlPhotoPost)
                .addListener(getRequestListener(itemBinding))
                .into(itemBinding.postImageView2)
            if(post.checkUserDataInitialization()){
                itemBinding.userNameText.text = post.userData.name
                Glide.with(context).load(post.userData.urlPhotoProfile).into(itemBinding.photoPrifileImageView)
            }
            if(post.isLiked){
                itemBinding.likeImageView.setImageResource(R.drawable.ic_like)
            }
            itemBinding.descriptionPostTextView.text = post.description

            itemBinding.photoPrifileImageView.setOnClickListener {
                listener.userProfileClick(post.userId)
            }
            itemBinding.userNameText.setOnClickListener {
                listener.userProfileClick(post.userId)
            }
            itemBinding.postImageView2.setOnClickListener {
                listener.onClick(post)
            }
            itemBinding.likeImageView.setOnClickListener {
                if(post.isLiked){
                    itemBinding.likeImageView.setImageResource(R.drawable.ic_not_like)
                }else{
                    itemBinding.likeImageView.setImageResource(R.drawable.ic_like)
                }
                listener.likeClick(post.id)
            }
        }
        private fun getRequestListener(itemBinding: FeedItemBinding): RequestListener<Drawable> {
            return object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    itemBinding.postImageView2.setImageResource(R.drawable.ic_logo)
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
                        CustomScaleType.applyCustomScaleType(itemBinding.postImageView2, resource)
                    }
                    return false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = FeedItemBinding.inflate(inflater, parent, false)
        return FeedViewHolder(itemBinding, listener)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(postList[position], context)
    }

    fun updateListPost(newList: List<PostModel>){
        postList = newList
        notifyDataSetChanged()
    }


}