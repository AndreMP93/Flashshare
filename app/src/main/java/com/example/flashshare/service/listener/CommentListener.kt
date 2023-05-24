package com.example.flashshare.service.listener

import android.view.View
import com.example.flashshare.model.CommentModel

interface CommentListener {
    fun onClickEdit(comment: CommentModel)

    fun onClickDelete(commentId: String)
}