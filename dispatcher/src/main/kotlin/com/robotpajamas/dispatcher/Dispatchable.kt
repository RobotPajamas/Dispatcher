package com.robotpajamas.dispatcher

enum class State {
    NONE, READY, EXECUTING, FINISHING, FINISHED, RESCHEDULED
}

interface Dispatchable : Runnable, Cancellable, Completable, Executable, Timeoutable, Retriable {
    val id: String
    var state: State

    override fun execute() {
        execution.invoke { result ->
            complete(result)

            // TODO: Moving this retry code to complete to see if that fixes the "timeout" no-retry issue
//            result.onFailure {
//                when (retryPolicy) {
//                    RetryPolicy.RETRY, RetryPolicy.RESCHEDULE ->
//                        if (retries < maxRetries) retry() else complete(result)
//                    RetryPolicy.NONE -> complete(result)
//                }
//            }
//            result.onSuccess {
//                complete(result)
//            }
        }
    }

    override fun complete(result: Result<*>) {
        result.onFailure {
            when (retryPolicy) {
                RetryPolicy.RETRY, RetryPolicy.RESCHEDULE -> if (retries < maxRetries) retry() else super.complete(result)
                RetryPolicy.NONE -> super.complete(result)
            }
        }
        result.onSuccess {
            super.complete(result)
        }
    }
}
