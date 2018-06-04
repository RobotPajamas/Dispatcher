package com.robotpajamas.dispatcher

interface Cancellable {
    var isCancelled: Boolean
    fun cancel() {
        isCancelled = true
    }
}