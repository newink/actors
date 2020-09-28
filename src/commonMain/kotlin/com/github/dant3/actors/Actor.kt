package com.github.dant3.actors

import com.github.dant3.actors.impl.Envelope

abstract class Actor<T: Any>(val context: ActorContext<T>) {
    abstract suspend fun Envelope<T>.receive()
}

@Suppress("FunctionName")
fun <T: Any> ActorContext<T>.Actor(receive: suspend Envelope<T>.() -> Unit): Actor<T> {
    return object: Actor<T>(this@Actor) {
        override suspend fun Envelope<T>.receive() {
            receive.invoke(this)
        }
    }
}