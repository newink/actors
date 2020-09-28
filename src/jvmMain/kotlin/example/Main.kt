package example

import com.github.dant3.actors.*
import com.github.dant3.actors.impl.Envelope
import kotlinx.coroutines.*

object Main {
    private fun ActorContext<Any>.echoActor() = Actor {
        println("echo: Received $message")
        sender.tell(message, self)
    }

    data class GreetMe(val name: String)

    private fun ActorContext<Any>.helloActor(): Actor<Any> {
        val echoActor = startActor<Any>("echo") { echoActor() }
        return Actor {
            when (message) {
                is GreetMe -> sender.tell("Hello, ${message.name}!", self)
                else -> echoActor.tell(message, sender)
            }
        }
    }

    private class MultiplyActor(actorContext: ActorContext<Long>): Actor<Long>(actorContext) {
        private var lastNumber: Long = 1

        override suspend fun Envelope<Long>.receive() {
            println("message = $message, lastNumber = $lastNumber")
            val previousNumber = lastNumber
            lastNumber = message
            sender.tell(message * previousNumber)
        }
    }

    private suspend fun CoroutineScope.delayJob(name: String) {
        println("$name running")
        while (isActive) {
            delay(100)
            println("$name after delay")
        }
        println("$name cancelled finally!")
    }

    @JvmStatic fun main(args: Array<String>) {
        System.setProperty("kotlinx.coroutines.stacktrace.recovery", "true")
        val system = ActorSystem()
        val helloActorRef = system.startActor<Any> { helloActor() }

        val response = helloActorRef.ask(GreetMe("World"))
        runBlocking {
            println("Response: ${response.await()}")
        }

        val response2 = system.ask(helloActorRef, "Echo")
        runBlocking {
            println("Response 2: ${response2.await()}")
        }

        system.stopActor(helloActorRef)
        val response3 = helloActorRef.ask(GreetMe("World"))

        runBlocking {
            delay(400)
            if (response3.isCompleted) {
                println("What a surprise: Response 3 completed: ${response3.getCompleted()}")
            }
        }

        val incrementActor = system.startActor(Main::MultiplyActor)

        runBlocking {
            val outcome = (2L until 30L).reduce { acc, i ->
                incrementActor.askAndAwait<Long, Long>(acc).also {
                    println("Sent $acc, received: $it")
                }
            }
            println("Total outcome: $outcome")
        }

        system.destroy()
    }

    private fun scopesPlayground() {
        runBlocking {
            val dispatcher = Dispatchers.Default
            val rootJob = SupervisorJob()
            val rootScope = GlobalScope + rootJob + dispatcher

            val rootJobScope = rootScope + SupervisorJob(rootScope.coroutineContext[Job.Key])

            val rootScopeJob = rootJobScope.launch {
                delayJob("RootJob")
            }

            val childJobScope = rootJobScope + SupervisorJob(rootJobScope.coroutineContext[Job.Key])
            val childJob = childJobScope.launch {
                delayJob("ChildJob")
            }

            delay(200)
            rootJobScope.cancel()
            println("RootJob cancelled")
            delay(200)
            childJobScope.cancel()
            println("ChildJob cancelled")
        }
    }
}