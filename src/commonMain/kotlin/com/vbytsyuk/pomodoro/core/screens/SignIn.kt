package com.vbytsyuk.pomodoro.core.screens

import com.vbytsyuk.pomodoro.core.api.ApiResult
import com.vbytsyuk.pomodoro.core.api.AuthApi
import com.vbytsyuk.pomodoro.core.screens.SignIn.State.SubState.*
import com.vbytsyuk.pomodoro.core.screens.social.SocialSignIn
import com.vbytsyuk.pomodoro.core.utils.Result
import com.vbytsyuk.pomodoro.elm.Elm
import kotlinx.coroutines.delay


class SignIn(
    authApi: AuthApi,
    googleSignIn: SocialSignIn<AuthApi.UserData, String>,
    appleSignIn: SocialSignIn<AuthApi.UserData, String>,
    twitterSignIn: SocialSignIn<AuthApi.UserData, String>
) : App.Screen<SignIn.State, SignIn.Action, SignIn.Effect> {

    override val controller: Elm.Controller<State, Action, Effect> = Elm.ControllerImpl(
        initialState = State(),
        initialAction = Action.Initialize,
        effectHandler = EffectHandler(authApi, googleSignIn, appleSignIn, twitterSignIn),
        reducer = Reducer()
    )

    data class State(
        val login: String = "",
        val password: String = "",
        val subState: SubState = Input
    ) : Elm.State {
        enum class SubState { Input, Loading, Error, Success }
    }

    sealed class Action : Elm.Action {
        object Initialize : Action()
        sealed class Changed : Action() {
            data class Login(val newLogin: String) : Changed()
            data class Password(val newPassword: String) : Changed()
        }
        sealed class Clicked : Action() {
            object SignIn : Clicked()
            data class Social(val social: SocialNetwork) : Clicked()
            object Register : Clicked()
        }
        sealed class Result : Action() {
            object WrongData : Result()
            data class SignedIn(val userData: AuthApi.UserData) : Result()
        }
    }

    enum class SocialNetwork(val effect: Effect) {
        GOOGLE(Effect.GoogleSignIn),
        APPLE(Effect.AppleSignIn),
        TWITTER(Effect.TwitterSignIn)
    }

    sealed class Effect : Elm.Effect {
        data class ServerSignIn(val login: String, val password: String) : Effect()
        object GoogleSignIn : Effect()
        object AppleSignIn : Effect()
        object TwitterSignIn : Effect()
        object Register : Effect()
    }

    class EffectHandler(
        private val authApi: AuthApi,
        private val googleSignIn: SocialSignIn<AuthApi.UserData, String>,
        private val appleSignIn: SocialSignIn<AuthApi.UserData, String>,
        private val twitterSignIn: SocialSignIn<AuthApi.UserData, String>
    ) : Elm.EffectHandler<Effect, Action> {
        override suspend fun handle(effect: Effect): Action = when (effect) {
            is Effect.ServerSignIn -> handleServerSignIn(effect.login, effect.password)
            Effect.GoogleSignIn -> handleGoogleSignIn()
            Effect.AppleSignIn -> handleAppleSignIn()
            Effect.TwitterSignIn -> handleTwitterSignIn()
            Effect.Register -> handleRegister()
        }

        private suspend fun handleServerSignIn(login: String, password: String) =
            when (val result = authApi.signIn(login, password)) {
                is ApiResult.Success.Content -> Action.Result.SignedIn(result.data)
                else -> Action.Result.WrongData
            }

        private suspend fun handleGoogleSignIn(): Action = when (val result = googleSignIn.signIn()) {
            is Result.Success -> Action.Result.SignedIn(result.data)
            is Result.Failure -> Action.Result.WrongData
        }

        private suspend fun handleAppleSignIn(): Action = when (val result = appleSignIn.signIn()) {
            is Result.Success -> Action.Result.SignedIn(result.data)
            is Result.Failure -> Action.Result.WrongData
        }

        private suspend fun handleTwitterSignIn(): Action = when (val result = twitterSignIn.signIn()) {
            is Result.Success -> Action.Result.SignedIn(result.data)
            is Result.Failure -> Action.Result.WrongData
        }

        private suspend fun handleRegister(): Action {
            delay(300)
            return Action.Result.SignedIn(AuthApi.UserData("New user"))
        }
    }

    class Reducer : Elm.Reducer<State, Action, Effect> {
        override fun reduce(oldState: State, action: Action): Pair<State, Effect?> = when (action) {
            Action.Initialize -> State() to null
            is Action.Changed -> reduceChanged(oldState, action)
            is Action.Clicked -> reduceClicked(oldState, action)
            is Action.Result -> reduceResult(oldState, action)
        }

        private fun reduceChanged(oldState: State, action: Action.Changed): Pair<State, Effect?> = when (action) {
            is Action.Changed.Login -> oldState.copy(login = action.newLogin, subState = Input) to null
            is Action.Changed.Password -> oldState.copy(password = action.newPassword, subState = Input) to null
        }

        private fun reduceClicked(oldState: State, action: Action.Clicked): Pair<State, Effect?> = when (action) {
            Action.Clicked.SignIn ->
                oldState.copy(subState = Loading) to Effect.ServerSignIn(oldState.login, oldState.password)

            is Action.Clicked.Social -> oldState.copy(subState = Loading) to action.social.effect
            Action.Clicked.Register -> oldState.copy(subState = Success) to Effect.Register
        }

        private fun reduceResult(oldState: State, action: Action.Result): Pair<State, Effect?> = when (action) {
            Action.Result.WrongData -> State(subState = Error) to null
            is Action.Result.SignedIn -> oldState.copy(subState = Success) to null
        }
    }
}
