package com.vbytsyuk.pomodoro.mock

import com.vbytsyuk.pomodoro.core.api.ApiResult
import com.vbytsyuk.pomodoro.core.api.AuthApi


class AuthApiMock(
    private val correctLogin: String,
    private val correctPassword: String,
    private val errorMessage: String
) : AuthApi {
    override suspend fun signIn(login: String, password: String): ApiResult<AuthApi.UserData> =
        when {
            login == correctLogin && password == correctPassword ->
                ApiResult.Success.Content(AuthApi.UserData("Test user by password"))

            else ->
                ApiResult.Success.Error(errorMessage)
        }
}
