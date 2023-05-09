package com.example.flashshare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.repository.AuthRepository
import com.example.flashshare.service.repository.local.SecurityPreferences
import kotlinx.coroutines.launch

class LoginViewModel(application: Application): AndroidViewModel(application) {

    private val authRepository = AuthRepository()
    private val securityPreferences = SecurityPreferences(application.applicationContext)

    private val _loginProcess = MutableLiveData<ResultModel<UserModel>>()
    val loginProcess: LiveData<ResultModel<UserModel>> = _loginProcess

    private val _loggedUser = MutableLiveData<Boolean>()
    val loggedUser: LiveData<Boolean> = _loggedUser

    fun login(email: String, password: String){
        viewModelScope.launch {
            _loginProcess.value = ResultModel.Loading
            val result = authRepository.doLogin(email, password)
            if(result is ResultModel.Success){
                securityPreferences.save(AppConstants.SHARED.USER_ID, result.data.id)
                securityPreferences.save(AppConstants.SHARED.USER_EMAIL, result.data.email)
            }
            _loginProcess.value = result
        }
    }

    fun verifyLoggedUser(){
        val userId = securityPreferences.get(AppConstants.SHARED.USER_ID)
        val userEmail = securityPreferences.get(AppConstants.SHARED.USER_EMAIL)

        val logged = (userId != "" && userEmail != "")
        _loggedUser.value = logged
    }

}