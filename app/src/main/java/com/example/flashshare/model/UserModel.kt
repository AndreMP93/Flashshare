package com.example.flashshare.model

import com.example.flashshare.service.AppConstants
import com.google.firebase.firestore.Exclude

class UserModel() {
    lateinit var name: String
    lateinit var email: String
    lateinit var id: String
    @Exclude @get: Exclude
    lateinit var password: String
    var bio: String? = null
    var username: String? = null
    var urlPhotoProfile: String? = null


    constructor(name: String, email: String): this(){
        this.name = name
        this.email = email
    }

    constructor(
        userId: String,
        name: String,
        email: String,
        bio: String?,
        username: String?,
        urlPhoto: String?,
    ):this(){
        this.id = userId
        this.name = name
        this.email = email
        this.bio = bio
        this.username = username
        this.urlPhotoProfile = urlPhoto
    }

    fun toMap(): Map<String, Any?>{
        val map = hashMapOf<String, Any?>()
        map[AppConstants.FIRESTORE.ID_KEY] = this.id
        map[AppConstants.FIRESTORE.NAME_KEY] = this.name
        map[AppConstants.FIRESTORE.EMAIL_KEY] = this.email
        map[AppConstants.FIRESTORE.USERNAME_KEY] = this.username ?: ""
        map[AppConstants.FIRESTORE.BIO_KEY] = this.bio ?: ""
        map[AppConstants.FIRESTORE.URL_PHOTO_KEY] = this.urlPhotoProfile ?: ""

        return map
    }
}