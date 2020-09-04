package com.robotpajamas.dispatcher

import android.os.Handler
import android.os.Looper.getMainLooper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class RetryTest {

    private val dispatcher = SerialDispatcher()
    private lateinit var dispatchOne: Dispatch<String>
    private lateinit var dispatchTwo: Dispatch<String>
    private lateinit var dispatchThree: Dispatch<String>

    private val actual = mutableListOf<String>()
    private val expected = mutableListOf<String>()
    private val handler = Handler()


    @Before
    fun setUp() {
        actual.clear()
        expected.clear()
        handler.removeCallbacksAndMessages(null)
    }

    @Test
    fun `Given 2 items with RETRY and NONE retry policies, When 2 items are scheduled and RETRY one fails, Then its execution is retried maximum attempts before executing the second item`() {
        dispatchOne = Dispatch(id = "id",
            retryPolicy = RetryPolicy.RETRY,
            execution = { cb ->
                actual.add(dispatchOne.id)
                println("Execution failed")
                cb(Result.Failure(Exception("Mocked failure")))
            },
            completion = { println("Completion block invoked") })
        dispatchTwo = Dispatch(id = "id2",
            retryPolicy = RetryPolicy.NONE,
            execution = { cb ->
                actual.add(dispatchTwo.id)
                println("Execution failed")
                cb(Result.Failure(Exception("Mocked failure")))
            },
            completion = { println("Completion block invoked") })
        expected.addAll(listOf(dispatchOne.id, dispatchOne.id, dispatchOne.id, dispatchTwo.id))

        handler.post {
            dispatcher.enqueue(dispatchOne)
            dispatcher.enqueue(dispatchTwo)
        }

        shadowOf(getMainLooper()).idle()
        println(actual)
        assertThat(actual).containsExactlyElementsOf(expected)
    }

    @Test
    fun `Given 2 items with RESCHEDULE and NONE retry policies, When 2 items are scheduled and RESCHEDULE one fails, Then it gets re-added to the tail of the queue, And the order of execution matches expected order`() {
        dispatchOne = Dispatch(id = "id",
            retryPolicy = RetryPolicy.RESCHEDULE,
            execution = { cb ->
                actual.add(dispatchOne.id)
                println("Execution failed")
                cb(Result.Failure(Exception("Mocked failure")))
            },
            completion = { println("Completion block invoked") })
        dispatchTwo = Dispatch(id = "id2",
            retryPolicy = RetryPolicy.NONE,
            execution = { cb ->
                actual.add(dispatchTwo.id)
                println("Execution failed")
                cb(Result.Failure(Exception("Mocked failure")))
            },
            completion = { println("Completion block invoked") })
        expected.addAll(listOf(dispatchOne.id, dispatchTwo.id, dispatchOne.id, dispatchOne.id))

        handler.post {
            dispatcher.enqueue(dispatchOne)
            dispatcher.enqueue(dispatchTwo)
        }

        shadowOf(getMainLooper()).idle()
        println(actual)
        assertThat(actual).containsExactlyElementsOf(expected)
    }

    @Test
    fun `Given 3 items with different retry policies, When execution fails, Then the order of retries matches expected order`() {
        dispatchOne = Dispatch(id = "id",
            retryPolicy = RetryPolicy.RESCHEDULE,
            execution = { cb ->
                actual.add(dispatchOne.id)
                println("Execution failed")
                cb(Result.Failure(Exception("Mocked failure")))
            },
            completion = { println("Completion block invoked") })
        dispatchTwo = Dispatch(id = "id2",
            retryPolicy = RetryPolicy.RETRY,
            execution = { cb ->
                actual.add(dispatchTwo.id)
                println("Execution failed")
                cb(Result.Failure(Exception("Mocked failure")))
            },
            completion = { println("Completion block invoked") })
        dispatchThree = Dispatch(id = "id3",
            retryPolicy = RetryPolicy.NONE,
            execution = { cb ->
                actual.add(dispatchThree.id)
                println("Execution failed")
                cb(Result.Failure(Exception("Mocked failure")))
            },
            completion = { println("Completion block invoked") })
        expected.addAll(
            listOf(
                dispatchOne.id, dispatchTwo.id, dispatchTwo.id, dispatchTwo.id,
                dispatchThree.id, dispatchOne.id, dispatchOne.id
            )
        )

        handler.post {
            dispatcher.enqueue(dispatchOne)
            dispatcher.enqueue(dispatchTwo)
            dispatcher.enqueue(dispatchThree)
        }

        shadowOf(getMainLooper()).idle()
        println(actual)
        assertThat(actual).containsExactlyElementsOf(expected)
    }
}
