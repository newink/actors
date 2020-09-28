package com.github.dant3.actors

import com.github.dant3.actors.impl.Envelope

interface MailboxIterator<T> {
    suspend operator fun hasNext(): Boolean
    operator fun next(): Envelope<T>
}