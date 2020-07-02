package com.vbytsyuk.pomodoro.mock

import com.vbytsyuk.pomodoro.core.api.SettingsRepository
import com.vbytsyuk.pomodoro.core.domain.PomodoroTime


class SettingsRepositoryMock : SettingsRepository {
    override suspend fun getWorkTime() = PomodoroTime(seconds = 3)
    override suspend fun getShortBreakTime()= PomodoroTime(seconds = 1)
    override suspend fun getLongBreakTime()= PomodoroTime(seconds = 2)
    override suspend fun getSessionLength() = 4
}
