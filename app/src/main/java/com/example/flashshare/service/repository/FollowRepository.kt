package com.example.flashshare.service.repository

import com.example.flashshare.model.FollowModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FollowRepository {

    private val db = Firebase.firestore

    suspend fun getQuantityFollowers(userId: String): ResultModel<Int>{
        return suspendCoroutine {continuation ->
            try{
                getFollowerRef(userId)
                    .get()
                    .addOnSuccessListener {
                        continuation.resume(ResultModel.Success(it.documents.size))
                    }
                    .addOnFailureListener {
                        continuation.resume(ResultModel.Error(it.message))
                    }

            }catch(e: Exception){
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    suspend fun getQuantityFollowing(userId: String): ResultModel<Int>{
        return suspendCoroutine {continuation ->
            try{
                getFollowingRef(userId)
                    .get()
                    .addOnSuccessListener {
                        continuation.resume(ResultModel.Success(it.documents.size))
                    }
                    .addOnFailureListener {
                        continuation.resume(ResultModel.Error(it.message))
                    }

            }catch(e: Exception){
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    suspend fun checkFollowUser(userId: String, friendId: String): ResultModel<Boolean>{
        return suspendCoroutine { continuation ->
            try {
                getFollowingRef(userId)
                    .document(friendId)
                    .get()
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            continuation.resume(ResultModel.Success(it.result.exists()))
                        }else{
                            continuation.resume(ResultModel.Error("Falha Inesperada!!"))
                        }
                    }
            }catch (e: Exception){
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    suspend fun followUser(userId: String, friendId: String): ResultModel<Unit>{
        return suspendCoroutine { continuation ->
            try {
                val refUser = getFollowingRef(userId)
                    .document(friendId)
                    .set(FollowModel(friendId))

                val refFriend = getFollowerRef(friendId)
                    .document(userId)
                    .set(FollowModel(userId))

                refUser.addOnCompleteListener {
                    refFriend.addOnCompleteListener {
                        continuation.resume(ResultModel.Success(Unit))
                    }
                }
                refUser.addOnFailureListener {
                    continuation.resume(ResultModel.Error(it.message))
                }

                refFriend.addOnFailureListener {
                    continuation.resume(ResultModel.Error(it.message))
                }


            }catch (e: Exception){
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    suspend fun unfollowUser(userId: String, friendId: String): ResultModel<Unit>{
        return suspendCoroutine { continuation ->
            try {
                val refUser = getFollowingRef(userId)
                    .document(friendId)
                    .delete()


                val refFriend = getFollowerRef(friendId)
                    .document(userId)
                    .delete()

                refUser.addOnCompleteListener {
                    refFriend.addOnCompleteListener {
                        continuation.resume(ResultModel.Success(Unit))
                    }
                }
                refUser.addOnFailureListener {
                    continuation.resume(ResultModel.Error(it.message))
                }

                refFriend.addOnFailureListener {
                    continuation.resume(ResultModel.Error(it.message))
                }


            }catch (e: Exception){
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    private fun getFollowerRef(userId: String): CollectionReference {
        return db.collection(AppConstants.FIRESTORE.USER_COLLECTION)
            .document(userId)
            .collection(AppConstants.FIRESTORE.FOLLOWERS_COLLECTION)
    }

    private fun getFollowingRef(userId: String): CollectionReference {
        return db.collection(AppConstants.FIRESTORE.USER_COLLECTION)
            .document(userId)
            .collection(AppConstants.FIRESTORE.FOLLOWING_COLLECTION)
    }
}