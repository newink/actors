package com.github.dant3.actors.impl.mailbox

import com.github.dant3.actors.Actor
import com.github.dant3.actors.ActorRef
import com.github.dant3.actors.Mailbox
import com.github.dant3.actors.impl.Envelope
import kotlinx.coroutines.*

internal class MailboxActorRef<T: Any>(override val id: String, private val mailbox: Mailbox<T>, parentScope: CoroutineScope): ActorRef<T> {
    val actorScope: CoroutineScope = parentScope + SupervisorJob(parentScope.coroutineContext[Job.Key])

    fun start(actor: Actor<T>) {
        actorScope.launch {
            with (actor) {
                for (envelope in mailbox) {
                    envelope.receive()
                }
            }
        }
    }

    override fun tell(message: T, sender: ActorRef<Any>) {
        mailbox.send(Envelope(message, sender))
    }

    fun stop() {
        mailbox.close()
        actorScope.cancel()
    }
}