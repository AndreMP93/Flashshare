package com.example.flashshare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashshare.model.CommentModel
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.repository.CommentRepository
import com.example.flashshare.service.repository.PostRepository
import com.example.flashshare.service.repository.local.SecurityPreferences
import kotlinx.coroutines.launch

class PostDetailsViewModel(application: Application): AndroidViewModel(application) {
    private val sharedPreferences = SecurityPreferences(application.applicationContext)
    private val postRepository = PostRepository()
    private val commentRepository = CommentRepository()

    private val _loadPostProcess = MutableLiveData<ResultModel<PostModel>>()
    val loadPostProcess: LiveData<ResultModel<PostModel>> = _loadPostProcess

    private val _updatePostProcess = MutableLiveData<ResultModel<Unit>>()
    val updatePostProcess: LiveData<ResultModel<Unit>> = _updatePostProcess

    private val _loadCommentsProcess = MutableLiveData<ResultModel<List<CommentModel>>>()
    val loadCommentsProcess: LiveData<ResultModel<List<CommentModel>>> = _loadCommentsProcess

    private val _createCommentProcess = MutableLiveData<ResultModel<Unit>>()
    val createCommentProcess: LiveData<ResultModel<Unit>> = _createCommentProcess

    private var uId: String = sharedPreferences.get(AppConstants.SHARED.USER_ID)

    fun getPost(postId: String){
        viewModelScope.launch {
            _loadPostProcess.value = postRepository.getPost(uId, postId)
        }
    }

    fun getPost(userId: String, postId: String){
        viewModelScope.launch {
            _loadPostProcess.value = postRepository.getPost(userId, postId)
        }
    }

    fun updatePost(postId: String, post: PostModel){
        viewModelScope.launch {
            val result = postRepository.updatePost(uId, postId, post)
        }
    }

    fun updatePost(userId: String, postId: String, post: PostModel){
        viewModelScope.launch {
            _updatePostProcess.value = postRepository.updatePost(userId, postId, post)
        }
    }

    fun getComments(postId: String){
        viewModelScope.launch {
            _loadCommentsProcess.value = commentRepository.getComments(uId, postId)
        }
    }

    fun getComments(userId: String, postId: String){
        viewModelScope.launch {
            _loadCommentsProcess.value = commentRepository.getComments(userId, postId)
        }
    }

    fun createComments(postId: String, comment: CommentModel){
        viewModelScope.launch {
            _createCommentProcess.value = commentRepository.addComment(uId, postId, comment)
        }
    }

    fun createComments(userId: String, postId: String, comment: CommentModel){
        viewModelScope.launch {
            _createCommentProcess.value = commentRepository.addComment(userId, postId, comment)
        }
    }

}