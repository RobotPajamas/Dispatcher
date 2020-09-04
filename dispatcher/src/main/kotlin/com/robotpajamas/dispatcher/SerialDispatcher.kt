package com.robotpajamas.dispatcher

import android.os.Handler
import android.os.Looper
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor

class SerialDispatcher(
    private val executionHandler: Handler = Handler(Looper.getMainLooper()),
    private val dispatchHandler: Handler = Handler(Looper.getMainLooper())
) : Dispatcher {

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
                    // TODO: Is this enqueue adding more and more "dispatchNext" calls?
                    enqueue(it)
                }
            } else if (it.retryPolicy == RetryPolicy.RETRY) {
                it.retry = {
                    //                    it.execute()
                    executor.execute(it)
                    // TODO: How to retry within the same context as the rest of the app?
                    // TODO: e.g. enqueue, but at the front of the queue - so the handlers all run?
                    dispatchHandler.postDelayed(cancel, it.timeout * 1000L)
                }
            }

            dispatchHandler.postDelayed(cancel, it.timeout * 1000L)
            executor.execute(it)
        }
    }

    @Synchronized
    fun <T> dispatched(id: String): Dispatch<T>? {
        @Suppress("UNCHECKED_CAST")
        return if (id == active?.id) active as? Dispatch<T> else null
    }
}
