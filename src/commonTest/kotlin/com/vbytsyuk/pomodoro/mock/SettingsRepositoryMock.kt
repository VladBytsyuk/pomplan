package com.vbytsyuk.pomodoro.mock

import com.vbytsyuk.pomodoro.core.api.SettingsRepository
import com.vbytsyuk.pomodoro.core.domain.pomodoroTime


class SettingsRepositoryMock : SettingsRepository {
    override suspend fun getWorkTime() = pomodoroTime(seconds = 3)
    override suspend fun getShortBreakTime()= pomodoroTime(seconds = 1)
    override suspend fun getLongBreakTime()= pomodoroTime(seconds = 2)
    override suspend fun getSessionLength() = 4
}
