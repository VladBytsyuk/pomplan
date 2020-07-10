package com.vbytsyuk.pomodoro.core.screens.pomodoro

import com.vbytsyuk.pomodoro.core.domain.pomodoroTime
import com.vbytsyuk.pomodoro.core.domain.milliseconds
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import com.vbytsyuk.pomodoro.core.screens.Pomodoro.Action.Clicked.Skip
import com.vbytsyuk.pomodoro.core.screens.Pomodoro.Action.Initialize
import com.vbytsyuk.pomodoro.core.screens.Pomodoro.State.LogicState.WAIT_FOR_BREAK
import com.vbytsyuk.pomodoro.core.screens.Pomodoro.State.LogicState.WAIT_FOR_WORK
import kotlin.test.Test


class `Pomodoro SKIP button test` : `Pomodoro screen`() {
    @Test
    fun `Skip click`() = test(
        actionsWithDelay = listOf(
            Initialize to 50.milliseconds,
            Skip to 50.milliseconds
        ),
        expectedState = Pomodoro.State(
            rules = rules,
            currentSession = 1,
            donePomodoroes = 1,
            remainTime = pomodoroTime(seconds = SHORT_BREAK),
            logicState = WAIT_FOR_BREAK
        )
    )

    @Test
    fun `Skip click x2`() = test(
        actionsWithDelay = listOf(
            Initialize to 50.milliseconds,
            Skip to 50.milliseconds,
            Skip to 50.milliseconds
        ),
        expectedState = Pomodoro.State(
            rules = rules,
            currentSession = 1,
            donePomodoroes = 1,
            remainTime = pomodoroTime(seconds = WORK),
            logicState = WAIT_FOR_WORK
        )
    )
}
