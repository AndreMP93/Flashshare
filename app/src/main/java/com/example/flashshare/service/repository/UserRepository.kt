package com.example.flashshare.service.repository

import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.AppConstants
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.suspendCoroutine

class UserRepository{

    private val db = Firebase.firestore

    suspend fun addUser(user: UserModel): ResultModel<Unit> {
        return suspendCoroutine {continuation ->
            try {
                db.collection(AppConstants.FIRESTORE.USER_COLLECTION)
                    .document(user.id)
                    .set(user)
                    .addOnSuccessListener {
                        continuation.resumeWith(Result.success(ResultModel.Success(Unit)))
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWith(Result.success(ResultModel.Error(e.message)))
                    }
            }catch (e: Exception){
                continuation.resumeWith(Result.success(ResultModel.Error(e.message)))
            }
        }

    }

    suspend fun getUser(userId: String): ResultModel<UserModel> {
        return suspendCoroutine { continuation ->
            try {
                db.collection(AppConstants.FIRESTORE.USER_COLLECTION)
                    .document(userId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val user = document.toObject(UserModel::class.java)
                            if(user!=null){

                                continuation.resumeWith(Result.success(ResultModel.Success(user)))

                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWith(Result.success(ResultModel.Error(e.message)))
                    }
            }catch (e: Exception){
                continuation.resumeWith(Result.success(ResultModel.Error(e.message)))
            }
        }
    }

    suspend fun updateUser(user: UserModel): ResultModel<Unit>{
        return suspendCoroutine {continuation ->
            try {
                db.collection(AppConstants.FIRESTORE.USER_COLLECTION)
                    .document(user.id)
                    .set(user)
                    .addOnSuccessListener {
                        continuation.resumeWith(Result.success(ResultModel.Success(Unit)))
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWith(Result.success(ResultModel.Error(e.message)))
                    }
            }catch (e: Exception){
                continuation.resumeWith(Result.success(ResultModel.Error(e.message)))
            }
        }
    }
}