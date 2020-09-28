package com.github.dant3.actors

interface ActorContext<T: Any>: ActorManager {
    val self: ActorRef<T>
}