package com.robotpajamas.dispatcher

enum class State {
    NONE, READY, EXECUTING, FINISHING, FINISHED, RESCHEDULED
}

interface Dispatchable : Runnable, Cancellable, Completable, Executable, Timeoutable, Retriable {
    val id: String
    var state: State

    override fun execute() {
        execution.invoke { result ->
            result.onFailure {
                when (retryPolicy) {
                    RetryPolicy.RETRY, RetryPolicy.RESCHEDULE ->
                        if (retries < maxRetries) retry() else complete(result)
                    RetryPolicy.NONE -> complete(result)
                }
            }
            result.onSuccess {
                complete(result)
            }
        }
    }
}
