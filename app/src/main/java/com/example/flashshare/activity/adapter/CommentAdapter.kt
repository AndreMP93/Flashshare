package com.example.flashshare.activity.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flashshare.R
import com.example.flashshare.databinding.CommentItemBinding
import com.example.flashshare.model.CommentModel
import com.example.flashshare.service.listener.CommentListener


class CommentAdapter(var context: Context, private var commentList: List<CommentModel>, private var listener: CommentListener): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(val itemBinding: CommentItemBinding, private val listener: CommentListener)
        : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(comment: CommentModel, context: Context){
            if(comment.isMyComment){
                itemBinding.optionsComment.visibility = View.VISIBLE
                itemBinding.optionsComment.setOnClickListener {
                    val popupMenu = PopupMenu(context , itemBinding.optionsComment)
                    popupMenu.inflate(R.menu.menu_options_post)
                    popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                        override fun onMenuItemClick(item: MenuItem?): Boolean {
                            when(item?.itemId){
                                R.id.delete_option -> {
                                    listener.onClickDelete(comment.id)
                                    return true
                                }
                                R.id.edit_option -> {
                                    listener.onClickEdit(comment)
                                    return true
                                }
                            }
                            return false
                        }
                    })
                    popupMenu.show()
                }
            }

            val spannableStringBuilder = SpannableStringBuilder()
            spannableStringBuilder.append(comment.userName+": ")
            spannableStringBuilder.append(comment.description)

            val boldSpan = StyleSpan(Typeface.BOLD)
            spannableStringBuilder.setSpan(
                boldSpan,
                0,
                comment.userName.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            itemBinding.commentTextView.text = spannableStringBuilder
            if (comment.urlUserPhoto != ""){
                Glide.with(context).load(comment.urlUserPhoto).into(itemBinding.userPhotoImage)
            }

        }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = CommentItemBinding.inflate(inflater, parent, false)
        return CommentViewHolder(itemBinding, listener)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position], context)
    }

    fun updateCommentsList(list: List<CommentModel>) {
        commentList = list
        notifyDataSetChanged()
    }

}


