package com.github.dant3.actors.impl.context

import com.github.dant3.actors.ActorContext
import com.github.dant3.actors.ActorRef
import com.github.dant3.actors.ActorSystem

interface ActorContextFactory {
    fun <T: Any> getContext(actorRef: ActorRef<T>): ActorContext<T>

    companion object {
        operator fun invoke(actorSystem: ActorSystem): ActorContextFactory = object:
            ActorContextFactory {
            override fun <T: Any> getContext(actorRef: ActorRef<T>): ActorContext<T> =
                ActorContextImpl(actorSystem, actorRef)
        }
    }
}