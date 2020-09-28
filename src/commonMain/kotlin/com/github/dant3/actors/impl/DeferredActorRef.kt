package com.github.dant3.actors.impl

import com.github.dant3.actors.ActorRef
import kotlinx.coroutines.CompletableDeferred

internal class DeferredActorRef(override val id: String): ActorRef<Any> {
    val result: CompletableDeferred<Any> = CompletableDeferred()

    override fun tell(message: Any, sender: ActorRef<Any>) {
        result.complete(message)
    }
}