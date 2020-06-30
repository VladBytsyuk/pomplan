package com.vbytsyuk.pomodoro.core

import com.vbytsyuk.pomodoro.core.domain.PomodoroTime
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import com.vbytsyuk.pomodoro.core.screens.Pomodoro.*
import com.vbytsyuk.pomodoro.mock.SettingsRepositoryMock
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.test.assertEquals


class `Pomodoro screen` {

    @Test
    fun `Pomodoro initialization`() = test(
        actions = listOf(
            Action.Initialize
        ),
        expectedState = State()
    )

    @Test
    fun `Pomodoro start`() = test(
        actions = listOf(
            Action.Initialize,
            Action.Clicked.PlayPause
        ),
        expectedState = State(
            rules = State.Rules(),
            currentSession = 1,
            time = PomodoroTime(minutes = 25),
            logicState = State.LogicState.WORK
        )
    )


    private fun test(
        actions: List<Action>,
        expectedState: State
    ) = runBlockingTest {
        val controller = Pomodoro(settingsRepository = SettingsRepositoryMock()).controller
        controller.attach()

        actions.forEach {
            controller.setAction(it)
            delay(1000)
        }

        controller.detach()
        assertEquals(expected = expectedState, actual = controller.currentState)
    }
}
