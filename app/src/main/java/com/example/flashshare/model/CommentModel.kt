package com.example.flashshare.model

import com.example.flashshare.service.AppConstants

class CommentModel() {
    var id = ""
    var description: String = ""
    var userId: String = ""
    var userName: String = ""
    var urlUserPhoto: String = ""
    var date: String = ""

    constructor(
        id: String,
        description: String,
        userId: String,
        userName: String,
        urlUserPhoto: String,
        date: String
    ) : this() {
        this.id = id
        this.description = description
        this.userId = userId
        this.userName = userName
        this.urlUserPhoto = urlUserPhoto
        this.date = date
    }

    constructor(map: Map<String, Any>): this(){
        this.id = map[AppConstants.FIRESTORE.ID_KEY].toString()
        this.description = map[AppConstants.FIRESTORE.DESCRIPTION_KEY].toString()
        this.userId = map[AppConstants.FIRESTORE.USER_ID_KEY].toString()
        this.userName = map[AppConstants.FIRESTORE.USERNAME_KEY].toString()
        this.urlUserPhoto = map[AppConstants.FIRESTORE.URL_PHOTO_KEY].toString()
        this.date = map[AppConstants.FIRESTORE.DATE_COMMENT_KEY].toString()
    }

    fun toMap(): Map<String, Any>{
        val map = hashMapOf<String, Any>()
        map[AppConstants.FIRESTORE.ID_KEY] = this.id
        map[AppConstants.FIRESTORE.DESCRIPTION_KEY] = this.description
        map[AppConstants.FIRESTORE.USER_ID_KEY] = this.userId
        map[AppConstants.FIRESTORE.USERNAME_KEY] = this.userName
        map[AppConstants.FIRESTORE.URL_PHOTO_KEY] = this.urlUserPhoto
        map[AppConstants.FIRESTORE.DATE_COMMENT_KEY] = this.date
        return map
    }
}