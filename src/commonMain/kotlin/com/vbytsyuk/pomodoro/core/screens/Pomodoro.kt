package com.vbytsyuk.pomodoro.core.screens

import com.vbytsyuk.pomodoro.core.api.SettingsRepository
import com.vbytsyuk.pomodoro.core.domain.PomodoroTime
import com.vbytsyuk.pomodoro.core.domain.seconds
import com.vbytsyuk.pomodoro.core.screens.Pomodoro.State.LogicState.*
import com.vbytsyuk.pomodoro.elm.Elm
import kotlinx.coroutines.delay


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
        val currentSession: Int = 1,
        val time: PomodoroTime = PomodoroTime(minutes = 0, seconds = 0)
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

        fun changeLogicState(newLogicState: LogicState, newCurrentSession: Int = this.currentSession) =
            copy(logicState = newLogicState, currentSession = newCurrentSession)

        fun addSecond() = copy(time = time.addSecond())
        fun takeSecond() = copy(time = time.takeSecond())

        fun addSession() = copy(currentSession = this.currentSession + 1)
    }

    sealed class Action : Elm.Action {
        object Initialize : Action()
        data class LoadedRules(val rules: State.Rules) : Action()
        sealed class Clicked : Action() {
            object PlayPause : Clicked()
            object Stop : Clicked()
            object Skip : Clicked()
        }
        object Tick : Action()
        object Done : Action()
    }

    sealed class Effect : Elm.Effect {
        object LoadRules : Effect()
        object Tick : Effect()
        object Done : Effect()
    }

    class EffectHandler(private val settingsRepository: SettingsRepository) : Elm.EffectHandler<Effect, Action> {
        override suspend fun handle(effect: Effect): Action = when (effect) {
            Effect.LoadRules -> {
                val rules = loadRules()
                Action.LoadedRules(rules)
            }
            Effect.Tick -> {
                delay(1.seconds)
                Action.Tick
            }
            Effect.Done -> Action.Done
        }

        private suspend fun loadRules(): State.Rules = State.Rules(
            workTime = settingsRepository.getWorkTime(),
            shortBreakTime = settingsRepository.getShortBreakTime(),
            longBreakTime = settingsRepository.getLongBreakTime(),
            sessionLength = settingsRepository.getSessionLength()
        )
    }

    class Reducer : Elm.Reducer<State, Action, Effect> {
        override fun reduce(oldState: State, action: Action): Pair<State, Effect?> = when (action) {
            Action.Initialize -> State() to Effect.LoadRules
            is Action.LoadedRules -> oldState.copy(rules = action.rules) to null

            is Action.Clicked -> reduceClicked(oldState, action)
            Action.Tick -> reduceTick(oldState)
            Action.Done -> when (oldState.logicState) {
                Work ->  oldState.changeLogicState(WaitForBreak) to null
                Break ->  oldState.changeLogicState(WaitForWork) to null
                else ->  oldState to null
            }
        }

        private fun reduceClicked(oldState: State, action: Action.Clicked): Pair<State, Effect?> = when (action) {
            Action.Clicked.PlayPause -> when (oldState.logicState) {
                WaitForWork ->  oldState.changeLogicState(Work) to Effect.Tick
                Work ->  oldState.changeLogicState(WaitForWork) to null
                WaitForBreak ->  oldState.changeLogicState(Break) to Effect.Tick
                Break ->  oldState.changeLogicState(WaitForBreak) to null
            }

            Action.Clicked.Stop -> oldState.copy(logicState = WaitForWork) to null

            Action.Clicked.Skip ->  when (oldState.logicState) {
                WaitForWork -> oldState.changeLogicState(WaitForBreak) to null
                WaitForBreak -> oldState.changeLogicState(WaitForWork) to null
                else -> oldState to null
            }
        }

        private fun reduceTick(oldState: State): Pair<State, Effect?> = when (oldState.logicState) {
            Work -> {
                val stateWithUpdatedTime = oldState.takeSecond()
                val effect = if (stateWithUpdatedTime.time <= 0) Effect.Done else Effect.Tick
                stateWithUpdatedTime to effect
            }
            Break -> {
                val rules = oldState.rules
                val isLongBreak = oldState.currentSession % rules.sessionLength == 0
                val breakTime = if (isLongBreak) rules.longBreakTime else rules.shortBreakTime

                val stateWithUpdatedTime = oldState.addSecond()
                val isDone = stateWithUpdatedTime.time >= breakTime
                val effect = if (isDone) Effect.Done else Effect.Tick

                val newState = if (isLongBreak && isDone) stateWithUpdatedTime.addSession() else stateWithUpdatedTime
                newState to effect
            }
            else -> oldState to null
        }
    }
}