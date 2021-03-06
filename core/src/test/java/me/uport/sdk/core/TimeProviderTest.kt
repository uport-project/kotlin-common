package me.uport.sdk.core

import assertk.assertThat
import assertk.assertions.isLessThan
import org.junit.Test

class TimeProviderTest {

    @Test
    fun `default provider is close to current time`() {
        val systemTime = System.currentTimeMillis()
        val defaultProvider = SystemTimeProvider
        //some systems have tens of milliseconds as the lowest granularity
        assertThat(defaultProvider.nowMs()).isLessThan(100L + systemTime)
    }
}
