package com.vbytsyuk.pomodoro.core.api


sealed class ApiResult<out T : Any> {
    sealed class Success<out T : Any> : ApiResult<T>() {
        data class Content<out T : Any>(val data: T) : Success<T>()
        data class Error(val error: String) : Success<Nothing>()
    }
    data class Failure(val exception: Throwable) : ApiResult<Nothing>()
}
