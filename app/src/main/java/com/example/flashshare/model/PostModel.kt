package com.example.flashshare.model

import android.content.ClipDescription
import android.net.Uri
import com.example.flashshare.service.AppConstants
import com.google.firebase.firestore.Exclude
import java.io.Serializable

class PostModel() {
    lateinit var id: String
    lateinit var userId: String
    lateinit var urlPhotoPost: String
    lateinit var description: String
    lateinit var datePublication: String

    @Exclude @get: Exclude
    lateinit var userData: UserModel
    @Exclude @get: Exclude
    var isLiked: Boolean = false
    @Exclude @get: Exclude
    var imageUri: Uri? = null

    constructor(postId: String, uId: String, urlPhoto: String, description: String, date: String):this(){
        this.id = postId
        this.userId = uId
        this.urlPhotoPost = urlPhoto
        this.description = description
        this.datePublication = date
    }

    constructor(map: Map<String, Any>):this(){
        this.id = map[AppConstants.FIRESTORE.ID_KEY].toString()
        this.userId = map[AppConstants.FIRESTORE.USER_ID_KEY].toString()
        this.urlPhotoPost = map[AppConstants.FIRESTORE.URL_PHOTO_POST_KEY] as String
        this.description = map[AppConstants.FIRESTORE.DESCRIPTION_KEY].toString()
        this.datePublication = map[AppConstants.FIRESTORE.DATE_PUBLICATION_KEY] as String

    }

    fun toMap(): Map<String, Any>{
        val map = hashMapOf<String, Any>()
        map[AppConstants.FIRESTORE.ID_KEY] = this.id
        map[AppConstants.FIRESTORE.USER_ID_KEY] = this.userId
        map[AppConstants.FIRESTORE.URL_PHOTO_POST_KEY] = this.urlPhotoPost
        map[AppConstants.FIRESTORE.DESCRIPTION_KEY] = this.description
        map[AppConstants.FIRESTORE.DATE_PUBLICATION_KEY] = this.datePublication
        return map
    }

    fun checkUserDataInitialization(): Boolean{
        return ::userData.isInitialized
    }
}