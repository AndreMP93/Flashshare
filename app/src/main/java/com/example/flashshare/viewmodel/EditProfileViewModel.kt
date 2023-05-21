package com.example.flashshare.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.repository.FollowRepository
import com.example.flashshare.service.repository.PostRepository
import com.example.flashshare.service.repository.UserRepository
import com.example.flashshare.service.repository.local.SecurityPreferences
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application): AndroidViewModel(application) {
    private val sharedPreferences = SecurityPreferences(application.applicationContext)
    private val userRepository = UserRepository()
    private val postRepository = PostRepository()
    private val followRepository = FollowRepository()

    private val _updateProcess = MutableLiveData<ResultModel<Unit>>()
    val updateProcess: LiveData<ResultModel<Unit>> = _updateProcess

    private val _loadProcess = MutableLiveData<ResultModel<UserModel>>()
    val loadProcess: LiveData<ResultModel<UserModel>> = _loadProcess

    private val _followerQuantity = MutableLiveData<ResultModel<Int>>()
    val followerQuantity: LiveData<ResultModel<Int>> = _followerQuantity

    private val _followingQuantity = MutableLiveData<ResultModel<Int>>()
    val followingQuantity: LiveData<ResultModel<Int>> = _followingQuantity

    private val _postsQuantity = MutableLiveData<ResultModel<Int>>()
    val postsQuantity: LiveData<ResultModel<Int>> = _postsQuantity

    private val _loadPosts = MutableLiveData<ResultModel<List<PostModel>>>()
    val loadPosts: LiveData<ResultModel<List<PostModel>>> = _loadPosts

    private val _savePhotoProcess = MutableLiveData<ResultModel<UserModel>>()
    val savePhotoProcess: LiveData<ResultModel<UserModel>> = _savePhotoProcess

    private var userId: String = sharedPreferences.get(AppConstants.SHARED.USER_ID)

    fun getUserData(){
        viewModelScope.launch {
            _loadProcess.value = ResultModel.Loading
            if(userId != ""){
                val result = userRepository.getUser(userId)
                _loadProcess.value = result
            }
        }
    }

    fun update(user: UserModel){
        viewModelScope.launch {
            _updateProcess.value = ResultModel.Loading
            _updateProcess.value = userRepository.updateUser(user)
        }
    }

    fun savePhotoProfile(user: UserModel, image: Uri){
        viewModelScope.launch {
            _savePhotoProcess.value = ResultModel.Loading
            val result = userRepository.savePhotoProfile(user, image)
            _savePhotoProcess.value = result
            if(result is ResultModel.Success){
                userRepository.updateUser(result.data)
            }
        }
    }

    fun getPosts(){
        viewModelScope.launch {
            if(userId != ""){
                _loadPosts.value = postRepository.getPosts(userId)
            }

        }
    }

    fun getFollowerQuantity(){
        viewModelScope.launch {
            if(userId != ""){
                _followerQuantity.value = followRepository.getQuantityFollowers(userId)
            }
        }
    }

    fun getFollowingQuantity(){
        viewModelScope.launch {
            if(userId != ""){
                _followingQuantity.value = followRepository.getQuantityFollowing(userId)
            }
        }
    }

    fun getPostsQuantity(){
        viewModelScope.launch {
            if(userId != ""){
                _postsQuantity.value = postRepository.getPostsQuantity(userId)
            }
        }
    }
}