package com.example.flashshare.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.repository.UserRepository
import com.example.flashshare.service.repository.local.SecurityPreferences
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application): AndroidViewModel(application) {
    private val sharedPreferences = SecurityPreferences(application.applicationContext)
    private val userRepository = UserRepository()

    private val _updateProcess = MutableLiveData<ResultModel<Unit>>()
    val updateProcess: LiveData<ResultModel<Unit>> = _updateProcess

    private val _loadProcess = MutableLiveData<ResultModel<UserModel>>()
    val loadProcess: LiveData<ResultModel<UserModel>> = _loadProcess

    private val _savePhotoProcess = MutableLiveData<ResultModel<UserModel>>()
    val savePhotoProcess: LiveData<ResultModel<UserModel>> = _savePhotoProcess


    fun getUserData(){
        viewModelScope.launch {
            _loadProcess.value = ResultModel.Loading
            val userId = sharedPreferences.get(AppConstants.SHARED.USER_ID)
            if(userId != ""){
                val result = userRepository.getUser(userId)
                _loadProcess.value = result
            }
        }
    }

    fun update(user: UserModel){
        viewModelScope.launch {
            _updateProcess.value = userRepository.updateUser(user)
        }
    }

    fun savePhotoProfile(user: UserModel, image: Uri){
        viewModelScope.launch {
            val result = userRepository.savePhotoProfile(user, image)
            _savePhotoProcess.value = result
            if(result is ResultModel.Success){
                userRepository.updateUser(result.data)
            }
        }
    }
}