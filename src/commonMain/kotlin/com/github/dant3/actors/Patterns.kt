package com.github.dant3.actors

import com.github.dant3.actors.impl.DeferredActorRef
import kotlinx.coroutines.Deferred

fun <T : Any> ActorRef<T>.ask(message: T): Deferred<Any> {
    val deferredActorRef = DeferredActorRef("")
    tell(message, deferredActorRef)
    return deferredActorRef.result
}

suspend fun <T : Any, U: Any> ActorRef<T>.askAndAwait(message: T): U {
    return ask(message).await() as U
}