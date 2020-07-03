package com.vbytsyuk.pomodoro.core.screens.pomodoro

import com.vbytsyuk.pomodoro.core.domain.pomodoroTime
import com.vbytsyuk.pomodoro.core.domain.milliseconds
import com.vbytsyuk.pomodoro.core.domain.seconds
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import kotlin.test.Test


class `Pomodoro PLAY button test` : `Pomodoro screen`() {
    @Test
    fun `Play click & wait work time`() = test(
        actionsWithDelay = listOf(
            Pomodoro.Action.Initialize to 50.milliseconds,
            Pomodoro.Action.Clicked.PlayPause to (WORK + 0.5).seconds
        ),
        expectedState = Pomodoro.State(
            rules = rules,
            currentSession = 1,
            donePomodoroes = 1,
            time = pomodoroTime(seconds = SHORT_BREAK),
            logicState = Pomodoro.State.LogicState.WAIT_FOR_BREAK
        )
    )

    @Test
    fun `Play click`() = test(
        actionsWithDelay = listOf(
            Pomodoro.Action.Initialize to 50.milliseconds,
            Pomodoro.Action.Clicked.PlayPause to 3.seconds
        ),
        expectedState = Pomodoro.State(
            rules = rules,
            currentSession = 1,
            donePomodoroes = 0,
            time = pomodoroTime(seconds = WORK - 2),
            logicState = Pomodoro.State.LogicState.WORK
        )
    )

    @Test
    fun `Play click with pause`() = test(
        actionsWithDelay = listOf(
            Pomodoro.Action.Initialize to 50.milliseconds,
            Pomodoro.Action.Clicked.PlayPause to 50.milliseconds,
            Pomodoro.Action.Clicked.PlayPause to 3.seconds,
            Pomodoro.Action.Clicked.PlayPause to 50.milliseconds
        ),
        expectedState = Pomodoro.State(
            rules = rules,
            currentSession = 1,
            donePomodoroes = 0,
            time = pomodoroTime(seconds = WORK),
            logicState = Pomodoro.State.LogicState.WORK
        )
    )
}
