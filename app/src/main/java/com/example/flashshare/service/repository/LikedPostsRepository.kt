package com.example.flashshare.service.repository

import com.example.flashshare.model.LikedPostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LikedPostsRepository {

    private val db = Firebase.firestore

    suspend fun addLike(postId: String, likedPost: LikedPostModel): ResultModel<Unit>{
        return suspendCoroutine { continuation ->
            try {
                getFireStoreRef(postId)
                    .document(likedPost.userId)
                    .set(likedPost.toMap())
                    .addOnSuccessListener {
                        continuation.resume(ResultModel.Success(Unit))
                    }
                    .addOnFailureListener {
                        continuation.resume(ResultModel.Error(it.message))
                    }
            }catch(e: Exception){
                continuation.resume(ResultModel.Error(e.message))
            }

        }
    }

    suspend fun removeLike(postId: String, likedPostModel: LikedPostModel): ResultModel<Unit>{
        return suspendCoroutine { continuation ->
            try {
                getFireStoreRef(postId)
                    .document(likedPostModel.userId)
                    .delete()
                    .addOnSuccessListener {
                        continuation.resume(ResultModel.Success(Unit))
                    }
                    .addOnFailureListener {
                        continuation.resume(ResultModel.Error(it.message))
                    }
            }catch(e: Exception){
                continuation.resume(ResultModel.Error(e.message))
            }

        }
    }

    suspend fun checkLikedPost(postId: String, likedPostModel: LikedPostModel): ResultModel<Boolean>{
        return suspendCoroutine { continuation ->
            try {
                getFireStoreRef(postId)
                    .whereEqualTo(AppConstants.FIRESTORE.USER_ID_KEY, likedPostModel.userId)
                    .get()
                    .addOnSuccessListener {
                        if(it.isEmpty){
                            continuation.resume(ResultModel.Success(false))
                        }else{
                            continuation.resume(ResultModel.Success(true))
                        }
                    }
                    .addOnFailureListener {
                        continuation.resume(ResultModel.Error(it.message))
                    }
            }catch (e: Exception){
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    private fun getFireStoreRef(postId: String): CollectionReference {
        return db.collection(AppConstants.FIRESTORE.POSTS_COLLECTION)
            .document(postId)
            .collection(AppConstants.FIRESTORE.LIKED_POSTS_COLLECTION)
    }
}