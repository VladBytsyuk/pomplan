package com.vbytsyuk.pomodoro.core.screens.pomodoro

import com.vbytsyuk.pomodoro.core.api.SettingsRepository
import com.vbytsyuk.pomodoro.core.domain.pomodoroTime
import com.vbytsyuk.pomodoro.core.runTest
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import com.vbytsyuk.pomodoro.core.screens.Pomodoro.*
import kotlinx.coroutines.delay
import kotlin.test.assertEquals


open class `Pomodoro screen` {
    companion object {
        const val WORK = 3
        const val SHORT_BREAK = 1
        const val LONG_BREAK = 2
    }

    private val settingsRepository = object : SettingsRepository {
        override suspend fun getWorkTime() = pomodoroTime(seconds = WORK)
        override suspend fun getShortBreakTime()= pomodoroTime(seconds = SHORT_BREAK)
        override suspend fun getLongBreakTime()= pomodoroTime(seconds = LONG_BREAK)
        override suspend fun getSessionLength() = 4
    }

    protected val rules = State.Rules(
        workTime = pomodoroTime(seconds = WORK),
        shortBreakTime = pomodoroTime(seconds = SHORT_BREAK),
        longBreakTime = pomodoroTime(seconds = LONG_BREAK),
        sessionLength = 4
    )

    protected fun test(
        actionsWithDelay: List<Pair<Action, Long>>,
        expectedState: State
    ) = runTest {
        val controller = Pomodoro(settingsRepository = settingsRepository).controller
        controller.attach()

        actionsWithDelay.forEach { (action, delayInMillis) ->
            controller.setAction(action)
            delay(delayInMillis)
        }

        controller.detach()
        assertEquals(expected = expectedState, actual = controller.currentState)
    }
}
