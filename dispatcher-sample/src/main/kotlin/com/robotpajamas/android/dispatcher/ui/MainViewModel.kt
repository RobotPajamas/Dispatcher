package com.robotpajamas.android.dispatcher.ui

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.robotpajamas.android.dispatcher.BR
import com.robotpajamas.dispatcher.Dispatch
import com.robotpajamas.dispatcher.Result
import com.robotpajamas.dispatcher.RetryPolicy
import com.robotpajamas.dispatcher.SerialDispatcher
import timber.log.Timber

class MainViewModel : BaseObservable() {

    private val dispatcher = SerialDispatcher()
    private lateinit var dispatchNone: Dispatch<String>
    private lateinit var dispatchRetry: Dispatch<String>
    private lateinit var dispatchReschedule: Dispatch<String>

    @Bindable
    var text = ""
        private set(value) {
            field = value
            notifyPropertyChanged(BR.text)
        }

    fun passAll() {
        text += "\n\n"
        dispatchNone = Dispatch(
                id = "NONE",
                retryPolicy = RetryPolicy.NONE,
                execution = { cb ->
                    val message = "${dispatchNone.id}: Executing... Will pass.\n"
                    Timber.d(message)
                    text += message
                    cb(Result.Success("${dispatchNone.id} - Woot!"))
                }) { result ->
            Timber.d("${dispatchNone.id}: Completion running")
            result.onSuccess {
                text += "${dispatchNone.id}: Completion success - $it\n"
            }
            result.onFailure {
                text += "${dispatchNone.id}: Completion failure - $it\n"
            }
        }

        dispatchRetry = Dispatch(
                id = "RETRY",
                retryPolicy = RetryPolicy.RETRY,
                execution = { cb ->
                    val message = "${dispatchRetry.id}: Executing... Will pass.\n"
                    Timber.d(message)
                    text += message
                    cb(Result.Success("${dispatchRetry.id} - Woot!"))
                }) { result ->
            Timber.d("${dispatchRetry.id}: Completion running")
            result.onSuccess {
                text += "${dispatchRetry.id}: Completion success - $it\n"
            }
            result.onFailure {
                text += "${dispatchRetry.id}: Completion failure - $it\n"
            }
        }

        dispatchReschedule = Dispatch(
                id = "RESCHED",
                retryPolicy = RetryPolicy.RESCHEDULE,
                execution = { cb ->
                    val message = "${dispatchReschedule.id}: Executing... Will pass.\n"
                    Timber.d(message)
                    text += message
                    cb(Result.Success("${dispatchReschedule.id} - Woot!"))
                }) { result ->
            Timber.d("${dispatchReschedule.id}: Completion running")
            result.onSuccess {
                text += "${dispatchReschedule.id}: Completion success - $it\n"
            }
            result.onFailure {
                text += "${dispatchReschedule.id}: Completion failure - $it\n"
            }
        }

        dispatcher.enqueue(dispatchReschedule)
        dispatcher.enqueue(dispatchNone)
        dispatcher.enqueue(dispatchRetry)
    }

    fun failAll() {
        text += "\n\n"
        dispatchNone = Dispatch(
                id = "NONE",
                retryPolicy = RetryPolicy.NONE,
                execution = { cb ->
                    val message = "${dispatchNone.id}: Executing... Will fail.\n"
                    Timber.d(message)
                    text += message
                    cb(Result.Failure(Exception("${dispatchNone.id}: Fake failure")))
                }) { result ->
            Timber.d("${dispatchNone.id}: Completion running")
            result.onSuccess {
                text += "${dispatchNone.id}: Completion success - $it\n"
            }
            result.onFailure {
                text += "${dispatchNone.id}: Completion failure - $it\n"
            }
        }

        dispatchRetry = Dispatch(
                id = "RETRY",
                retryPolicy = RetryPolicy.RETRY,
                execution = { cb ->
                    val message = "${dispatchRetry.id}: Executing... Will fail.\n"
                    Timber.d(message)
                    text += message
                    cb(Result.Failure(Exception("${dispatchRetry.id}: Fake failure")))
                }) { result ->
            Timber.d("${dispatchRetry.id}: Completion running")
            result.onSuccess {
                text += "${dispatchRetry.id}: Completion success - $it\n"
            }
            result.onFailure {
                text += "${dispatchRetry.id}: Completion failure - $it\n"
            }
        }

        dispatchReschedule = Dispatch(
                id = "RESCHED",
                retryPolicy = RetryPolicy.RESCHEDULE,
                execution = { cb ->
                    val message = "${dispatchReschedule.id}: Executing... Will fail.\n"
                    Timber.d(message)
                    text += message
                    cb(Result.Failure(Exception("${dispatchReschedule.id}: Fake failure")))
                }) { result ->
            Timber.d("${dispatchReschedule.id}: Completion running")
            result.onSuccess {
                text += "${dispatchReschedule.id}: Completion success - $it\n"
            }
            result.onFailure {
                text += "${dispatchReschedule.id}: Completion failure - $it\n"
            }
        }

        dispatcher.enqueue(dispatchReschedule)
        dispatcher.enqueue(dispatchNone)
        dispatcher.enqueue(dispatchRetry)
    }

    fun clear() {
        text = ""
    }
}