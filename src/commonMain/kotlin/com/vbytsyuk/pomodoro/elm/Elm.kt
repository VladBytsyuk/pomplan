package com.vbytsyuk.pomodoro.elm

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


interface Elm {

    interface State
    interface Action
    interface Effect

    interface EffectHandler<E : Effect, A : Action> {
        suspend fun handle(effect: E): A
    }

    interface Reducer<S : State, A : Action, E : Effect> {
        fun reduce(oldState: S, action: A): Pair<S, E?>
    }


    interface Controller<S : State, A : Action, E : Effect> {
        val effectHandler: EffectHandler<E, A>
        val reducer: Reducer<S, A, E>

        val currentState: S

        fun setAction(action: A)
        fun observeState(callback: suspend (S) -> Unit)

        fun attach()
        fun detach()
    }

    class ControllerImpl<S : State, A : Action, E : Effect>(
        initialState: S,
        initialAction: A,
        override val effectHandler: EffectHandler<E, A>,
        override val reducer: Reducer<S, A, E>
    ) : Controller<S, A, E> {

        private val actionFlow = MutableStateFlow(initialAction)
        private val stateFlow = MutableStateFlow(initialState)

        override val currentState: S get() = stateFlow.value

        private val supervisorJob = SupervisorJob()

        private fun launchCoroutine(
            dispatcher: CoroutineDispatcher,
            block: suspend () -> Unit
        ) = CoroutineScope(dispatcher + supervisorJob).launch { block() }

        private fun stopAllCoroutines() = supervisorJob.cancel()


        override fun setAction(action: A) {
            actionFlow.value = action
        }

        override fun observeState(callback: suspend (S) -> Unit) {
            launchCoroutine(Dispatchers.Default) {
                stateFlow.collect { callback(it) }
            }
        }


        override fun attach() {
            launchCoroutine(Dispatchers.Default) {
                actionFlow.collect { onActionReceived(it) }
            }
        }

        override fun detach() {
            stopAllCoroutines()
        }


        private suspend fun onActionReceived(action: A) {
            val oldState = stateFlow.value
            val (newState, effect) = reducer.reduce(oldState, action)
            if (newState != oldState) stateFlow.value = newState
            if (effect != null) {
                val newAction = effectHandler.handle(effect)
                actionFlow.value = newAction
            }
        }
    }
}
