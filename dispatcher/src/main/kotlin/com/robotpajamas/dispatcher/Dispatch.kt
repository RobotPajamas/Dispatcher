package com.robotpajamas.dispatcher

import java.util.concurrent.CancellationException
import java.util.concurrent.TimeoutException

//private enum class State {
//    NONE, READY, EXECUTING, FINISHING, FINISHED
//}

class Dispatch<T>(override val id: String,
                  override val timeout: Int,
                  override val execution: ExecutionBlock<T>,
                  completion: CompletionBlock<T>? = null) : Dispatchable {
    override val completions = mutableListOf<CompletionBlock<*>>()

    init {
        assert(timeout >= 0, { "QueueItem timeout must be >= 0" })
        completion?.let { completions.add(it as CompletionBlock<*>) }
    }

    private var isCancelled = false
//    private var state = State.READY

    override fun run() {
        if (isCancelled) {
            complete(Result.Failure<T>(CancellationException("$id: was cancelled before starting execution")))
            return
        }
//        state = State.EXECUTING
        execute()
    }

    override fun cancel() {
        isCancelled = true
    }

    override fun timedOut() {
        complete(Result.Failure<T>(TimeoutException("$id timed out after $timeout seconds")))
    }

}
