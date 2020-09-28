package com.github.dant3.actors

import com.github.dant3.actors.impl.Envelope
import kotlinx.coroutines.CoroutineScope

interface Mailbox<T> {
    val scope: CoroutineScope

    fun send(envelope: Envelope<T>)
    fun close()

    suspend operator fun iterator(): MailboxIterator<T>
}