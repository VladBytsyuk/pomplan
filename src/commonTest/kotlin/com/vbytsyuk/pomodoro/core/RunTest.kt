package com.vbytsyuk.pomodoro.core


expect fun <T> runTest(block: suspend () -> T)
