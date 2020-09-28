package com.github.dant3.actors

import com.github.dant3.actors.impl.ActorSystemImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers

interface ActorSystem: ActorManager {
    fun <T: Any> tell(actorRef: ActorRef<T>, message: T)
    fun <T: Any> ask(actorRef: ActorRef<T>, message: T): Deferred<Any>

    fun <T: Any, U: Any> startActor(parent: ActorRef<U>, actorBuilder: ActorBuilder<T>): ActorRef<T>
    fun <T: Any, U: Any> startActor(parent: ActorRef<U>, id: String, actorBuilder: ActorBuilder<T>): ActorRef<T>

    fun destroy()

    companion object {
        operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.Default): ActorSystem =
            ActorSystemImpl(dispatcher)
    }
}