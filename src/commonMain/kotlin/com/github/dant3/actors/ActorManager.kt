package com.github.dant3.actors

typealias ActorBuilder<T> = ActorContext<T>.() -> Actor<T>

interface ActorManager {
    fun <T: Any> startActor(actorBuilder: ActorBuilder<T>): ActorRef<T>
    fun <T: Any> startActor(id: String, actorBuilder: ActorBuilder<T>): ActorRef<T>

    fun getActor(id: String): ActorRef<Any>
    fun findActor(id: String): ActorRef<Any>?

    fun stopActor(actor: ActorRef<Any>)
}