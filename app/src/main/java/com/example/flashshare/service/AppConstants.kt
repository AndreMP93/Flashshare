package com.example.flashshare.service

class AppConstants private constructor(){
    // SharedPreferences
    object SHARED {
        const val PREFERENCES_NAME = "authShared"
        const val USER_ID = "userId"
        const val USER_EMAIL = "userEmail"
    }

    object FIRESTORE {
        const val USER_COLLECTION = "users"
        const val ID_KEY = "id"
        const val NAME_KEY = "name"
        const val EMAIL_KEY = "email"
        const val BIO_KEY = "bio"
        const val USERNAME_KEY = "username"
        const val URL_PHOTO_KEY = "urlPhotoProfile"
    }

    object STORAGE {
        const val IMAGE_PATH = "images"
        const val PROFILE_PATH = "profile"
        const val POSTS_PATH = "posts"
    }
}