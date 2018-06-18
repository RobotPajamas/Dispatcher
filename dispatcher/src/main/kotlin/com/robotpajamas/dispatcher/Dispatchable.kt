package com.robotpajamas.dispatcher

internal enum class State {
    NONE, READY, EXECUTING, FINISHING, FINISHED
}

interface Dispatchable : Runnable, Cancellable, Completable, Executable, Timeoutable {
    val id: String

    override fun execute() {
        execution.invoke { result ->
            complete(result)
        }
    }
}
