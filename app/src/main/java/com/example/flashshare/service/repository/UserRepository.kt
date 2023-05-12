package com.example.flashshare.service.repository

import android.net.Uri
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.AppConstants
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.coroutines.suspendCoroutine

class UserRepository{

    private val db = Firebase.firestore
    private val storage = Firebase.storage

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
                    .update(user.toMap())
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

    suspend fun savePhotoProfile(user: UserModel, image: Uri): ResultModel<UserModel>{
        return suspendCoroutine { continuation ->
            try {
                val storageRef = storage.reference
                    .child(AppConstants.STORAGE.IMAGE_PATH)
                    .child(AppConstants.STORAGE.PROFILE_PATH)
                    .child("${user.id}.jpeg")

                storageRef.putFile(image)
                    .addOnSuccessListener {
                        it.storage.downloadUrl.addOnCompleteListener {uri ->
                            user.urlPhotoProfile = uri.result.toString()
                            continuation.resumeWith(Result.success(ResultModel.Success(user)))
                        }

                    }
                    .addOnFailureListener {
                        continuation.resumeWith(Result.success(ResultModel.Error(it.message)))
                    }
            }catch (e: Exception){
                continuation.resumeWith(Result.success(ResultModel.Error(e.message)))
            }
        }
    }

    suspend fun getUsers(text: String): ResultModel<List<UserModel>>{
        return suspendCoroutine {continuation ->
            val query = db.collection(AppConstants.FIRESTORE.USER_COLLECTION)
                .orderBy(AppConstants.FIRESTORE.NAME_UPPERCASE)
                .startAt(text)
                .endAt(text + "\uf8ff")

            query.get()
                .addOnSuccessListener {
                    val listUsers = mutableListOf<UserModel>()
                    for (doc in it.documents){
                        listUsers.add(UserModel(doc.data as Map<String, Any?>))
                    }
                    continuation.resumeWith(Result.success(ResultModel.Success(listUsers)))
                }
                .addOnFailureListener {
                    continuation.resumeWith(Result.success(ResultModel.Error(it.message)))
                }
        }
    }
}