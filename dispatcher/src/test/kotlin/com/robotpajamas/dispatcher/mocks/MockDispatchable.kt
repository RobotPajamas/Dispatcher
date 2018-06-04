package com.robotpajamas.dispatcher.mocks

import com.robotpajamas.dispatcher.CompletionBlock
import com.robotpajamas.dispatcher.Dispatchable
import com.robotpajamas.dispatcher.ExecutionBlock

class MockDispatchable : Dispatchable {
    override val id: String
        get() = "MockId"

    override fun run() {

    }

    override var isCancelled: Boolean = false
    override val completions: MutableList<CompletionBlock<*>> = mutableListOf()
    override val execution: ExecutionBlock<*>
        get() = {}
    override val timeout: Int = 42

    override fun timedOut() {

    }

}