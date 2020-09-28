package com.github.dant3.actors.impl.mailbox

import com.github.dant3.actors.Mailbox
import com.github.dant3.actors.MailboxIterator
import com.github.dant3.actors.impl.Envelope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.launch

internal class MailboxImpl<T>(private val channel: Channel<Envelope<T>>, override val scope: CoroutineScope): Mailbox<T> {
    override fun send(envelope: Envelope<T>) {
        if (!channel.isClosedForSend) {
            scope.launch {
                channel.send(envelope)
            }
        }
    }

    override fun close() {
        channel.close()
    }

    override suspend fun iterator(): MailboxIterator<T> =
        MailboxIteratorImpl(channel.iterator())

    private class MailboxIteratorImpl<T>(private val channelIterator: ChannelIterator<Envelope<T>>): MailboxIterator<T> {
        override suspend fun hasNext(): Boolean = channelIterator.hasNext()
        override fun next(): Envelope<T> = channelIterator.next()
    }
}