package com.vbytsyuk.pomodoro.jvm

import com.vbytsyuk.pomodoro.core.repositories.SettingsRepositoryImpl
import com.vbytsyuk.pomodoro.core.screens.Pomodoro
import com.vbytsyuk.pomodoro.core.screens.Pomodoro.*
import com.vbytsyuk.pomodoro.jvm.widgets.Sizes.Design.Button
import com.vbytsyuk.pomodoro.jvm.widgets.Sizes.SCALING_FACTOR
import com.vbytsyuk.pomodoro.jvm.extensions.px
import com.vbytsyuk.pomodoro.jvm.widgets.Colors
import com.vbytsyuk.pomodoro.jvm.widgets.DarkTheme
import com.vbytsyuk.pomodoro.jvm.widgets.Sizes
import com.vbytsyuk.pomodoro.jvm.widgets.Sizes.Pomodoro.Progress
import com.vbytsyuk.pomodoro.jvm.widgets.Theme
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
    private val theme: Theme = DarkTheme

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
        style { backgroundColor += theme.colors.background }
    }

    private fun EventTarget.progressView() = stackpane {
        group {
            rectangle(x = 0.0, y = 0.0, width = Progress.SIZE, height = Progress.SIZE) {
                fill = theme.colors.background
            }
            arc(
                centerX = Progress.SIZE / 2, centerY = Progress.SIZE / 2,
                radiusX = Progress.SIZE / 2, radiusY = Progress.SIZE / 2,
                startAngle = 90.0
            ) {
                lengthProperty().bind(controller.angle)
                type = javafx.scene.shape.ArcType.ROUND
                fill = theme.colors.accent
                fillProperty().bind(controller.backgroundColor)
            }
            circle(
                centerX = Progress.SIZE / 2, centerY = Progress.SIZE / 2,
                radius = Progress.SIZE / 2 - 8
            ) {
                fill = theme.colors.background
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
        space(horizontal = 16.0 * 4)
        iconButton(controller.playPause, Action.Clicked.PlayPause, Button.Big.SIZE, big = true)
        space(horizontal = 16.0 * 4)
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
                fill = theme.colors.content
            }
        }
        button(graphic = svg) {
            action { controller.setAction(action) }
            style {
                backgroundColor += theme.colors.background
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
                fill = theme.colors.content
            }
        }
        button(graphic = svg) {
            style {
                backgroundColor += theme.colors.background
            }
            action { controller.setAction(action) }
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
        stroke = theme.colors.textPrimary
        strokeWidth = 2 * SCALING_FACTOR
        controller.donePomodoroes.addListener { _, _, done ->
            fill = if (done >= number) theme.colors.accent else null
            stroke = if (done >= number) theme.colors.accent else theme.colors.textPrimary
        }
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
    private val theme: Theme = DarkTheme

    var backgroundColor = SimpleObjectProperty(theme.colors.background)
    var contentColor = SimpleObjectProperty(theme.colors.content)
    var timeText = SimpleStringProperty("")
    var angle = SimpleDoubleProperty(0.0)
    val playPause = SimpleObjectProperty(Icon.PLAY)
    val donePomodoroes = SimpleObjectProperty(0)

    override fun render(state: State) {
        val background = when (state.logicState) {
            State.LogicState.WAIT_FOR_WORK, State.LogicState.WORK -> theme.colors.red
            State.LogicState.WAIT_FOR_BREAK, State.LogicState.BREAK -> theme.colors.grey
        }
        backgroundColor.set(background)

        timeText.set(state.remainTime.toString())
        val maxTime = when (state.logicState) {
            State.LogicState.WAIT_FOR_WORK, State.LogicState.WORK -> state.rules.workTime
            State.LogicState.WAIT_FOR_BREAK, State.LogicState.BREAK -> state.currentBreakTime
        }
        angle.set(state.remainTime.timestamp.toDouble() / maxTime.timestamp.toDouble() * 360.0)
        this.backgroundColor.set(background)
        this.contentColor.set(theme.colors.content)

        donePomodoroes.set(state.donePomodoroes)
        val playPauseIcon = when (state.logicState) {
            State.LogicState.WAIT_FOR_WORK, State.LogicState.WAIT_FOR_BREAK -> Icon.PLAY
            State.LogicState.WORK, State.LogicState.BREAK -> Icon.PAUSE
        }
        playPause.set(playPauseIcon)
    }
}
