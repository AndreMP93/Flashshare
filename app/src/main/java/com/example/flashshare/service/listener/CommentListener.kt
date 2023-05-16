package com.example.flashshare.service.listener

interface CommentListener {
    fun onClickEdit(commentId: String)

    fun onClickDelete(commentId: String)
}