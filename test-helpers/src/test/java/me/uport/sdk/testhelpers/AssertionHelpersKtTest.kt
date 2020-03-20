package me.uport.sdk.testhelpers

import assertk.all
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isSuccess
import kotlinx.coroutines.delay
import org.junit.Test

class AssertionHelpersKtTest {

    @Test
    fun `can coAssert on simple methods`() {

        coAssert {
            delay(1)
        }.isSuccess()

    }

    @Test
    fun `can coAssert on throwing methods`() {
        coAssert {
            delay(1)
            throw IllegalStateException("I'm throwing it")
        }.isFailure().all {
            isInstanceOf(IllegalStateException::class)
            hasMessage("I'm throwing it")
        }
    }

    @Test
    fun `can check instance of multiple types`() {
        assertThat("hello").isInstanceOf(listOf(String::class, Number::class))
    }
}
