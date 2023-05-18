package com.example.flashshare.model

import com.example.flashshare.service.AppConstants
import com.google.firebase.firestore.Exclude

class UserModel() {
    lateinit var name: String
    lateinit var email: String
    lateinit var id: String
    @Exclude @get: Exclude
    lateinit var password: String
    var nameUppercase: String = ""
    var bio: String? = null
    var urlPhotoProfile: String? = null
    var posts: Int = 0
    var followers: Int = 0
    var following: Int = 0


    constructor(name: String, email: String): this(){
        this.name = name
        this.email = email
        this.nameUppercase = this.name.uppercase()
    }

    constructor(
        userId: String,
        name: String,
        email: String,
        bio: String?,
        urlPhoto: String?
    ):this(){
        this.id = userId
        this.name = name
        this.nameUppercase = this.name.uppercase()
        this.email = email
        this.bio = bio ?: ""
        this.urlPhotoProfile = urlPhoto ?: ""

    }

    constructor(map: Map<String, Any?>): this(){
        this.id = map[AppConstants.FIRESTORE.ID_KEY] as String
        this.name = map[AppConstants.FIRESTORE.NAME_KEY] as String
        this.nameUppercase = this.name.uppercase()
        this.email = map[AppConstants.FIRESTORE.EMAIL_KEY] as String
        this.bio = map[AppConstants.FIRESTORE.BIO_KEY].toString()
        this.urlPhotoProfile = map[AppConstants.FIRESTORE.URL_PHOTO_KEY] as String?
    }

    fun toMap(): Map<String, Any?>{
        val map = hashMapOf<String, Any?>()
        map[AppConstants.FIRESTORE.ID_KEY] = this.id
        map[AppConstants.FIRESTORE.NAME_KEY] = this.name
        map[AppConstants.FIRESTORE.NAME_UPPERCASE] = this.nameUppercase
        map[AppConstants.FIRESTORE.EMAIL_KEY] = this.email
        map[AppConstants.FIRESTORE.BIO_KEY] = this.bio ?: ""
        map[AppConstants.FIRESTORE.URL_PHOTO_KEY] = this.urlPhotoProfile ?: ""

        return map
    }
}