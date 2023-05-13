package com.example.flashshare.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flashshare.databinding.RowSearchListBinding
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.listener.SearchListener

class SearchAdapter(var context: Context, private var usersList: List<UserModel>, private var listener: SearchListener)
    : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(private val itemBinding: RowSearchListBinding, private val listener: SearchListener)
        : RecyclerView.ViewHolder(itemBinding.root){

            fun bind(user: UserModel, context: Context){
                itemBinding.nameLabel.text = user.name
                itemBinding.usernameLabel.text = user.username
                if (user.urlPhotoProfile != null && user.urlPhotoProfile != ""){
                    Glide.with(context).load(user.urlPhotoProfile).into(itemBinding.userImage)
                }
                itemView.setOnClickListener { listener.onClick(user.id) }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = RowSearchListBinding.inflate(inflater, parent, false)
        return SearchViewHolder(itemBinding, listener)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(usersList[position], context)
    }

    fun updateUsersList(list: List<UserModel>) {
        usersList = list
        notifyDataSetChanged()
    }
}