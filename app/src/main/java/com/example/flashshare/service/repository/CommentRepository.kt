package com.example.flashshare.service.repository

import com.example.flashshare.model.CommentModel
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CommentRepository {

    private val db = Firebase.firestore

    suspend fun addComment(userId: String, postId: String, comment: CommentModel):ResultModel<Unit>{
        return suspendCoroutine { continuation ->
            try {
                db.collection(AppConstants.FIRESTORE.USER_COLLECTION)
                    .document(userId)
                    .collection(AppConstants.FIRESTORE.POSTS_COLLECTION)
                    .document(postId)
                    .collection(AppConstants.FIRESTORE.COMMENT_COLLECTION)
                    .add(comment.toMap())
                    .addOnSuccessListener {
                        continuation.resume(ResultModel.Success(Unit))
                    }
                    .addOnFailureListener {
                        continuation.resume(ResultModel.Error(it.message))
                    }
            }catch (e: Exception){
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    suspend fun getComments(userId: String, postId: String): ResultModel<List<CommentModel>>{
        return suspendCoroutine { continuation ->
            try {

            }catch (e: Exception){
                continuation.resume(ResultModel.Error(e.message))
                db.collection(AppConstants.FIRESTORE.USER_COLLECTION)
                    .document(userId)
                    .collection(AppConstants.FIRESTORE.POSTS_COLLECTION)
                    .document(postId)
                    .collection(AppConstants.FIRESTORE.COMMENT_COLLECTION)
                    .get()
                    .addOnSuccessListener {
                        val listComments = mutableListOf<CommentModel>()
                        for(doc in it.documents){
                            val comment = CommentModel(doc.data as Map<String, Any>)
                            if(comment.userId == userId){
                                listComments.add(0, comment)
                            }
                            else{
                                listComments.add(comment)
                            }

                        }
                        continuation.resume(ResultModel.Success(listComments))
                    }
            }
        }
    }
}