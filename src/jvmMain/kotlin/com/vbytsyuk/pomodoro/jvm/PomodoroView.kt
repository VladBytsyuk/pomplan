package com.vbytsyuk.pomodoro.jvm

import com.vbytsyuk.pomodoro.core.repositories.SettingsRepositoryImpl
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import com.vbytsyuk.pomodoro.core.screens.Pomodoro.*
import com.vbytsyuk.pomodoro.jvm.Sizes.Design.Button
import com.vbytsyuk.pomodoro.jvm.Sizes.SCALING_FACTOR
import com.vbytsyuk.pomodoro.jvm.extensions.px
import com.vbytsyuk.pomodoro.jvm.widgets.Colors
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Pane
import javafx.scene.text.FontWeight
import tornadofx.*


class PomodoroView : UIComponent() {
    private val controller: PomodoroController by inject()

    override val root = vbox {
        space(vertical = 24.0)
        progressView()
        space(vertical = 24.0)
        buttonsView()
        space(vertical = 24.0)
        doneView()

        vboxConstraints {
            alignment = Pos.CENTER
        }

        controller.backgroundColor.addListener { _, _, color ->
            style { backgroundColor += color }
        }
    }

    private fun EventTarget.progressView() = stackpane {
        group {
            rectangle(x = 0.0, y = 0.0, width = Sizes.Pomodoro.Progress.SIZE, height = Sizes.Pomodoro.Progress.SIZE) {
                fillProperty().bind(controller.backgroundColor)
            }
            arc(
                centerX = Sizes.Pomodoro.Progress.SIZE / 2, centerY = Sizes.Pomodoro.Progress.SIZE / 2,
                radiusX = Sizes.Pomodoro.Progress.SIZE / 2, radiusY = Sizes.Pomodoro.Progress.SIZE / 2,
                startAngle = 90.0
            ) {
                lengthProperty().bind(controller.angle)
                type = javafx.scene.shape.ArcType.ROUND
                fill = Colors.white
            }
            circle(
                centerX = Sizes.Pomodoro.Progress.SIZE / 2, centerY = Sizes.Pomodoro.Progress.SIZE / 2,
                radius = Sizes.Pomodoro.Progress.SIZE / 2 - 16
            ) {
                fillProperty().bind(controller.backgroundColor)
            }
        }
        label(controller.timeText) {
            style {
                fontSize = (64 * SCALING_FACTOR).px
                fontWeight = FontWeight.BOLD
                textFillProperty().bind(controller.contentColor)
            }
        }
    }

    private fun EventTarget.buttonsView() = hbox {
        maxWidth = Sizes.Pomodoro.Buttons.WIDTH
        maxHeight = Sizes.Pomodoro.Buttons.HEIGHT

        iconButton(Icon.STOP, Action.Clicked.Stop, Button.Small.SIZE)
        space(horizontal = 16.0)
        iconButton(controller.playPause, Action.Clicked.PlayPause, Button.Big.SIZE, big = true)
        space(horizontal = 16.0)
        iconButton(Icon.SKIP, Action.Clicked.Skip, Button.Small.SIZE)
    }

    private fun EventTarget.space(horizontal: Double? = null, vertical: Double? = null) = stackpane {
        if (horizontal != null) minWidth = horizontal * SCALING_FACTOR
        if (vertical != null) minHeight = vertical * SCALING_FACTOR
    }

    private fun EventTarget.iconButton(
        icon: Icon, action: Action.Clicked, size: Double, big: Boolean = false
    ) = stackpane {
        val svg = svgpath(icon.path) {
            style {
                scaleX = (if (big) 3 else 2) * SCALING_FACTOR
                scaleY = (if (big) 3 else 2) * SCALING_FACTOR
                fillProperty().bind(controller.backgroundColor)
            }
        }
        button(graphic = svg) {
            action { controller.setAction(action) }
            setMinSize(size, size)
            style {
                backgroundColor += Colors.white
                backgroundRadius = multi(box(all = size.px))
            }
        }
    }

    private fun EventTarget.iconButton(
        iconObservable: ObservableValue<Icon>, action: Action.Clicked, size: Double, big: Boolean = false
    ) = stackpane {
        val svg = svgpath(iconObservable.value.path) {
            iconObservable.addListener { _, _, icon -> content = icon.path }
            style {
                scaleX = (if (big) 3 else 2) * SCALING_FACTOR
                scaleY = (if (big) 3 else 2) * SCALING_FACTOR
                fillProperty().bind(controller.backgroundColor)
            }
        }
        button(graphic = svg) {
            action { controller.setAction(action) }
            setMinSize(size, size)
            style {
                backgroundColor += Colors.white
                backgroundRadius =
                    multi(box(all = size.px))
            }
        }
    }

    private fun EventTarget.doneView(rows: Int = 2, columns: Int = 2, inColumn: Int = 4) = gridpane {
        for (row in 1 .. rows) {
            row {
                space(horizontal = 16.0)
                for (numberInRow in 1 .. columns * inColumn) {
                    donePomodoro(number = numberInRow + (row - 1) * columns * inColumn)
                    space(horizontal = if (numberInRow % inColumn == 0) 20.0 else 4.0)
                }
            }
            row { space(vertical = 16.0) }
        }
    }

    private fun Pane.donePomodoro(number: Int) = circle(radius = 16 * SCALING_FACTOR) {
        fill = null
        stroke = Colors.white
        strokeWidth = 4 * SCALING_FACTOR
        controller.donePomodoroes.addListener { _, _, done -> fill = if (done >= number) Colors.white else null }
    }
}

enum class Icon(val path: String) {
    STOP("M6 6h12v12H6z"),
    SKIP("M6 18l8.5-6L6 6v12zM16 6v12h2V6h-2z"),
    PLAY("M8 5v14l11-7z"),
    PAUSE("M6 19h4V5H6v14zm8-14v14h4V5h-4z")
}

class PomodoroController : PomPlanController<State, Action>(
    elmController = Pomodoro(settingsRepository = SettingsRepositoryImpl()).controller
) {
    var backgroundColor = SimpleObjectProperty(Colors.white)
    var contentColor = SimpleObjectProperty(Colors.black)
    var timeText = SimpleStringProperty("")
    var angle = SimpleDoubleProperty(0.0)
    val playPause = SimpleObjectProperty(Icon.PLAY)
    val donePomodoroes = SimpleObjectProperty(0)

    override fun render(state: State) {
        println(state)
        val background = when (state.logicState) {
            State.LogicState.WAIT_FOR_WORK, State.LogicState.WORK -> Colors.red
            State.LogicState.WAIT_FOR_BREAK, State.LogicState.BREAK -> Colors.green
        }
        backgroundColor.set(background)

        timeText.set(state.remainTime.toString())
        val maxTime = when (state.logicState) {
            State.LogicState.WAIT_FOR_WORK, State.LogicState.WORK -> state.rules.workTime
            State.LogicState.WAIT_FOR_BREAK, State.LogicState.BREAK -> state.currentBreakTime
        }
        angle.set(state.remainTime.timestamp.toDouble() / maxTime.timestamp.toDouble() * 360.0)
        this.backgroundColor.set(background)
        this.contentColor.set(Colors.white)

        donePomodoroes.set(state.donePomodoroes)
        playPause.set(
            when (state.logicState) {
                State.LogicState.WAIT_FOR_WORK, State.LogicState.WAIT_FOR_BREAK -> Icon.PLAY
                State.LogicState.WORK, State.LogicState.BREAK -> Icon.PAUSE
            }
        )
    }
}
