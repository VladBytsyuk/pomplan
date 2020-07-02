package com.vbytsyuk.pomodoro.jvm

import com.vbytsyuk.pomodoro.core.repositories.SettingsRepositoryImpl
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import com.vbytsyuk.pomodoro.core.screens.Pomodoro.*
import com.vbytsyuk.pomodoro.jvm.extensions.px
import com.vbytsyuk.pomodoro.jvm.widgets.Colors
import com.vbytsyuk.pomodoro.jvm.widgets.ppButton
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.text.FontWeight
import tornadofx.*


class PomodoroView : UIComponent() {
    private val controller: PomodoroController by inject()

    override val root = vbox {
        prefWidth = 512.0
        label(controller.time) {
            usePrefWidth = true
            style {
                fontSize = 72.px
                fontWeight = FontWeight.EXTRA_BOLD
                padding = box(horizontal = 64.px, vertical = 32.px)
            }
        }
        label(controller.logicState) {
            style {
                fontSize = 24.px
                padding = box(all = 16.px)
            }
        }
        label(controller.donePomodoroes) {
            style {
                fontSize = 24.px
                padding = box(all = 16.px)
            }
        }
        hbox {
            ppButton("Stop", color = Colors.black) {
                action { controller.setAction(Action.Clicked.Stop) }
            }
            ppButton(controller.playPause) {
                action { controller.setAction(Action.Clicked.PlayPause) }
            }
            ppButton("Skip", color = Colors.red) {
                action { controller.setAction(Action.Clicked.Skip) }
            }
        }
    }
}

class PomodoroController : PomPlanController<State, Action>(
    elmController = Pomodoro(settingsRepository = SettingsRepositoryImpl()).controller
) {
    var time = SimpleObjectProperty("")
    var logicState = SimpleObjectProperty("")
    val donePomodoroes = SimpleObjectProperty("")
    val playPause = SimpleObjectProperty("")

    override fun render(state: State) {
        time.set(state.time.toString())
        logicState.set(state.logicState.toString())
        donePomodoroes.set(state.donePomodoroes.toString())
        playPause.set(
            when (state.logicState) {
                State.LogicState.WAIT_FOR_WORK, State.LogicState.WAIT_FOR_BREAK -> "Play"
                State.LogicState.WORK, State.LogicState.BREAK -> "Pause"
            }
        )
    }
}
