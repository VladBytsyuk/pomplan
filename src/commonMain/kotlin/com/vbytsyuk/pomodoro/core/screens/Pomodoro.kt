package com.vbytsyuk.pomodoro.core.screens

import com.vbytsyuk.pomodoro.core.api.SettingsRepository
import com.vbytsyuk.pomodoro.core.domain.PomodoroTime
import com.vbytsyuk.pomodoro.core.domain.pomodoroTime
import com.vbytsyuk.pomodoro.core.domain.second
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
        val logicState: LogicState = WAIT_FOR_WORK,
        val donePomodoroes: Int = 0,
        val currentSession: Int = 1,
        val time: PomodoroTime = pomodoroTime(minutes = 0, seconds = 0)
    ) : Elm.State {
        data class Rules(
            val workTime: PomodoroTime = pomodoroTime(minutes = 25),
            val shortBreakTime: PomodoroTime = pomodoroTime(minutes = 5),
            val longBreakTime: PomodoroTime = pomodoroTime(minutes = 15),
            val sessionLength: Int = 4
        )

        enum class LogicState { WAIT_FOR_WORK, WORK, WAIT_FOR_BREAK, BREAK }

        val isDone: Boolean get() = time <= 0
        val isLongBreak = donePomodoroes % rules.sessionLength == 0

        fun changeLogicState(newLogicState: LogicState, newCurrentSession: Int = this.currentSession) =
            copy(logicState = newLogicState, currentSession = newCurrentSession)

        fun takeSecond() = copy(time = time.takeSecond())
        fun addSession() = copy(currentSession = this.currentSession + 1)
        fun addPomodoro() = copy(donePomodoroes = donePomodoroes + 1)

        fun done(logicState: LogicState, time: PomodoroTime? = null) =
            this.copy(logicState = logicState, time = time ?: currentBreakTime)

        val currentBreakTime: PomodoroTime get() = if (isLongBreak) rules.longBreakTime else rules.shortBreakTime
        fun updateBreakTime(): State = copy(time = currentBreakTime)
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

    private class EffectHandler(
        private val settingsRepository: SettingsRepository
    ) : Elm.EffectHandler<Effect, Action> {
        override suspend fun handle(effect: Effect): Action = when (effect) {
            Effect.LoadRules -> {
                val rules = loadRules()
                Action.LoadedRules(rules)
            }
            Effect.Tick -> {
                delay(1.second)
                Action.Tick
            }
            is Effect.Done -> Action.Done
        }

        private suspend fun loadRules(): State.Rules = State.Rules(
            workTime = settingsRepository.getWorkTime(),
            shortBreakTime = settingsRepository.getShortBreakTime(),
            longBreakTime = settingsRepository.getLongBreakTime(),
            sessionLength = settingsRepository.getSessionLength()
        )
    }

    private class Reducer : Elm.Reducer<State, Action, Effect> {
        override fun reduce(oldState: State, action: Action): Pair<State, Effect?> = when (action) {
            Action.Initialize -> State() to Effect.LoadRules
            is Action.LoadedRules ->
                oldState.copy(logicState = WAIT_FOR_WORK, rules = action.rules, time = action.rules.workTime) to null

            is Action.Clicked -> reduceClicked(oldState, action)
            Action.Tick -> reduceTick(oldState)
            is Action.Done -> when (oldState.logicState) {
                WORK, WAIT_FOR_WORK ->
                    oldState.done(logicState = WAIT_FOR_BREAK).updateBreakTime() to null
                BREAK, WAIT_FOR_BREAK ->
                    oldState.done(
                        logicState = WAIT_FOR_WORK,
                        time = oldState.rules.workTime
                    ) to null
            }
        }

        private fun reduceClicked(oldState: State, action: Action.Clicked): Pair<State, Effect?> = when (action) {
            Action.Clicked.PlayPause -> when (oldState.logicState) {
                WAIT_FOR_WORK -> oldState.changeLogicState(WORK) to Effect.Tick
                WORK -> oldState.changeLogicState(WAIT_FOR_WORK) to null
                WAIT_FOR_BREAK -> oldState.changeLogicState(BREAK) to Effect.Tick
                BREAK -> oldState.changeLogicState(WAIT_FOR_BREAK) to null
            }

            Action.Clicked.Stop -> when (oldState.logicState) {
                WAIT_FOR_WORK -> oldState.copy(time = oldState.rules.workTime) to null
                WORK -> oldState.copy(logicState = WAIT_FOR_WORK, time = oldState.rules.workTime) to null
                WAIT_FOR_BREAK -> oldState.updateBreakTime() to null
                BREAK -> oldState.copy(logicState = WAIT_FOR_BREAK).updateBreakTime() to null
            }

            Action.Clicked.Skip -> when (oldState.logicState) {
                WAIT_FOR_WORK, WORK -> oldState.addPomodoro() to Effect.Done
                WAIT_FOR_BREAK, BREAK -> oldState to Effect.Done
            }
        }

        private fun reduceTick(oldState: State): Pair<State, Effect?> {
            val state = oldState.takeSecond()
            return when (oldState.logicState) {
                WORK -> when {
                    state.isDone -> state.addPomodoro() to Effect.Done
                    else -> state to Effect.Tick
                }

                BREAK -> when {
                    state.isDone && state.isLongBreak -> state.addSession() to Effect.Done
                    state.isDone -> state to Effect.Done
                    else -> state to Effect.Tick
                }

                else -> oldState to null
            }
        }

        private fun isLongBreak(state: State) = state.donePomodoroes % state.rules.sessionLength == 0
    }
}
