package com.example.flashshare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashshare.model.CommentModel
import com.example.flashshare.model.LikedPostModel
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.repository.CommentRepository
import com.example.flashshare.service.repository.LikedPostsRepository
import com.example.flashshare.service.repository.PostRepository
import com.example.flashshare.service.repository.UserRepository
import com.example.flashshare.service.repository.local.SecurityPreferences
import kotlinx.coroutines.launch

class PostDetailsViewModel(application: Application): AndroidViewModel(application) {
    private val sharedPreferences = SecurityPreferences(application.applicationContext)
    private val postRepository = PostRepository()
    private val userRepository = UserRepository()
    private val likedPostsRepository = LikedPostsRepository()
    private val commentRepository = CommentRepository()

    private val _loadPostProcess = MutableLiveData<ResultModel<PostModel>>()
    val loadPostProcess: LiveData<ResultModel<PostModel>> = _loadPostProcess

    private val _loadCommentsProcess = MutableLiveData<ResultModel<List<CommentModel>>>()
    val loadCommentsProcess: LiveData<ResultModel<List<CommentModel>>> = _loadCommentsProcess

    private val _isLiked = MutableLiveData<ResultModel<Boolean>>()
    val isLiked: LiveData<ResultModel<Boolean>> = _isLiked

    private val _createCommentProcess = MutableLiveData<ResultModel<Unit>>()
    val createCommentProcess: LiveData<ResultModel<Unit>> = _createCommentProcess

    private val _changeLikeProcess = MutableLiveData<ResultModel<Unit>>()
    val changeLikeProcess: LiveData<ResultModel<Unit>> = _changeLikeProcess

    private val _loadUserProcess = MutableLiveData<ResultModel<UserModel>>()
    val loadUserProcess: LiveData<ResultModel<UserModel>> = _loadUserProcess

    private var uId: String = sharedPreferences.get(AppConstants.SHARED.USER_ID)
    private var isLikedPost: Boolean = false


    fun getPost(postId: String){
        viewModelScope.launch {
            _loadPostProcess.value = postRepository.getPost(postId)
        }
    }

    fun getComments(postId: String){
        viewModelScope.launch {
            val result = commentRepository.getComments(postId, uId)
            _loadCommentsProcess.value = result
            getUserDataForComment(result)
        }
    }


    fun createComments(postId: String, comment: CommentModel){
        viewModelScope.launch {
            val result = userRepository.getUser(uId)
            if(result is ResultModel.Success){
                comment.userId = uId
                comment.userName = result.data.name
                comment.urlUserPhoto = result.data.urlPhotoProfile ?: ""
                _createCommentProcess.value = commentRepository.addComment(postId, comment)
            }else{
                val error = result as ResultModel.Error
                _createCommentProcess.value = ResultModel.Error(error.message)
            }

        }
    }

    fun updateComment(postId: String, commentId: String, comment: CommentModel){
        viewModelScope.launch {
            commentRepository.updateComment(postId, commentId, comment)
            getComments(postId)
        }
    }
    fun removeComment(postId: String, commentId: String){
        viewModelScope.launch {
            commentRepository.removeComment(postId, commentId)
            getComments(postId)
        }
    }

    fun changeLike(postId: String){
        viewModelScope.launch {
            if(isLikedPost){
                isLikedPost = false
                _changeLikeProcess.value = likedPostsRepository.removeLike(postId, LikedPostModel(uId))
            }else{
                isLikedPost = true
                _changeLikeProcess.value = likedPostsRepository.addLike(postId, LikedPostModel(uId))
            }
            checkLikedPost(postId)
        }
    }

    fun checkLikedPost(postId: String){
        viewModelScope.launch {
            val result = likedPostsRepository.checkLikedPost(postId, LikedPostModel(uId))
            isLikedPost = if(result is ResultModel.Success) result.data else false
            _isLiked.value = result
        }
    }

    fun getUserData(userId: String){
        viewModelScope.launch {
            _loadUserProcess.value = userRepository.getUser(userId)
        }
    }

    private fun getUserDataForComment(result: ResultModel<List<CommentModel>>){
        viewModelScope.launch {
            if(result is ResultModel.Success){
                val listComment = mutableListOf<CommentModel>()
                for(item in result.data){
                    val user = userRepository.getUser(item.userId)
                    if(user is ResultModel.Success){
                        item.userName = user.data.name
                        item.urlUserPhoto = user.data.urlPhotoProfile ?: ""
                        listComment.add(item)
                    }
                }
                _loadCommentsProcess.value = ResultModel.Success(listComment)
            }else{
                _loadCommentsProcess.value = result
            }
        }
    }

    fun deletePost(postId: String){
        viewModelScope.launch {
            postRepository.deletePost(postId)
        }
    }
}