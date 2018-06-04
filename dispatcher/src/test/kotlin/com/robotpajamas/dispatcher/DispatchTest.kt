package com.robotpajamas.dispatcher

import org.assertj.core.api.Assertions.*
import org.junit.Test

class DispatchTest {

    @Test
    fun run() {
    }

    @Test
    fun cancel() {
        val dispatch = Dispatch<String>(
                id = "something",
                execution = {})
        assertThat(dispatch.isCancelled).isFalse()
        dispatch.cancel()
        assertThat(dispatch.isCancelled).isTrue()
    }


    @Test
    fun getStates() {
        val dispatch = Dispatch<String>(
                id = "something",
                execution = {})
        assertThat(dispatch.state).isEqualTo(State.READY)
//        dispatch.cancel()
//        assertThat(dispatch.state).isEqualTo(State.FINISHED)
    }

    @Test
    fun getTimeout() {
        val expected = 42
        val dispatch = Dispatch<String>(
                id = "something",
                timeout = expected,
                execution = {})
        assertThat(dispatch.timeout).isEqualTo(expected)
    }

    @Test
    fun getId() {
        val expected = "myId"
        val dispatch = Dispatch<String>(id = expected,
                execution = {})
        assertThat(dispatch.id).isEqualTo(expected)
    }
}