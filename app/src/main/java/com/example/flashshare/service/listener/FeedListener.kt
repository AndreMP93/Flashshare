package com.example.flashshare.service.listener

import com.example.flashshare.model.PostModel
import com.example.flashshare.model.UserModel

interface FeedListener {

    fun onClick(post: PostModel)

    fun likeClick(postId: String)

    fun userProfileClick(userId: String)

}