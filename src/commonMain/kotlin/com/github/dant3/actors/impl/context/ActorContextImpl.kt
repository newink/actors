package com.github.dant3.actors.impl.context

import com.github.dant3.actors.*

internal class ActorContextImpl<T: Any>(private val actorSystem: ActorSystem,
                                        override val self: ActorRef<T>) : ActorContext<T> {
    override fun findActor(id: String): ActorRef<Any>? = actorSystem.findActor(id)
    override fun getActor(id: String): ActorRef<Any> = actorSystem.getActor(id)

    override fun <U: Any> startActor(actorBuilder: ActorBuilder<U>): ActorRef<U> = actorSystem.startActor(self, actorBuilder)
    override fun <U: Any> startActor(id: String, actorBuilder: ActorBuilder<U>): ActorRef<U> = actorSystem.startActor(self, id, actorBuilder)

    override fun stopActor(actor: ActorRef<Any>) {
        actorSystem.stopActor(actor)
    }
}