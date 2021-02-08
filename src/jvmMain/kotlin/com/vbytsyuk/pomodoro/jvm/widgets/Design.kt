package com.vbytsyuk.pomodoro.jvm.widgets

import javafx.scene.paint.Color
import tornadofx.*


interface Theme {
    val colors: Colors
}


object LightTheme : Theme {
    override val colors = LightColors
}

object DarkTheme : Theme {
    override val colors = DarkColors
}


interface Colors {
    val white: Color
    val black: Color
    val grey: Color
    val blue: Color
    val greyBlue: Color
    val red: Color
    val yellow: Color
    val green: Color
    val cyan: Color
    val violet: Color

    val background: Color
    val content: Color
    val textPrimary: Color
    val textSecondary: Color
    val accent: Color

    val transparent: Color
}

object LightColors : Colors {
    override val white = c("#F5F6FA")
    override val black = c("#353B48")
    override val grey = c("#7F8FA6")
    override val blue = c("#273C75")
    override val greyBlue = c("#487EB0")
    override val red = c("#E84118")
    override val yellow = c("#FBC531")
    override val green = c("#4CD137")
    override val cyan = c("#00A8FF")
    override val violet = c("#9C88FF")

    override val background = white
    override val content = black
    override val textPrimary = black
    override val textSecondary = black
    override val accent = red

    override val transparent = Color.TRANSPARENT
}

object DarkColors : Colors {
    override val white = c("#DCDDE1")
    override val black = c("#2F3640")
    override val grey = c("#718093")
    override val blue = c("#192A56")
    override val greyBlue = c("#40739E")
    override val red = c("#C23616")
    override val yellow = c("#E1B12C")
    override val green = c("#44BD32")
    override val cyan = c("#0097E6")
    override val violet = c("#8C7AE6")

    override val background = black
    override val content = white
    override val textPrimary = white
    override val textSecondary = white
    override val accent = red

    override val transparent = Color.TRANSPARENT
}

