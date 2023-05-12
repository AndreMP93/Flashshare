package com.example.flashshare.viewmodel

import android.app.Application
import android.net.Uri
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

class PublicationPostViewModel(application: Application): AndroidViewModel(application) {
    private val postRepository = PostRepository()
    private val sharedPreferences = SecurityPreferences(application.applicationContext)

    private val _publicationProcess = MutableLiveData<ResultModel<Unit>>()
    val publicationProcess: LiveData<ResultModel<Unit>> = _publicationProcess

    fun publicationPost(post: PostModel, image: Uri){
        viewModelScope.launch {
            val userId = sharedPreferences.get(AppConstants.SHARED.USER_ID)
            if(userId!=null && userId!= ""){
                post.userId = userId
                _publicationProcess.value = postRepository.publicationPost(userId, post, image)
            }


        }
    }
}