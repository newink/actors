package com.github.dant3.actors

import com.github.dant3.actors.impl.EmptyActorRef

interface ActorRef<in T: Any> {
    val id: String
    fun tell(message: T, sender: ActorRef<Any>)
    fun tell(message: T) = tell(message, noSender())

    companion object {
        fun noSender(): ActorRef<Any> = EmptyActorRef
    }
}