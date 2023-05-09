package com.example.flashshare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.repository.FollowRepository
import com.example.flashshare.service.repository.UserRepository
import com.example.flashshare.service.repository.local.SecurityPreferences
import kotlinx.coroutines.launch

class FriendProfileViewModel(application: Application): AndroidViewModel(application){

    private val userRepository = UserRepository()
    private val followRepository = FollowRepository()
    private val sharedPreferences = SecurityPreferences(application.applicationContext)


    private val _loadUserDataProcess = MutableLiveData<ResultModel<UserModel>>()
    val loadUserDataProcess: LiveData<ResultModel<UserModel>> = _loadUserDataProcess

    private val _followerQuantity = MutableLiveData<ResultModel<Int>>()
    val followerQuantity: LiveData<ResultModel<Int>> = _followerQuantity

    private val _followingQuantity = MutableLiveData<ResultModel<Int>>()
    val followingQuantity: LiveData<ResultModel<Int>> = _followingQuantity

    private val _isFollowingUser = MutableLiveData<ResultModel<Boolean>>()
    val isFollowingUser: LiveData<ResultModel<Boolean>> = _isFollowingUser

    private val _followProcess = MutableLiveData<ResultModel<Unit>>()
    val followProcess: LiveData<ResultModel<Unit>> = _followProcess

    fun getUserData(userId: String){
        viewModelScope.launch {
            _loadUserDataProcess.value = ResultModel.Loading
            _loadUserDataProcess.value = userRepository.getUser(userId)
        }
    }

    fun getQuantityFollower(friendID: String){
        viewModelScope.launch {
            _followerQuantity.value = followRepository.getQuantityFollowers(friendID)
        }
    }

    fun getQuantityFollowing(friendID: String){
        viewModelScope.launch {
            _followingQuantity.value = followRepository.getQuantityFollowing(friendID)
        }
    }

    fun checkFollowingUser(friendID: String){
        viewModelScope.launch {
            val userId: String = sharedPreferences.get(AppConstants.SHARED.USER_ID)
            _isFollowingUser.value = followRepository.checkFollowUser(userId, friendID)
        }
    }

    fun followUser(friendId: String){
        viewModelScope.launch {
            val userId: String = sharedPreferences.get(AppConstants.SHARED.USER_ID)
            val result = followRepository.followUser(userId, friendId)
            if(result is ResultModel.Success){
                checkFollowingUser(friendId)
                getQuantityFollower(friendId)
                getQuantityFollowing(friendId)
            }
        }
    }

    fun unfollowUser(friendId: String){
        viewModelScope.launch {
            val userId: String = sharedPreferences.get(AppConstants.SHARED.USER_ID)
            val result = followRepository.unfollowUser(userId, friendId)
            if(result is ResultModel.Success){
                checkFollowingUser(friendId)
                getQuantityFollower(friendId)
                getQuantityFollowing(friendId)
            }
        }
    }

}