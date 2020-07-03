package com.vbytsyuk.pomodoro.core.screens

import com.vbytsyuk.pomodoro.elm.Elm


class Statistics : App.Screen<Statistics.State, Statistics.Action, Statistics.Effect> {
    override val controller: Elm.Controller<State, Action, Effect> = Elm.ControllerImpl(
        initialState = State(),
        initialAction = Action.Initialize,
        effectHandler = EffectHandler(),
        reducer = Reducer()
    )

    data class State(val count: Int = 0) : Elm.State

    sealed class Action : Elm.Action {
        object Initialize : Action()
    }

    sealed class Effect : Elm.Effect

    class EffectHandler : Elm.EffectHandler<Effect, Action> {
        override suspend fun handle(effect: Effect): Action = Action.Initialize
    }

    class Reducer : Elm.Reducer<State, Action, Effect> {
        override fun reduce(oldState: State, action: Action): Pair<State, Effect?> = when (action) {
            Action.Initialize -> State() to null
        }
    }
}
