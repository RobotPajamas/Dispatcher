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