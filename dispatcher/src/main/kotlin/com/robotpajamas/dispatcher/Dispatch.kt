package com.robotpajamas.dispatcher

import java.util.concurrent.CancellationException
import java.util.concurrent.TimeoutException

class Dispatch<T>(override val id: String,
                  override val timeout: Int = 1,
                  override val execution: ExecutionBlock<T>,
                  completion: CompletionBlock<T>? = null) : Dispatchable {

    override val completions = mutableListOf<CompletionBlock<*>>()
    override var isCancelled = false
    internal var state = State.READY

    init {
        assert(timeout >= 0, { "QueueItem timeout must be >= 0" })
        completion?.let { completions.add(it as CompletionBlock<*>) }
    }

    override fun run() {
        if (isCancelled) {
            complete(Result.Failure<T>(CancellationException("$id: was cancelled before starting execution")))
            return
        }
//        state = State.EXECUTING
        execute()
    }

    override fun timedOut() {
        complete(Result.Failure<T>(TimeoutException("$id timed out after $timeout seconds")))
    }
}
