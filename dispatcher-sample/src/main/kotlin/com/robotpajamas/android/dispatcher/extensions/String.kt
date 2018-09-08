package com.robotpajamas.android.dispatcher.extensions

fun String.prepend(message: String): String {
    return "$message$this"
}