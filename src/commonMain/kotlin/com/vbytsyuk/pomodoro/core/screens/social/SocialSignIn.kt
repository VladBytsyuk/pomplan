package com.vbytsyuk.pomodoro.core.screens.social

import com.vbytsyuk.pomodoro.core.utils.Result


interface SocialSignIn<D, E> {
    suspend fun signIn(): Result<D, E>
}
