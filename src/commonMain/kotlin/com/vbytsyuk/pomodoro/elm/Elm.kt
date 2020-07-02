package com.vbytsyuk.pomodoro.elm

import kotlinx.coroutines.*


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
        fun subscribeOnState(subscriber: Any, callback: suspend (S) -> Unit)
        fun unsubscribeFromState(subscriber: Any)

        fun attach()
        fun detach()
    }

    class ControllerImpl<S : State, A : Action, E : Effect>(
        initialState: S,
        initialAction: A,
        override val effectHandler: EffectHandler<E, A>,
        override val reducer: Reducer<S, A, E>
    ) : Controller<S, A, E> {

        private val action = Observable(initialAction)
        private val state = Observable(initialState)

        override val currentState: S get() = state.get()

        override fun setAction(action: A) {
            this.action.set(action)
        }

        override fun subscribeOnState(subscriber: Any, callback: suspend (S) -> Unit) {
            state.subscribe(subscriber) { callback(it) }
        }

        override fun unsubscribeFromState(subscriber: Any) {
            state.unsubscribe(subscriber)
        }

        override fun attach() {
            action.subscribe(this) { onActionReceived(it) }
        }

        override fun detach() {
            action.unsubscribe(this)
        }


        private suspend fun onActionReceived(action: A) {
            val oldState = state.get()
            val (newState, effect) = reducer.reduce(oldState, action)
            if (newState != oldState) state.set(newState)
            if (effect != null) {
                val newAction = effectHandler.handle(effect)
                this.action.set(newAction)
            }
        }
    }

    private class Observable<T>(initialValue: T) {
        private fun launchCoroutine(job: Job, block: suspend () -> Unit) =
            CoroutineScope(Dispatchers.Default + job).launch { block() }


        private val subscribers = mutableMapOf<Any, Pair<Job, suspend (T) -> Unit>>()
        fun subscribe(subscriber: Any, callback: suspend (T) -> Unit) {
            val job = SupervisorJob()
            subscribers[subscriber] = job to callback
            launchCoroutine(job) { callback(value) }
        }
        fun unsubscribe(subscriber: Any) {
            subscribers[subscriber]?.first?.cancel()
            subscribers.remove(subscriber)
        }

        private var value: T = initialValue
        fun set(newValue: T) {
            value = newValue
            subscribers.values.forEach { (job, callback) -> launchCoroutine(job) { callback(newValue) } }
        }
        fun get(): T = value

    }
}
