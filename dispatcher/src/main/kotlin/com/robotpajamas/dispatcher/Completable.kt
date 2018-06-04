package com.robotpajamas.dispatcher

typealias CompletionBlock<T> = (Result<T>) -> Unit

interface Completable {
    val completions: MutableList<CompletionBlock<*>>
    fun complete(result: Result<*>) {
        completions.forEach { it.invoke(result) }
    }
}