package me.uport.sdk.testhelpers

import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isInstanceOf
import kotlinx.coroutines.delay
import org.junit.Test

class AssertionHelpersKtTest {

    @Test
    fun `can coAssert on simple methods`() {

        coAssert {
            delay(1)
        }.doesNotThrowAnyException()

    }

    @Test
    fun `can coAssert on throwing methods`() {
        coAssert {
            delay(1)
            throw IllegalStateException("I'm throwing it")
        }.thrownError {
            isInstanceOf(IllegalStateException::class)
            hasMessage("I'm throwing it")
        }
    }

    @Test
    fun `can check instance of multiple types`() {
        assertThat("hello").isInstanceOf(listOf(String::class, Number::class))
    }
}