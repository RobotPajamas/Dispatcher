package com.robotpajamas.dispatcher

interface Dispatcher {
    fun clear()
    fun count(): Int
    fun enqueue(item: Dispatchable)
}