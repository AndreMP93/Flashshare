package com.example.flashshare.service.repository

import com.example.flashshare.model.UserModel
import com.example.flashshare.service.repository.remote.FirebaseAuthService

class AuthRepository() {
    private val firebaseAuthService =FirebaseAuthService()

    suspend fun doLogin(email:String, password: String) = firebaseAuthService.doLogin(email, password)

    suspend fun registerUser(user: UserModel) = firebaseAuthService.registerUser(user)

    suspend fun logout() = firebaseAuthService.logout()

}