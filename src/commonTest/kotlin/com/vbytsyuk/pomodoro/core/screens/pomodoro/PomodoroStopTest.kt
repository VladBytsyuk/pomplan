package com.vbytsyuk.pomodoro.core.screens.pomodoro

import com.vbytsyuk.pomodoro.core.domain.pomodoroTime
import com.vbytsyuk.pomodoro.core.domain.milliseconds
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import kotlin.test.Test


class `Pomodoro STOP button test` : `Pomodoro screen`() {
    @Test
    fun `Stop click`() = test(
        actionsWithDelay = listOf(
            Pomodoro.Action.Initialize to 50.milliseconds,
            Pomodoro.Action.Clicked.Stop to 50.milliseconds
        ),
        expectedState = Pomodoro.State(
            rules = rules,
            currentSession = 1,
            donePomodoroes = 0,
            time = pomodoroTime(seconds = WORK),
            logicState = Pomodoro.State.LogicState.WAIT_FOR_WORK
        )
    )

    @Test
    fun `Stop click x2`() = test(
        actionsWithDelay = listOf(
            Pomodoro.Action.Initialize to 50.milliseconds,
            Pomodoro.Action.Clicked.Stop to 50.milliseconds,
            Pomodoro.Action.Clicked.Stop to 50.milliseconds
        ),
        expectedState = Pomodoro.State(
            rules = rules,
            currentSession = 1,
            donePomodoroes = 0,
            time = pomodoroTime(seconds = WORK),
            logicState = Pomodoro.State.LogicState.WAIT_FOR_WORK
        )
    )
}
