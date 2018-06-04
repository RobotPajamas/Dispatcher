package com.robotpajamas.dispatcher

interface Dispatcher {
    fun clear()
    fun enqueue(item: Dispatchable)
}