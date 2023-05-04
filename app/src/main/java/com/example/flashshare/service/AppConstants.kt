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
    }
}