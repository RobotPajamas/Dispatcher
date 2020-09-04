package com.robotpajamas.dispatcher

import java.util.concurrent.CancellationException
import java.util.concurrent.TimeoutException

class Dispatch<T>(
    override val id: String,
    override val timeout: Int = 1,
    override val retryPolicy: RetryPolicy = RetryPolicy.NONE,
    override val maxRetries: Int = 2,
    override val execution: ExecutionBlock<T>,
    completion: CompletionBlock<T>? = null
) : Dispatchable {

    override val completions = mutableListOf<CompletionBlock<*>>()
    override var isCancelled = false
    override var state = State.READY
    override var retries = 0
    override var retry: () -> Unit = {}

    init {
        assert(timeout >= 0) { "QueueItem timeout must be >= 0" }
        completion?.let { completions.add(it as CompletionBlock<*>) }
        state = State.READY
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
