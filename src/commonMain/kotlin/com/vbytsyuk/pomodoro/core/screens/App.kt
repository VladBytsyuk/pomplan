package com.vbytsyuk.pomodoro.core.screens

import com.vbytsyuk.pomodoro.core.api.AuthApi
import com.vbytsyuk.pomodoro.core.api.SettingsRepository
import com.vbytsyuk.pomodoro.core.screens.social.SocialSignIn
import com.vbytsyuk.pomodoro.elm.Elm


class App(
    authApi: AuthApi,
    googleSignIn: SocialSignIn<AuthApi.UserData, String>,
    appleSignIn: SocialSignIn<AuthApi.UserData, String>,
    twitterSignIn: SocialSignIn<AuthApi.UserData, String>,
    settingsRepository: SettingsRepository
) {
    interface Screen<S : Elm.State, A : Elm.Action, E : Elm.Effect> {
        val controller: Elm.Controller<S, A, E>
    }

    val signIn: Screen<SignIn.State, SignIn.Action, SignIn.Effect> =
        SignIn(authApi, googleSignIn, appleSignIn, twitterSignIn)
            .apply { controller.observeState { this@App.controller.setAction(Action.ChangedSignInState(it)) } }
    val pomodoro: Screen<Pomodoro.State, Pomodoro.Action, Pomodoro.Effect> =
        Pomodoro(settingsRepository)
            .apply { controller.observeState { this@App.controller.setAction(Action.ChangedPomodoroState(it)) } }
    val statistics: Screen<Statistics.State, Statistics.Action, Statistics.Effect> =
        Statistics()
            .apply { controller.observeState { this@App.controller.setAction(Action.ChangedStatisticsState(it)) } }
    val settings: Screen<Settings.State, Settings.Action, Settings.Effect> =
        Settings()
            .apply { controller.observeState { this@App.controller.setAction(Action.ChangedSettingsState(it)) } }

    val controller: Elm.Controller<State, Action, Effect> = Elm.ControllerImpl(
        initialState = State(),
        initialAction = Action.Initialize,
        effectHandler = EffectHandler(),
        reducer = Reducer(signIn, pomodoro, statistics, settings)
    )


    data class State(
        val signInState: SignIn.State = SignIn.State(),
        val pomodoroState: Pomodoro.State = Pomodoro.State(),
        val settingsState: Settings.State = Settings.State(),
        val statisticsState: Statistics.State = Statistics.State(),
        val screens: List<Screen<*, *, *>> = emptyList()
    ) : Elm.State {
        fun addScreen(newScreen: Screen<*, *, *>): State = this.copy(screens = this.screens + newScreen)
        fun dropScreen(n: Int = 1): State =
            this.copy(screens = if (this.screens.size < n) emptyList() else this.screens.dropLast(n))

        companion object {
            fun initWith(screen: Screen<*, *, *>): State = State(screens = listOf(screen))
        }
    }

    sealed class Action : Elm.Action {
        object Initialize : Action()

        object NavigateToPomodoroScreen : Action()
        object NavigateToStatisticsScreen : Action()
        object NavigateToSettingsScreen : Action()
        object NavigateBack : Action()

        data class ChangedSignInState(val newState: SignIn.State) : Action()
        data class ChangedPomodoroState(val newState: Pomodoro.State) : Action()
        data class ChangedStatisticsState(val newState: Statistics.State) : Action()
        data class ChangedSettingsState(val newState: Settings.State) : Action()
    }

    sealed class Effect : Elm.Effect

    class EffectHandler : Elm.EffectHandler<Effect, Action> {
        override suspend fun handle(effect: Effect): Action = Action.Initialize
    }

    class Reducer(
        private val signInScreen: Screen<SignIn.State, SignIn.Action, SignIn.Effect>,
        private val pomodoroScreen: Screen<Pomodoro.State, Pomodoro.Action, Pomodoro.Effect>,
        private val statisticsScreen: Screen<Statistics.State, Statistics.Action, Statistics.Effect>,
        private val settingsScreen: Screen<Settings.State, Settings.Action, Settings.Effect>
    ) : Elm.Reducer<State, Action, Effect> {
        override fun reduce(oldState: State, action: Action): Pair<State, Effect?> = when (action) {
            Action.Initialize -> State.initWith(signInScreen) to null

            Action.NavigateToPomodoroScreen -> oldState.addScreen(pomodoroScreen) to null
            Action.NavigateToStatisticsScreen -> oldState.addScreen(statisticsScreen) to null
            Action.NavigateToSettingsScreen -> oldState.addScreen(settingsScreen) to null
            Action.NavigateBack -> oldState.dropScreen() to null

            is Action.ChangedSignInState -> oldState.copy(signInState = action.newState) to null
            is Action.ChangedPomodoroState -> oldState.copy(pomodoroState = action.newState) to null
            is Action.ChangedStatisticsState -> oldState.copy(statisticsState = action.newState) to null
            is Action.ChangedSettingsState -> oldState.copy(settingsState = action.newState) to null
        }
    }
}
