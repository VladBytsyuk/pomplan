package com.vbytsyuk.pomodoro.jvm.extensions

import javafx.geometry.Insets
import tornadofx.*


val Double.px get() = Dimension(this, Dimension.LinearUnits.px)
val Int.px get() = this.toDouble().px

val Double.mm get() = Dimension(this, Dimension.LinearUnits.mm)
val Int.mm get() = this.toDouble().mm

val Double.cm get() = Dimension(this, Dimension.LinearUnits.cm)
val Int.cm get() = this.toDouble().cm

val Double.inches get() = Dimension(this, Dimension.LinearUnits.inches)
val Int.inches get() = this.toDouble().inches

val Double.pt get() = Dimension(this, Dimension.LinearUnits.pt)
val Int.pt get() = this.toDouble().pt

val Double.pc get() = Dimension(this, Dimension.LinearUnits.pc)
val Int.pc get() = this.toDouble().pc

val Double.em get() = Dimension(this, Dimension.LinearUnits.em)
val Int.em get() = this.toDouble().em

val Double.ex get() = Dimension(this, Dimension.LinearUnits.ex)
val Int.ex get() = this.toDouble().ex

val Double.percent get() = Dimension(this, Dimension.LinearUnits.percent)
val Int.percent get() = this.toDouble().percent


fun margin(all: Double) =
    margin(vertical = all, horizontal = all)
fun margin(vertical: Double = 0.0, horizontal: Double = 0.0) =
    margin(top = vertical, bottom = vertical, left = horizontal, right = horizontal)
fun margin(
    top: Double = 0.0,
    right: Double = 0.0,
    bottom: Double = 0.0,
    left: Double = 0.0
) = Insets(top, right, bottom, left)
