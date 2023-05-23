package com.example.flashshare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashshare.R
import com.example.flashshare.model.LikedPostModel
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.repository.LikedPostsRepository
import com.example.flashshare.service.repository.PostRepository
import com.example.flashshare.service.repository.UserRepository
import com.example.flashshare.service.repository.local.SecurityPreferences
import kotlinx.coroutines.launch

class FeedViewModel(private val application: Application): AndroidViewModel(application) {
    private val postRepository = PostRepository()
    private val userRepository = UserRepository()
    private val likedPostsRepository = LikedPostsRepository()
    private val sharedPreferences = SecurityPreferences(application.applicationContext)

    private val currentUserId = sharedPreferences.get(AppConstants.SHARED.USER_ID)

    private val _loadFeedProcess = MutableLiveData<ResultModel<List<PostModel>>>()
    val loadFeedProcess: LiveData<ResultModel<List<PostModel>>> = _loadFeedProcess

    private val _loadUserDataProcess = MutableLiveData<ResultModel<UserModel>>()
    val loadUserDataProcess: LiveData<ResultModel<UserModel>> = _loadUserDataProcess

    private val _isLiked = MutableLiveData<ResultModel<Boolean>>()
    val isLiked: LiveData<ResultModel<Boolean>> = _isLiked

    private val _changeLikeProcess = MutableLiveData<ResultModel<Unit>>()
    val changeLikeProcess: LiveData<ResultModel<Unit>> = _changeLikeProcess

    private var isLikedPost = false

    fun getFeed(){
        viewModelScope.launch {
            _loadFeedProcess.value = ResultModel.Loading
            if(currentUserId != ""){
                val result = postRepository.getFeed(currentUserId)
                if(result is ResultModel.Success){
                    val posts = result.data

                    for (post in posts) {
                        if(!post.checkUserDataInitialization()){
                            fetchUserData(post.userId)
                        }
                        checkLikedPost(post.id)
                    }

                    _loadFeedProcess.value = ResultModel.Success(posts)
                }else{
                    _loadFeedProcess.value = ResultModel.Error(application.getString(R.string.error_message))
                }
            }else{
                _loadFeedProcess.value = ResultModel.Error(application.getString(R.string.error_message))
            }
        }
    }

    private fun fetchUserData(userId: String){
        viewModelScope.launch {
            val result = userRepository.getUser(userId)

            if (result is ResultModel.Success) {
                val user = result.data
                val posts = _loadFeedProcess.value
                if(posts is ResultModel.Success){
                    val updatedPosts = posts.data.map { post ->
                        if (post.userId == user.id) {
                            post.userData = user
                        }
                        post
                    }
                    _loadFeedProcess.value = ResultModel.Success(updatedPosts)
                }
            }
        }
    }

    fun changeLike(postId: String){
        viewModelScope.launch {
            if(isLikedPost){
                isLikedPost = false
                _changeLikeProcess.value = likedPostsRepository.removeLike(postId, LikedPostModel(currentUserId))
            }else{
                isLikedPost = true
                _changeLikeProcess.value = likedPostsRepository.addLike(postId, LikedPostModel(currentUserId))
            }
            checkLikedPost(postId)
        }
    }

    private fun checkLikedPost(postId: String){
        viewModelScope.launch {
            val result = likedPostsRepository.checkLikedPost(postId, LikedPostModel(currentUserId))
            val isLikedPost = if (result is ResultModel.Success) result.data else false

            val posts = _loadFeedProcess.value
            if(posts is ResultModel.Success){
                val updatedPosts = posts.data.map { post ->
                    if (post.id == postId) {
                        post.isLiked = isLikedPost
                    }
                    post
                }
                _loadFeedProcess.value = ResultModel.Success(updatedPosts)
            }

        }
    }
}