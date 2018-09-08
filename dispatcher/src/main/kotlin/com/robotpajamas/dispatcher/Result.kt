package com.robotpajamas.dispatcher

sealed class Result<Value> {
    abstract val value: Value?
    abstract val error: Exception?

    class Success<Value : Any>(override val value: Value) : Result<Value>() {
        override val error: Exception? = null
    }

    class Failure<Nothing>(override val error: Exception) : Result<Nothing>() {
        override val value: Nothing? = null
    }

    val isSuccess: Boolean
        get() = when (this) {
            is Result.Success -> true
            else -> false
        }

    val isFailure: Boolean
        get() = !isSuccess

    fun onSuccess(call: (Value) -> Unit) {
        if (this is Result.Success) {
            call(value)
        }
    }

    fun onFailure(call: (Exception) -> Unit) {
        if (this is Result.Failure) {
            call(error)
        }
    }

    @Throws(Exception::class)
    fun unwrap(): Value = when (this) {
        is Result.Success -> value
        is Result.Failure -> throw error
    }
}

inline fun <reified T> Result<*>.to(): Result<T>? {
    @Suppress("UNCHECKED_CAST")
    return this as? Result<T>
}
