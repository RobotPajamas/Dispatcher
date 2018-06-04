package com.robotpajamas.dispatcher

interface Dispatchable : Runnable, Cancellable, Timeoutable {
    val id: String
    fun execute()
}
