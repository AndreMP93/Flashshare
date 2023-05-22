package com.example.flashshare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.repository.PostRepository
import com.example.flashshare.service.repository.local.SecurityPreferences
import kotlinx.coroutines.launch

class FeedViewModel(application: Application): AndroidViewModel(application) {
    private val postRepository = PostRepository()
    private val sharedPreferences = SecurityPreferences(application.applicationContext)
    private val currentUserId = sharedPreferences.get(AppConstants.SHARED.USER_ID)

    private val _loadFeedProcess = MutableLiveData<ResultModel<List<PostModel>>>()
    val loadFeedProcess: LiveData<ResultModel<List<PostModel>>> = _loadFeedProcess

    fun getFeed(){
        viewModelScope.launch {
            if(currentUserId != ""){
                _loadFeedProcess.value = postRepository.getFeed(currentUserId)
            }
        }
    }
}