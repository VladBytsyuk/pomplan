package com.vbytsyuk.pomodoro.core.screens

import com.vbytsyuk.pomodoro.core.api.SettingsRepository
import com.vbytsyuk.pomodoro.core.domain.PomodoroTime
import com.vbytsyuk.pomodoro.core.screens.Pomodoro.State.LogicState.*
import com.vbytsyuk.pomodoro.elm.Elm


class Pomodoro(
    settingsRepository: SettingsRepository
) : App.Screen<Pomodoro.State, Pomodoro.Action, Pomodoro.Effect> {

    override val controller: Elm.Controller<State, Action, Effect> = Elm.ControllerImpl(
        initialState = State(),
        initialAction = Action.Initialize,
        effectHandler = EffectHandler(settingsRepository),
        reducer = Reducer()
    )

    data class State(
        val rules: Rules = Rules(),
        val logicState: LogicState = WaitForWork,
        val currentSession: Int = 1
    ) : Elm.State {
        data class Rules(
            val workTime: PomodoroTime = PomodoroTime(minutes = 25),
            val shortBreakTime: PomodoroTime = PomodoroTime(minutes = 5),
            val longBreakTime: PomodoroTime = PomodoroTime(minutes = 15),
            val sessionLength: Int = 4
        )

        sealed class LogicState {
            object WaitForWork : LogicState()
            object Work : LogicState()
            object WaitForBreak : LogicState()
            object Break : LogicState()
        }

        fun changeLogicState(newLogicState: LogicState) = copy(logicState = newLogicState)
    }

    sealed class Action : Elm.Action {
        object Initialize : Action()
        data class LoadedPomodoroRules(val rules: State.Rules) : Action()
        sealed class Clicked : Action() {
            object PlayPause : Clicked()
            object Stop : Clicked()
            object Skip : Clicked()
        }
        sealed class Control : Action() {
            object PlusSecond : Control()
            object MinusSecond : Control()
        }
    }

    sealed class Effect : Elm.Effect {
        object LoadPomodoroRules : Effect()
    }

    class EffectHandler(private val settingsRepository: SettingsRepository) : Elm.EffectHandler<Effect, Action> {
        override suspend fun handle(effect: Effect): Action = when (effect) {
            Effect.LoadPomodoroRules -> {
                val rules = loadPomodoroRules()
                Action.LoadedPomodoroRules(rules)
            }
        }

        private suspend fun loadPomodoroRules(): State.Rules = State.Rules(
            workTime = settingsRepository.getWorkTime(),
            shortBreakTime = settingsRepository.getShortBreakTime(),
            longBreakTime = settingsRepository.getLongBreakTime(),
            sessionLength = settingsRepository.getSessionLength()
        )
    }

    class Reducer : Elm.Reducer<State, Action, Effect> {
        override fun reduce(oldState: State, action: Action): Pair<State, Effect?> = when (action) {
            Action.Initialize -> State() to Effect.LoadPomodoroRules
            is Action.LoadedPomodoroRules -> oldState.copy(rules = action.rules) to null

            is Action.Clicked -> reduceClicked(oldState, action)
            is Action.Control -> reduceControl(oldState, action)
        }

        private fun reduceClicked(oldState: State, action: Action.Clicked): Pair<State, Effect?> = when (action) {
            Action.Clicked.PlayPause -> when (oldState.logicState) {
                WaitForWork ->  oldState.changeLogicState(Work) to null
                Work ->  oldState.changeLogicState(WaitForWork) to null
                WaitForBreak ->  oldState.changeLogicState(Break) to null
                Break ->  oldState.changeLogicState(WaitForBreak) to null
            }

            Action.Clicked.Stop -> oldState.copy(logicState = WaitForWork) to null

            Action.Clicked.Skip ->  when (oldState.logicState) {
                WaitForWork -> oldState.changeLogicState(WaitForBreak) to null
                WaitForBreak -> oldState.changeLogicState(WaitForWork) to null
                else -> oldState to null
            }
        }


        private fun reduceControl(oldState: State, action: Action.Control): Pair<State, Effect?> = when (action) {
            Action.Control.PlusSecond -> TODO()
            Action.Control.MinusSecond -> TODO()
        }
    }
}