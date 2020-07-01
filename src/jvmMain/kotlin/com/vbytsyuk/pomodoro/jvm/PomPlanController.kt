package com.vbytsyuk.pomodoro.jvm

import com.vbytsyuk.pomodoro.elm.Elm
import com.vbytsyuk.pomodoro.jvm.extensions.doOnUI
import tornadofx.*
import java.util.*


abstract class PomPlanController<S : Elm.State, A : Elm.Action>(
    private val elmController: Elm.Controller<S, A, *>
) : Controller() {

    private val subscriber = UUID.randomUUID().toString()

    init {
        elmController.attach()
        elmController.subscribeOnState(subscriber) { state -> doOnUI { render(state) } }
    }



    abstract fun render(state : S)
    fun setAction(action: A) = elmController.setAction(action)
}
