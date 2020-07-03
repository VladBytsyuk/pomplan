package com.vbytsyuk.pomodoro.mock

import com.vbytsyuk.pomodoro.core.api.AuthApi
import com.vbytsyuk.pomodoro.core.screens.social.SocialSignIn
import com.vbytsyuk.pomodoro.core.utils.Result


class SocialSignInMock(private val socialName: String) : SocialSignIn<AuthApi.UserData, String> {
    override suspend fun signIn(): Result<AuthApi.UserData, String> =
        Result.Success(AuthApi.UserData(socialName))
}
