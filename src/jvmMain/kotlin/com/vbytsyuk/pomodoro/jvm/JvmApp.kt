package com.vbytsyuk.pomodoro.jvm

import tornadofx.*


class JvmApp : App(PomodoroView::class)

fun main(args: Array<String>) {
    launch<JvmApp>(args)
}
