package com.vbytsyuk.pomodoro.core.screens

import com.vbytsyuk.pomodoro.elm.Elm


class Settings : App.Screen<Settings.State, Settings.Action, Settings.Effect> {

    override val controller: Elm.Controller<State, Action, Effect> = Elm.ControllerImpl(
        initialState = State(),
        initialAction = Action.Initialize,
        effectHandler = EffectHandler(),
        reducer = Reducer()
    )


    data class State(val darkTheme: Boolean = false): Elm.State

    sealed class Action : Elm.Action {
        object Initialize : Action()
        object ChangeTheme : Action()
    }

    sealed class Effect : Elm.Effect

    class EffectHandler : Elm.EffectHandler<Effect, Action> {
        override suspend fun handle(effect: Effect): Action = Action.Initialize
    }

    class Reducer : Elm.Reducer<State, Action, Effect> {
        override fun reduce(oldState: State, action: Action): Pair<State, Effect?> = when (action) {
            Action.Initialize -> State() to null
            Action.ChangeTheme -> oldState.copy(darkTheme = !oldState.darkTheme) to null
        }
    }
}
