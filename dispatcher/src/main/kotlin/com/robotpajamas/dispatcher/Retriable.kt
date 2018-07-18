package com.robotpajamas.dispatcher

enum class RetryPolicy {
    NONE, RETRY, RESCHEDULE
}

interface Retriable {
    val maxRetries: Int
    val retryPolicy: RetryPolicy
    var retries: Int
    fun retry() {
        retries++
    }
}
