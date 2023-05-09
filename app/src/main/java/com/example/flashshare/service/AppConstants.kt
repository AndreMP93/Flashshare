package com.example.flashshare.service

class AppConstants private constructor(){
    // SharedPreferences
    object SHARED {
        const val PREFERENCES_NAME = "authShared"
        const val USER_ID = "userId"
        const val USER_EMAIL = "userEmail"
    }

    object BUNDLE {
        const val USER_ID = "userId"
    }

    object FIRESTORE {
        const val USER_COLLECTION = "users"
        const val FOLLOWERS_COLLECTION = "followers"
        const val FOLLOWING_COLLECTION = "following"
        const val POSTS_COLLECTION = "posts"
        const val ID_KEY = "id"
        const val NAME_KEY = "name"
        const val NAME_UPPERCASE = "nameUppercase"
        const val EMAIL_KEY = "email"
        const val BIO_KEY = "bio"
        const val USERNAME_KEY = "username"
        const val URL_PHOTO_KEY = "urlPhotoProfile"
        const val POSTS_KEY = "posts"
        const val FOLLOWERS_KEY = "followers"
        const val FOLLOWING_KEY ="following"
    }

    object STORAGE {
        const val IMAGE_PATH = "images"
        const val PROFILE_PATH = "profile"
        const val POSTS_PATH = "posts"
    }
}