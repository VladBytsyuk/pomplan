package com.vbytsyuk.pomodoro.core.api


interface AuthApi {
    data class UserData(val name: String)

    suspend fun signIn(login: String, password: String): ApiResult<UserData>
}
