package com.example.flashshare.service.repository

import android.net.Uri
import com.example.flashshare.model.FollowModel
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PostRepository {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    private fun getPostReference(): CollectionReference {
        return db.collection(AppConstants.FIRESTORE.POSTS_COLLECTION)
    }

    suspend fun getPosts(userId: String): ResultModel<List<PostModel>> {
        return suspendCoroutine { continuation ->
            try {
                getPostReference()
                    .whereEqualTo(AppConstants.FIRESTORE.USER_ID_KEY, userId)
                    .orderBy(AppConstants.FIRESTORE.DATE_PUBLICATION_KEY, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener {
                        val listPost = mutableListOf<PostModel>()
                        for (doc in it.documents) {
                            listPost.add(PostModel(doc.data as Map<String, Any>))
                        }
                        continuation.resume(ResultModel.Success(listPost))
                    }
            } catch (e: Exception) {
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    suspend fun getPost(postId: String): ResultModel<PostModel> {
        return suspendCoroutine { continuation ->
            try {
                getPostReference()
                    .document(postId)
                    .get()
                    .addOnSuccessListener {
                        val post = PostModel(it.data as Map<String, Any>)
                        continuation.resume(ResultModel.Success(post))
                    }
                    .addOnFailureListener {
                        continuation.resume(ResultModel.Error(it.message))
                    }
            } catch (e: Exception) {
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    suspend fun deletePost(postId: String): ResultModel<Unit> {
        return suspendCoroutine { continuation ->
            try {
                getPostReference()
                    .document(postId)
                    .delete()
                    .addOnSuccessListener {
                        continuation.resume(ResultModel.Success(Unit))
                    }
                    .addOnFailureListener {
                        continuation.resume(ResultModel.Error(it.message))
                    }
            } catch (e: Exception) {
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    suspend fun updatePost(postId: String, post: PostModel): ResultModel<Unit> {
        return suspendCoroutine { continuation ->
            try {
                getPostReference()
                    .document(postId)
                    .update(post.toMap())
                    .addOnSuccessListener {
                        continuation.resume(ResultModel.Success(Unit))
                    }
                    .addOnFailureListener {
                        continuation.resume(ResultModel.Error(it.message))
                    }
            } catch (e: Exception) {
                continuation.resume(ResultModel.Error(e.message))
            }

        }
    }

    suspend fun getPostsQuantity(userId: String): ResultModel<Int> {
        return suspendCoroutine { continuation ->
            try {
                getPostReference()
                    .whereEqualTo(AppConstants.FIRESTORE.USER_ID_KEY, userId)
                    .get()
                    .addOnSuccessListener {
                        continuation.resume(ResultModel.Success(it.documents.size))
                    }
            } catch (e: Exception) {
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    suspend fun publicationPost(userId: String, post: PostModel, image: Uri): ResultModel<Unit> {
        return suspendCoroutine { continuation ->
            try {
                getPostReference()
                    .add(post.toMap())
                    .addOnSuccessListener {
                        post.id = it.id

                        savePhotoPost(image, userId, post) { url ->
                            if (url != null) {
                                post.urlPhotoPost = url
                                it.update(post.toMap())
                                    .addOnSuccessListener {
                                        continuation.resume(ResultModel.Success(Unit))
                                    }
                            } else {
                                continuation.resume(ResultModel.Error("Falha ao fazer a postagem"))
                            }
                        }

                    }
            } catch (e: Exception) {
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    private fun savePhotoPost(
        image: Uri,
        userId: String,
        post: PostModel,
        updateFireStore: (url: String?) -> Unit
    ) {
        try {
            storage.reference
                .child(AppConstants.STORAGE.POSTS_PATH)
                .child(userId)
                .child("${post.id}.jpg")
                .putFile(image)
                .addOnSuccessListener {
                    it.storage.downloadUrl.addOnCompleteListener { uri ->
                        updateFireStore(uri.result.toString())
                    }
                }
        } catch (e: Exception) {
            updateFireStore(null)
        }
    }

    suspend fun getFeed(userId: String): ResultModel<List<PostModel>>{
        return suspendCoroutine { continuation ->
            db.collection(AppConstants.FIRESTORE.USER_COLLECTION)
                .document(userId)
                .collection(AppConstants.FIRESTORE.FOLLOWING_COLLECTION)
                .get()
                .addOnSuccessListener { followResult ->
                    val followingIDs = followResult.documents.map {
                        it.id
                    }
                    if(followingIDs.isNotEmpty()){
                        db.collection(AppConstants.FIRESTORE.POSTS_COLLECTION)
                            .whereIn(AppConstants.FIRESTORE.USER_ID_KEY, followingIDs)
                            .get()
                            .addOnSuccessListener {feedResult ->
                                val posts = mutableListOf<PostModel>()
                                for(doc in feedResult.documents){
                                    val post = PostModel(doc.data as Map<String, Any>)
                                    posts.add(post)
                                }
                                continuation.resume(ResultModel.Success(posts))
                            }
                            .addOnFailureListener {
                                continuation.resume(ResultModel.Error(it.message))
                            }
                    }else{
                        continuation.resume(ResultModel.Success(mutableListOf()))
                    }
                }
                .addOnFailureListener {
                    continuation.resume(ResultModel.Error(it.message))
                }

        }
    }
}