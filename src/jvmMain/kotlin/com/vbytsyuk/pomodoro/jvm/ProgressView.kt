package com.vbytsyuk.pomodoro.jvm

import com.vbytsyuk.pomodoro.core.domain.PomodoroTime
import com.vbytsyuk.pomodoro.jvm.Sizes.Pomodoro.Progress.SIZE
import com.vbytsyuk.pomodoro.jvm.extensions.margin
import com.vbytsyuk.pomodoro.jvm.widgets.Colors
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.text.FontWeight
import tornadofx.*


class ProgressView : UIComponent() {
    private val controller: ProgressController by inject()

    override val root = stackpane {
        group {
            stackpaneConstraints {
                margin = margin(all = 52.0 * 2)
                alignment = Pos.CENTER
            }
            rectangle(x = 0.0, y = 0.0, width = SIZE, height = SIZE) {
                fillProperty().bind(controller.backgroundColor)
            }
            arc(centerX = SIZE / 2, centerY = SIZE / 2, radiusX = SIZE / 2, radiusY = SIZE / 2, startAngle = 90.0) {
                lengthProperty().bind(controller.angle)
                type = ArcType.ROUND
                fill = Colors.white
            }
            circle(centerX = SIZE / 2, centerY = SIZE / 2, radius = SIZE / 2 - 16) {
                fillProperty().bind(controller.backgroundColor)
            }
        }
        label(controller.timeText) {
            style {
                fontSize = (64 * 2).px
                fontWeight = FontWeight.BOLD
                textFillProperty().bind(controller.contentColor)
            }
        }
    }
}

class ProgressController : Controller() {
    var backgroundColor = SimpleObjectProperty(Colors.white)
    var contentColor = SimpleObjectProperty(Colors.black)
    var timeText = SimpleStringProperty("")
    var angle = SimpleDoubleProperty(0.0)

    fun render(
        time: PomodoroTime,
        maxTime: PomodoroTime,
        backgroundColor: Color,
        contentColor: Color
    ) {
        timeText.set(time.toString())
        angle.set(time.timestamp.toDouble() / maxTime.timestamp.toDouble() * 360.0)
        this.backgroundColor.set(backgroundColor)
        this.contentColor.set(contentColor)
    }
}
