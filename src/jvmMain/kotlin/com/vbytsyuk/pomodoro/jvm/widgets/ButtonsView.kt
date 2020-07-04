package com.vbytsyuk.pomodoro.jvm.widgets

import javafx.scene.image.Image
import tornadofx.*

class ButtonsView : UIComponent() {
    private val controller: ButtonsController by inject()

    override val root = hbox {
        imageview(image = Image("ic_stop.svg"))
        imageview(image = Image("ic_play.svg"))
        imageview(image = Image("ic_skip.svg"))
    }
}

class ButtonsController : Controller() {

}
