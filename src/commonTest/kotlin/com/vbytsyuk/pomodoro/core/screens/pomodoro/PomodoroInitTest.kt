package com.vbytsyuk.pomodoro.core.screens.pomodoro

import com.vbytsyuk.pomodoro.core.domain.milliseconds
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import kotlin.test.Test


class `Pomodoro screen init` : `Pomodoro screen`() {
    @Test
    fun `Screen initialization`() = test(
        actionsWithDelay = listOf(
            Pomodoro.Action.Initialize to 50.milliseconds
        ),
        expectedState = Pomodoro.State(rules = rules, time = rules.workTime)
    )
}
