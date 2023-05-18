package com.example.flashshare.model

import com.example.flashshare.service.AppConstants

class LikedPostModel() {
    var userId: String = ""

    constructor(idPost: String): this(){
        this.userId = idPost
    }

    constructor(map: Map<String, Any>): this(){
        this.userId = map[AppConstants.FIRESTORE.USER_ID_KEY].toString()
    }

    fun toMap(): Map<String, Any>{
        val map = hashMapOf<String, Any>()
        map[AppConstants.FIRESTORE.USER_ID_KEY] = this.userId
        return map
    }
}