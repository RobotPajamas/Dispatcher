package com.robotpajamas.dispatcher

import android.os.Handler
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.robotpajamas.dispatcher.mocks.MockDispatchable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SerialDispatcherTest {

    @Test
    fun clear() {
        val dispatcher = SerialDispatcher()
        dispatcher.enqueue(MockDispatchable())
        dispatcher.enqueue(MockDispatchable())
        dispatcher.enqueue(MockDispatchable())
        dispatcher.enqueue(MockDispatchable())

        assertThat(dispatcher.count()).isEqualTo(1)
        dispatcher.clear()
        assertThat(dispatcher.count()).isEqualTo(0)
    }

    @Test
    fun count() {
        val dispatcher = SerialDispatcher()
        dispatcher.enqueue(MockDispatchable())
        assertThat(dispatcher.count()).isEqualTo(1)
    }

    @Test
    fun enqueue() {
    }
}