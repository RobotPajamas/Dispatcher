package com.robotpajamas.dispatcher

import android.os.Handler
import android.os.Looper
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor

class SerialDispatcher(
        private val executionHandler: Handler = Handler(Looper.getMainLooper()),
        private val dispatchHandler: Handler = Handler(Looper.getMainLooper())) : Dispatcher {

    private var active: Dispatchable? = null
    private val queue: Queue<Dispatchable> = ConcurrentLinkedQueue()
    private val executor = Executor { command -> executionHandler.post(command) }

    @Synchronized
    override fun clear() {
        // TODO: What to do with in-flight calls? Time out? Cancel?
        dispatchHandler.removeCallbacksAndMessages(null)
        active = null
        queue.clear()
    }

    @Synchronized
    override fun count(): Int {
        return queue.size
    }

    // TODO: Should this return the dispatch item's callback object?
    @Synchronized
    override fun enqueue(item: Dispatchable) {
        item.completions.add { dispatchNext() }
        queue.add(item)
        if (active == null) {
            dispatchNext()
        }
    }

    @Synchronized
    private fun dispatchNext() {
        // Remove pending timeout runnables
        // TODO: this poses a potential problem when using a handler with the same looper to enqueue items
        // TODO: as it will clear any posted callbacks and messages, thus clearing the queue
        dispatchHandler.removeCallbacksAndMessages(null)

        active = queue.poll()
        active?.let {
            // Start timeout clock
            val cancel = Runnable {
                // TODO: Check if this is doing what I think it is
                // Capture this current queueItem, and compare it against the active item in X seconds
                if (it.id != active?.id) {
                    return@Runnable
                }
                active?.timedOut()
            }

            // Set retry action as per item's retry policy
            if (it.retryPolicy == RetryPolicy.RESCHEDULE) {
                it.retry = {
                    active = null
                    enqueue(it)
                }
            } else if (it.retryPolicy == RetryPolicy.RETRY) {
                it.retry = { it.execute() }
            }

            dispatchHandler.postDelayed(cancel, it.timeout * 1000L)
            executor.execute(it)
        }
    }

//    @Synchronized
//    internal fun <T> dispatched(name: String): QueueItem<T>? {
//        @Suppress("UNCHECKED_CAST")
//        return if (name == active?.name) active as? QueueItem<T> else null
//    }
}