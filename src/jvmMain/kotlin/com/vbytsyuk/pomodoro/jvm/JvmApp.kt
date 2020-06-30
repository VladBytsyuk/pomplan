package com.vbytsyuk.pomodoro.jvm

import tornadofx.*


class JvmApp : App(AppView::class)

fun main(args: Array<String>) {
    launch<JvmApp>(args)
}
