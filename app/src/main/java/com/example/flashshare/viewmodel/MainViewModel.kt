package com.example.flashshare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.FirebaseListener
import com.example.flashshare.service.repository.AuthRepository
import com.example.flashshare.service.repository.local.SecurityPreferences
import kotlinx.coroutines.launch

class MainViewModel(application: Application):AndroidViewModel(application) {
    private val authRepository = AuthRepository()
    private val securityPreferences = SecurityPreferences(application.applicationContext)

    private val _logoutProcess = MutableLiveData<ResultModel<Unit>>()
    val logoutProcess: LiveData<ResultModel<Unit>> = _logoutProcess

    fun logout(){
        viewModelScope.launch {
            _logoutProcess.value = ResultModel.Loading
            val result = authRepository.logout()
            if(result is ResultModel.Success){
                securityPreferences.remove(AppConstants.SHARED.USER_EMAIL)
                securityPreferences.remove(AppConstants.SHARED.USER_ID)
            }
            _logoutProcess.value = result
        }

    }
}