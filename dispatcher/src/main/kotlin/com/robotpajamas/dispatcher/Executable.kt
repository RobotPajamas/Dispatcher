package com.robotpajamas.dispatcher

typealias ExecutionBlock<T> = ((Result<T>) -> Unit) -> Unit

interface Executable {
    val execution: ExecutionBlock<*>
    fun execute()
}