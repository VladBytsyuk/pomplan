package com.vbytsyuk.pomodoro.jvm

import tornadofx.*


class AppView : UIComponent() {
    override val root = borderpane {
        center = vbox {
            label("Time")
        }
    }
}
