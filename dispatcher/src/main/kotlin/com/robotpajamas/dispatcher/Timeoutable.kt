package com.robotpajamas.dispatcher

interface Timeoutable {
    val timeout: Int // ms
    fun timedOut()
}