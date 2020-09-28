package com.github.dant3.actors.impl.mailbox

import com.github.dant3.actors.Mailbox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel

interface MailboxFactory {
    fun <T> createMailbox(): Mailbox<T>

    companion object {
        operator fun invoke(deliveryScope: CoroutineScope): MailboxFactory = object: MailboxFactory {
            override fun <T> createMailbox(): Mailbox<T> = MailboxImpl(Channel(Channel.BUFFERED), deliveryScope)
        }
    }
}