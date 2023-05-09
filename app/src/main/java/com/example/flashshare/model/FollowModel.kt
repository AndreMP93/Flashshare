package com.example.flashshare.model

import com.example.flashshare.service.AppConstants

class FollowModel() {
    lateinit var userId: String

    constructor(userId: String): this(){
        this.userId = userId
    }

    constructor(map: Map<String, Any>): this(){
        this.userId = map[AppConstants.FIRESTORE.ID_KEY].toString()
    }

    fun toMap(): Map<String, Any>{
        val map = hashMapOf<String, Any>()
        map[AppConstants.FIRESTORE.ID_KEY] = this.userId
        return map
    }
}