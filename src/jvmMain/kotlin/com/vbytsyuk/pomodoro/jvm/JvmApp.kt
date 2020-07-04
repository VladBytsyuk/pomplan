package com.vbytsyuk.pomodoro.jvm

import javafx.stage.Stage
import tornadofx.*


class JvmApp : App(PomodoroView::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        stage.width = Sizes.Window.WIDTH
        stage.height = Sizes.Window.HEIGHT
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    launch<JvmApp>(args)
}
