package com.example.flashshare.service

interface FirebaseListener<T> {
    fun onSuccess(result: T)
}