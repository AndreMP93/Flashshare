package com.example.flashshare.service.repository

import android.net.Uri
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PostRepository {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    suspend fun getPosts(userId: String): ResultModel<List<PostModel>>{
        return suspendCoroutine {continuation ->
            try {
                db.collection(AppConstants.FIRESTORE.USER_COLLECTION)
                    .document(userId)
                    .collection(AppConstants.FIRESTORE.POSTS_COLLECTION)
                    .get()
                    .addOnSuccessListener {
                        val listPost = mutableListOf<PostModel>()
                        for(doc in it.documents){
                            listPost.add(PostModel(doc.data as Map<String, Any>))
                        }
                    }
            }catch (e: Exception){
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    suspend fun publicationPost(userId: String, post: PostModel, image: Uri): ResultModel<Unit>{
        return suspendCoroutine { continuation ->
            try {
                val ref = db.collection(AppConstants.FIRESTORE.USER_COLLECTION)
                    .document(userId)
                    .collection(AppConstants.FIRESTORE.POSTS_COLLECTION)
                    .add(post.toMap())
                    .addOnSuccessListener {
                        post.id = it.id

                        savePhotoPost(image, userId, post){ url->
                            if(url!=null){
                                post.urlPhotoPost = url
                                it.update(post.toMap())
                                    .addOnSuccessListener {
                                        continuation.resume(ResultModel.Success(Unit))
                                    }
                            }else{
                                continuation.resume(ResultModel.Error("Falha ao fazer a postagem"))
                            }
                        }

                    }
            }catch (e: Exception){
                continuation.resume(ResultModel.Error(e.message))
            }
        }
    }

    private fun savePhotoPost(image: Uri, userId: String, post: PostModel, updateFireStore: (url: String?)-> Unit){
        try {
            storage.reference
                .child(AppConstants.STORAGE.POSTS_PATH)
                .child(userId)
                .child("${post.id}.jpg")
                .putFile(image)
                .addOnSuccessListener {
                    it.storage.downloadUrl.addOnCompleteListener {uri ->
                        updateFireStore(uri.result.toString())
                    }
                }
        }catch (e: Exception){
            updateFireStore(null)
        }
    }
}