package com.github.dant3.actors.impl

import com.github.dant3.actors.*
import com.github.dant3.actors.impl.context.ActorContextFactory
import com.github.dant3.actors.impl.mailbox.MailboxActorRef
import com.github.dant3.actors.impl.mailbox.MailboxFactory
import com.github.dant3.actors.util.UUID
import kotlinx.coroutines.*

internal class ActorSystemImpl(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) : ActorSystem {
    private val pool = ActorsRegistry()
    private val deadLetters = EmptyActorRef

    private val rootJob = SupervisorJob()
    private val rootActorScope = GlobalScope + rootJob + dispatcher

    private val mailboxFactory = MailboxFactory(rootActorScope)
    private val actorContextFactory = ActorContextFactory(this)

    override fun <T : Any> startActor(actorBuilder: ActorBuilder<T>): ActorRef<T> {
        return startActor(uniqueId(), actorBuilder)
    }

    override fun <T : Any> startActor(id: String, actorBuilder: ActorBuilder<T>): ActorRef<T> {
        val ref = createAndStartActor("/" + id, rootActorScope, actorBuilder)
        pool.register(ref)
        return ref
    }

    override fun findActor(id: String): ActorRef<Any>? = pool.get(id)
    override fun getActor(id: String): ActorRef<Any> = findActor(id) ?: deadLetters

    override fun <T: Any> tell(actorRef: ActorRef<T>, message: T)  {
        rootActorScope.launch {
            actorRef.tell(message, ActorRef.noSender())
        }
    }

    override fun <T: Any> ask(actorRef: ActorRef<T>, message: T): Deferred<Any> {
        return actorRef.ask(message)
    }

    override fun <T: Any, U: Any> startActor(parent: ActorRef<U>, actorBuilder: ActorBuilder<T>): ActorRef<T> {
        return startActor(parent, uniqueId(), actorBuilder)
    }

    override fun <T: Any, U: Any> startActor(parent: ActorRef<U>, id: String, actorBuilder: ActorBuilder<T>): ActorRef<T> {
        if (parent is MailboxActorRef<U>) {
            val ref = createAndStartActor(parent.id + "/" + id, parent.actorScope, actorBuilder)
            pool.register(ref)
            return ref
        } else {
            throw IllegalArgumentException("Illegal parent actor")
        }
    }

    private fun <T: Any> createAndStartActor(absoluteId: String, scope: CoroutineScope, actorBuilder: ActorBuilder<T>): ActorRef<T> {
        val mailbox = mailboxFactory.createMailbox<T>()
        val ref = MailboxActorRef(absoluteId, mailbox, scope)
        val actorContext = actorContextFactory.getContext(ref)
        val actor = actorBuilder(actorContext)
        ref.start(actor)
        return ref
    }

    override fun stopActor(actor: ActorRef<Any>) {
        if (actor is MailboxActorRef) {
            actor.stop()
            pool.deregister(actor)
        }
    }

    override fun destroy() {
        rootActorScope.cancel()
        pool.clear()
    }

    private fun uniqueId(): String = UUID.generate()
}
