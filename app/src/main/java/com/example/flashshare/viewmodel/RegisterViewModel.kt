package com.example.flashshare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.FirebaseListener
import com.example.flashshare.service.repository.AuthRepository
import com.example.flashshare.service.repository.UserRepository
import com.example.flashshare.service.repository.local.SecurityPreferences
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    private val securityPreferences = SecurityPreferences(application.applicationContext)

    private val _registerProcess = MutableLiveData<ResultModel<UserModel>>()
    val registerProcess: LiveData<ResultModel<UserModel>> = _registerProcess

    private val _saveDataProcess = MutableLiveData<ResultModel<Unit>>()
    val saveDateProcess: LiveData<ResultModel<Unit>> = _saveDataProcess

    fun registerUser(user: UserModel){
        viewModelScope.launch {
            _registerProcess.value = ResultModel.Loading
            val result = authRepository.registerUser(user)
            _registerProcess.value = result
            if(result is ResultModel.Success){
                securityPreferences.save(AppConstants.SHARED.USER_ID, result.data.id)
                securityPreferences.save(AppConstants.SHARED.USER_EMAIL, result.data.email)
                saveUserData(result.data)
            }
        }
    }

    private fun saveUserData(user: UserModel){
        viewModelScope.launch{
            _saveDataProcess.value = ResultModel.Loading
            _saveDataProcess.value = userRepository.addUser(user)
        }
    }
}