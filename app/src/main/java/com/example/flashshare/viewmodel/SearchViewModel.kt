package com.example.flashshare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.repository.UserRepository
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application){
    val userRepository = UserRepository()

    private val _searchProcess =MutableLiveData<ResultModel<List<UserModel>>>()
    val searchProcess: LiveData<ResultModel<List<UserModel>>> = _searchProcess

    fun searchUser(text: String){
        viewModelScope.launch {
            _searchProcess.value = userRepository.getUsers(text.uppercase())
        }
    }
}