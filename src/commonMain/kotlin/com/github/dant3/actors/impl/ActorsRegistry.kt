package com.github.dant3.actors.impl

import com.github.dant3.actors.ActorRef

internal class ActorsRegistry {
    private val storage: MutableMap<String, ActorRef<*>> = mutableMapOf()

    fun <T: Any> register(actorRef: ActorRef<T>) {
        storage[actorRef.id] = actorRef
    }

    fun get(id: String): ActorRef<Any>? {
        return storage[id] as ActorRef<Any>?
    }

    fun deregister(ref: ActorRef<Any>) {
        storage.remove(ref.id)
    }

    fun clear() {
        storage.clear()
    }
}