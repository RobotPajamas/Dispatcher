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
                    RetryPolicy.RETRY -> if (retries < maxRetries) {
                        retry()
                        execute()
                    } else {
                        complete(result)
                    }
                    RetryPolicy.RESCHEDULE -> if (retries < maxRetries) {
                        retry()
                        state = State.RESCHEDULED
                    } else {
                        state = State.READY //any state, but must not be RESCHEDULED
                        complete(result)
                    }
                    RetryPolicy.NONE -> complete(result)
                }
            }
            result.onSuccess {
                complete(result)
            }
        }
    }
}
