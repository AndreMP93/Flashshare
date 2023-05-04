package com.example.flashshare.model

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
        this.username = urlPhoto
    }
}