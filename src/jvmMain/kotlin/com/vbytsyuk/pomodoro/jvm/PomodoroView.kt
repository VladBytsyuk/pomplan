package com.vbytsyuk.pomodoro.jvm

import com.vbytsyuk.pomodoro.core.repositories.SettingsRepositoryImpl
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import com.vbytsyuk.pomodoro.jvm.extensions.px
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*


class PomodoroView : UIComponent() {
    private val controller: PomodoroController by inject()

    override val root = vbox {
        label(controller.time) {
            style {
                usePrefWidth = true
                fontSize = 36.px
                padding = box(all = 256.px)
            }
        }
        label(controller.logicState) {
            style {
                useMaxWidth = true
                fontSize = 24.px
                padding = box(all = 16.px)
            }
        }
        label(controller.donePomodoroes) {
            style {
                useMaxWidth = true
                fontSize = 24.px
                padding = box(all = 16.px)
            }
        }
        button(controller.playPause) {
            action { controller.setAction(Pomodoro.Action.Clicked.PlayPause) }
            style {
                useMaxWidth = true
                fontSize = 36.px
                padding = box(all = 24.px)
            }
        }
    }
}

class PomodoroController : PomPlanController<Pomodoro.State, Pomodoro.Action>(
    elmController = Pomodoro(settingsRepository = SettingsRepositoryImpl()).controller
) {
    var time = SimpleObjectProperty("")
    var logicState = SimpleObjectProperty("")
    val donePomodoroes = SimpleObjectProperty("")
    val playPause = SimpleObjectProperty("")

    override fun render(state: Pomodoro.State) {
        time.set(state.time.toString())
        logicState.set(state.logicState.toString())
        donePomodoroes.set(state.donePomodoroes.toString())
        playPause.set(
            when (state.logicState) {
                Pomodoro.State.LogicState.WAIT_FOR_WORK, Pomodoro.State.LogicState.WAIT_FOR_BREAK -> "Play"
                Pomodoro.State.LogicState.WORK, Pomodoro.State.LogicState.BREAK -> "Pause"
            }
        )
    }
}
