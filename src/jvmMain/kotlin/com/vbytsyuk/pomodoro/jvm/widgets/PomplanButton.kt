package com.vbytsyuk.pomodoro.jvm.widgets

import com.vbytsyuk.pomodoro.jvm.extensions.px
import javafx.beans.value.ObservableValue
import javafx.css.Styleable
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

private fun Styleable.ppButtonStyle(color: Color) = style(append = true) {
    fontWeight = FontWeight.BOLD
    backgroundColor += color
    textFill = Colors.white
    fontSize = 36.px
    padding = box(all = 24.px)
}

fun EventTarget.ppButton(
    text: String = "",
    color: Color = Colors.green,
    graphic: Node? = null,
    op: Button.() -> Unit = {}
): Button {
    val styledOp: Button.() -> Unit = {
        op()
        ppButtonStyle(color)
    }
    return Button(text).attachTo(this, styledOp) {
        if (graphic != null) it.graphic = graphic
    }
}


fun EventTarget.ppButton(
    text: ObservableValue<String>,
    color: Color = Colors.green,
    graphic: Node? = null,
    op: Button.() -> Unit = {}
): Button {
    val styledOp: Button.() -> Unit = {
        op()
        ppButtonStyle(color)
    }
    return Button().attachTo(this, styledOp) {
        it.textProperty().bind(text)
        if (graphic != null) it.graphic = graphic
    }
}
