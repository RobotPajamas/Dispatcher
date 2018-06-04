package com.robotpajamas.dispatcher

import com.robotpajamas.dispatcher.mocks.MockDispatchable
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SerialDispatcherTest {

    @Before
    fun setup() {

    }

    @After
    fun teardown() {

    }

    @Test
    fun clear() {
        val dispatcher = SerialDispatcher()
        dispatcher.enqueue(MockDispatchable())
        dispatcher.enqueue(MockDispatchable())
        dispatcher.enqueue(MockDispatchable())
        dispatcher.enqueue(MockDispatchable())
        assertThat(dispatcher.count()).isNotEqualTo(0)

        dispatcher.clear()
        assertThat(dispatcher.count()).isEqualTo(0)
    }

    @Test
    fun count() {
        val dispatcher = SerialDispatcher()
        dispatcher.enqueue(MockDispatchable())
        assertThat(dispatcher.count()).isEqualTo(0) // Dispatched item isn't counted

        dispatcher.enqueue(MockDispatchable())
        assertThat(dispatcher.count()).isEqualTo(1)

        dispatcher.enqueue(MockDispatchable())
        assertThat(dispatcher.count()).isEqualTo(2)
    }

    @Test
    fun enqueue() {
    }
}