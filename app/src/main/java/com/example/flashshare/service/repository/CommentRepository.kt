package com.example.flashshare.service.repository

import com.example.flashshare.model.CommentModel
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CommentRepository {

    private val db = Firebase.firestore
    private fun getRefFireStore(postId: String): CollectionReference {
        return db.collection(AppConstants.FIRESTORE.POSTS_COLLECTION)
            .document(postId)
            .collection(AppConstants.FIRESTORE.COMMENT_COLLECTION)
    }

    suspend fun addComment(postId: String, comment: CommentModel):ResultModel<Unit>{
        return suspendCoroutine { continuation ->
            try {
                val documentRef = getRefFireStore(postId)
                comment.id = documentRef.document().id
                documentRef.document(comment.id)
                    .set(comment.toMap())
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

    suspend fun updateComment(postId: String, commentId: String, comment: CommentModel):ResultModel<Unit>{
        return suspendCoroutine { continuation ->
            try {
                getRefFireStore(postId)
                    .document(commentId)
                    .update(comment.toMap())
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

    suspend fun getComments(postId: String, currentUserId: String): ResultModel<List<CommentModel>>{
        return suspendCoroutine { continuation ->
            try {
                getRefFireStore(postId)
                    .orderBy(AppConstants.FIRESTORE.DATE_COMMENT_KEY, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener {
                        val listComments = mutableListOf<CommentModel>()
                        for(doc in it.documents){
                            val comment = CommentModel(doc.data as Map<String, Any>)
                            if(comment.userId == currentUserId){
                                comment.isMyComment = true
                                listComments.add(0, comment)
                            }
                            else{
                                listComments.add(comment)
                            }

                        }
                        continuation.resume(ResultModel.Success(listComments))
                    }
            }catch (e: Exception){
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    suspend fun removeComment(postId: String, commentId: String): ResultModel<Unit>{
        return suspendCoroutine { continuation ->
            try {
                getRefFireStore(postId)
                    .document(commentId)
                    .delete()
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
}