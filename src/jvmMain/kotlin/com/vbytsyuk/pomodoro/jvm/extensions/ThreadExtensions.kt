package com.vbytsyuk.pomodoro.jvm.extensions

import javafx.application.Platform


fun doOnUI(block: () -> Unit) = Platform.runLater(block)
