package com.vbytsyuk.pomodoro.jvm

import com.vbytsyuk.pomodoro.core.repositories.SettingsRepositoryImpl
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import com.vbytsyuk.pomodoro.core.screens.Pomodoro.*
import com.vbytsyuk.pomodoro.jvm.extensions.px
import com.vbytsyuk.pomodoro.jvm.widgets.ButtonsView
import com.vbytsyuk.pomodoro.jvm.widgets.Colors
import com.vbytsyuk.pomodoro.jvm.widgets.ppButton
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*


class PomodoroView : UIComponent() {
    private val controller: PomodoroController by inject()

    override val root = borderpane {
        top<ProgressView>()
        center<ButtonsView>()
        bottom = hbox {
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
        style {
        }
        controller.backgroundColor.addListener { _, _, color ->
            style { backgroundColor += color }
        }
    }
}

class PomodoroController : PomPlanController<State, Action>(
    elmController = Pomodoro(settingsRepository = SettingsRepositoryImpl()).controller
) {
    private val progressController: ProgressController by inject()

    var backgroundColor = SimpleObjectProperty(Colors.white)
    val donePomodoroes = SimpleObjectProperty("")
    val playPause = SimpleObjectProperty("")

    override fun render(state: State) {
        val background = when (state.logicState) {
            State.LogicState.WAIT_FOR_WORK, State.LogicState.WORK -> Colors.red
            State.LogicState.WAIT_FOR_BREAK, State.LogicState.BREAK -> Colors.green
        }
        backgroundColor.set(background)
        progressController.render(
            time = state.time,
            maxTime = when (state.logicState) {
                State.LogicState.WAIT_FOR_WORK, State.LogicState.WORK -> state.rules.workTime
                State.LogicState.WAIT_FOR_BREAK, State.LogicState.BREAK -> state.currentBreakTime
            },
            backgroundColor = background,
            contentColor = Colors.white
        )
        donePomodoroes.set(state.donePomodoroes.toString())
        playPause.set(
            when (state.logicState) {
                State.LogicState.WAIT_FOR_WORK, State.LogicState.WAIT_FOR_BREAK -> "Play"
                State.LogicState.WORK, State.LogicState.BREAK -> "Pause"
            }
        )
    }
}
