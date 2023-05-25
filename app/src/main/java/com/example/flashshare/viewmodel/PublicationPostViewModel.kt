package com.example.flashshare.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashshare.R
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.repository.PostRepository
import com.example.flashshare.service.repository.local.SecurityPreferences
import kotlinx.coroutines.launch

class PublicationPostViewModel(private val application: Application): AndroidViewModel(application) {
    private val postRepository = PostRepository()
    private val sharedPreferences = SecurityPreferences(application.applicationContext)

    private val _publicationProcess = MutableLiveData<ResultModel<Unit>>()
    val publicationProcess: LiveData<ResultModel<Unit>> = _publicationProcess

    private val _loadPostProcess = MutableLiveData<ResultModel<PostModel>>()
    val loadPostProcess: LiveData<ResultModel<PostModel>> = _loadPostProcess

    fun publicationPost(post: PostModel){
        viewModelScope.launch {
            if(post.id == ""){
                _publicationProcess.value = ResultModel.Loading
                val userId = sharedPreferences.get(AppConstants.SHARED.USER_ID)
                if(userId!= ""){
                    post.userId = userId
                    _publicationProcess.value = postRepository.publicationPost(userId, post, post.imageUri!!)
                }else{
                    _publicationProcess.value = ResultModel.Error(application.getString(R.string.error_message))
                }
            }else{
                _publicationProcess.value = postRepository.updatePost(post.id, post)
            }
        }
    }

    fun getPostData(postId: String){
        viewModelScope.launch {
            _loadPostProcess.value = postRepository.getPost(postId)
        }
    }
}