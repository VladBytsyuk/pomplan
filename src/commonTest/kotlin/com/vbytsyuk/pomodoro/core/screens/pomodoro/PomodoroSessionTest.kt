package com.vbytsyuk.pomodoro.core.screens.pomodoro

import com.vbytsyuk.pomodoro.core.domain.pomodoroTime
import com.vbytsyuk.pomodoro.core.domain.milliseconds
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import kotlin.test.Test


class `Pomodoro session test` : `Pomodoro screen`() {
    @Test
    fun `Check 1st break time`() = test(
        actionsWithDelay = listOf(
            Pomodoro.Action.Initialize to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds
        ),
        expectedState = Pomodoro.State(
            rules = rules,
            currentSession = 1,
            donePomodoroes = 1,
            remainTime = pomodoroTime(seconds = SHORT_BREAK),
            logicState = Pomodoro.State.LogicState.WAIT_FOR_BREAK
        )
    )

    @Test
    fun `Check 2nd break time`() = test(
        actionsWithDelay = listOf(
            Pomodoro.Action.Initialize to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds
        ),
        expectedState = Pomodoro.State(
            rules = rules,
            currentSession = 1,
            donePomodoroes = 2,
            remainTime = pomodoroTime(seconds = SHORT_BREAK),
            logicState = Pomodoro.State.LogicState.WAIT_FOR_BREAK
        )
    )

    @Test
    fun `Check 3rd break time`() = test(
        actionsWithDelay = listOf(
            Pomodoro.Action.Initialize to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds
        ),
        expectedState = Pomodoro.State(
            rules = rules,
            currentSession = 1,
            donePomodoroes = 3,
            remainTime = pomodoroTime(seconds = SHORT_BREAK),
            logicState = Pomodoro.State.LogicState.WAIT_FOR_BREAK
        )
    )

    @Test
    fun `Check 4th break time`() = test(
        actionsWithDelay = listOf(
            Pomodoro.Action.Initialize to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds,
            Pomodoro.Action.Clicked.Skip to 50.milliseconds
        ),
        expectedState = Pomodoro.State(
            rules = rules,
            currentSession = 1,
            donePomodoroes = 4,
            remainTime = pomodoroTime(seconds = LONG_BREAK),
            logicState = Pomodoro.State.LogicState.WAIT_FOR_BREAK
        )
    )
}
