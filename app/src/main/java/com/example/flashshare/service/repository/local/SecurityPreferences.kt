package com.example.flashshare.service.repository.local

import android.content.Context
import android.content.SharedPreferences
import com.example.flashshare.service.AppConstants

class SecurityPreferences(context: Context){
    private val preferences: SharedPreferences =
        context.getSharedPreferences(AppConstants.SHARED.PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun save(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }

    fun get(key: String): String {
        return preferences.getString(key, "") ?: ""
    }
}