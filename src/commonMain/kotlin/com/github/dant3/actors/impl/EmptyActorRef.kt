package com.github.dant3.actors.impl

import com.github.dant3.actors.ActorRef

internal object EmptyActorRef: ActorRef<Any> {
    override val id: String = ""
    override fun tell(message: Any, sender: ActorRef<Any>) {}
}