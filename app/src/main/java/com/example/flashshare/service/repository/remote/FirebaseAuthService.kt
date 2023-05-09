package com.example.flashshare.service.repository.remote

import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthService {
    private val authInstance = Firebase.auth

    suspend fun doLogin(email: String, password: String): ResultModel<UserModel> {
        return suspendCoroutine { continuation ->
            try{
                authInstance.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val user = UserModel("", "")
                        user.email = it.user?.email!!
                        user.id = it.user?.uid!!
                        continuation.resumeWith(Result.success(ResultModel.Success(user)))
                    }.addOnFailureListener {
                        continuation.resumeWith(Result.success(ResultModel.Error(it.message)))
                    }
            }catch (e: Exception){
                continuation.resumeWith(Result.success(ResultModel.Error(e.toString())))
            }
        }
    }

    suspend fun registerUser(user: UserModel): ResultModel<UserModel>{
        return suspendCoroutine { continuation ->
            try {
                authInstance.createUserWithEmailAndPassword(user.email, user.password)
                    .addOnSuccessListener {
                        user.email = it.user?.email!!
                        user.id = it.user?.uid!!
                        continuation.resumeWith(Result.success(ResultModel.Success(user)))
                    }.addOnFailureListener{
                        continuation.resumeWith(Result.success(ResultModel.Error(it.toString())))
                    }
            }catch (e: Exception){
                continuation.resumeWith(Result.success(ResultModel.Error(e.toString())))
            }
        }
    }

    suspend fun logout():ResultModel<Unit>{
        return suspendCoroutine { continuation ->
            try {
                authInstance.signOut()
                continuation.resumeWith(Result.success(ResultModel.Success(Unit)))
            }catch (e: Exception){
                continuation.resumeWith(Result.success(ResultModel.Error(e.toString())))
            }
        }
    }
}