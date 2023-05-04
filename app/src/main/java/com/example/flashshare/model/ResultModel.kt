package com.example.flashshare.model

sealed class ResultModel<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultModel<T>()
    data class Error(val message: String?, val cause: Throwable? = null) : ResultModel<Nothing>()
    object Loading : ResultModel<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[message=$message, cause=$cause]"
            Loading -> "Loading"
        }
    }
}
