package com.vbytsyuk.pomodoro.core

import kotlinx.coroutines.runBlocking


actual fun <T> runTest(block: suspend () -> T) {
    runBlocking { block() }
}
