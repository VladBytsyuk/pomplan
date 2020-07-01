package com.vbytsyuk.pomodoro.core.screens

import com.vbytsyuk.pomodoro.core.api.SettingsRepository
import com.vbytsyuk.pomodoro.core.domain.PomodoroTime
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
        val time: PomodoroTime = PomodoroTime(minutes = 0, seconds = 0)
    ) : Elm.State {
        data class Rules(
            val workTime: PomodoroTime = PomodoroTime(minutes = 25),
            val shortBreakTime: PomodoroTime = PomodoroTime(minutes = 5),
            val longBreakTime: PomodoroTime = PomodoroTime(minutes = 15),
            val sessionLength: Int = 4
        )

        enum class LogicState { WAIT_FOR_WORK, WORK, WAIT_FOR_BREAK, BREAK }

        fun changeLogicState(newLogicState: LogicState, newCurrentSession: Int = this.currentSession) =
            copy(logicState = newLogicState, currentSession = newCurrentSession)

        fun takeSecond() = copy(time = time.takeSecond())

        fun addPomodoro() = copy(donePomodoroes = this.donePomodoroes + 1)
        fun addSession() = copy(currentSession = this.currentSession + 1)

        fun done(logicState: LogicState, addPomodoro: Boolean, time: PomodoroTime) = this.copy(
            logicState = logicState,
            donePomodoroes = if (addPomodoro) donePomodoroes + 1 else donePomodoroes,
            time = time
        )
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

    private class EffectHandler(private val settingsRepository: SettingsRepository) : Elm.EffectHandler<Effect, Action> {
        override suspend fun handle(effect: Effect): Action = when (effect) {
            Effect.LoadRules -> {
                val rules = loadRules()
                Action.LoadedRules(rules)
            }
            Effect.Tick -> {
                delay(1.second)
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

    private class Reducer : Elm.Reducer<State, Action, Effect> {
        override fun reduce(oldState: State, action: Action): Pair<State, Effect?> = when (action) {
            Action.Initialize -> State() to Effect.LoadRules
            is Action.LoadedRules ->
                oldState.copy(logicState = WAIT_FOR_WORK, rules = action.rules, time = action.rules.workTime) to null

            is Action.Clicked -> reduceClicked(oldState, action)
            Action.Tick -> reduceTick(oldState)
            Action.Done -> when (oldState.logicState) {
                WORK, WAIT_FOR_WORK ->
                    oldState.done(
                        logicState = WAIT_FOR_BREAK,
                        addPomodoro = true,
                        time = if (isLongBreak(oldState)) oldState.rules.longBreakTime else oldState.rules.shortBreakTime
                    ) to null
                BREAK, WAIT_FOR_BREAK ->
                    oldState.done(
                        logicState = WAIT_FOR_WORK,
                        addPomodoro = false,
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

            Action.Clicked.Stop -> oldState.copy(logicState = WAIT_FOR_WORK) to null

            Action.Clicked.Skip -> when (oldState.logicState) {
                WAIT_FOR_WORK, WORK -> oldState.addPomodoro() to Effect.Done
                WAIT_FOR_BREAK, BREAK -> oldState to Effect.Done
            }
        }

        private fun reduceTick(oldState: State): Pair<State, Effect?> = when (oldState.logicState) {
            WORK -> {
                val stateWithUpdatedTime = oldState.takeSecond()
                val effect = if (stateWithUpdatedTime.time <= 0) Effect.Done else Effect.Tick
                stateWithUpdatedTime to effect
            }
            BREAK -> {
                val stateWithUpdatedTime = oldState.takeSecond()
                val isDone = stateWithUpdatedTime.time <= 0
                val effect = if (isDone) Effect.Done else Effect.Tick

                val newState = if (isLongBreak(oldState) && isDone) stateWithUpdatedTime.addSession() else stateWithUpdatedTime
                newState to effect
            }
            else -> oldState to null
        }

        private fun isLongBreak(state: State) = state.donePomodoroes % state.rules.sessionLength == 0
    }
}
