package com.robotpajamas.dispatcher.mocks

import com.robotpajamas.dispatcher.*

class MockDispatchable : Dispatchable {
    override val id: String
        get() = "MockId"

    override fun run() {

    }

    override var isCancelled: Boolean = false
    override val completions: MutableList<CompletionBlock<*>> = mutableListOf()
    override val execution: ExecutionBlock<*>
        get() = {}
    override var state: State = State.READY
    override val retryPolicy: RetryPolicy = RetryPolicy.NONE
    override var retries: Int = 0
    override val maxRetries: Int = 2

    override var timeout: Int = 42

    override fun timedOut() {
        isTimedOut = true
    }

    // Test checks
    var isTimedOut = false

}