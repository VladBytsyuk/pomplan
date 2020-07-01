package com.vbytsyuk.pomodoro.jvm

import com.vbytsyuk.pomodoro.core.repositories.SettingsRepositoryImpl
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import com.vbytsyuk.pomodoro.jvm.extensions.px
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*


class PomodoroView : UIComponent() {
    private val controller: PomodoroController by inject()

    override val root = borderpane {
        center = label(controller.time) {
            style {
                usePrefSize = true
                fontSize = 36.px
                padding = box(all = 256.px)
            }
        }
        bottom = button("Play") {
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

    override fun render(state: Pomodoro.State) {
        time.set(state.time.toString())
    }
}
