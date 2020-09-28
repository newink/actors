package com.github.dant3.actors.impl

import com.github.dant3.actors.ActorRef

data class Envelope<out T>(val message: T, val sender: ActorRef<Any>)