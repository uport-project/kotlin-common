@file:Suppress("ReplaceCallWithBinaryOperator")

package me.uport.sdk.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test

class ExtensionsKtTest {

    @Before
    fun `run before every test`() {
        stubUiContext()
    }

    @Test
    fun `can use stubbed UI context in unit test`() {

        suspend fun whatever(): String = withContext(UI) { "hello world" }

        runBlocking {
            assertThat(whatever()).isEqualTo("hello world")
        }
    }

    @Test
    fun `base 64 works both ways`() {
        val strings = listOf(
            "",
            "f",
            "fo",
            "foo",
            "foo ",
            "foo b",
            "foo ba",
            "foo bar",
            "foo bar ",
            "foo bar b",
            "foo bar ba",
            "foo bar baz"
        )
        strings.forEach {
            assertThat(String(it.toBase64().decodeBase64())).isEqualTo(it)
            assertThat(it.toBase64().decodeBase64()).isEqualTo(it.toByteArray())
            assertThat(it.toBase64UrlSafe().decodeBase64()).isEqualTo(it.toByteArray())
        }

        val bytes = ByteArray(255) { it.toByte() }
        for (i in 0..bytes.size) {
            val tested = bytes.copyOfRange(0, i)
            assertThat(tested.toBase64().decodeBase64()).isEqualTo(tested)
            assertThat(tested.toBase64UrlSafe().decodeBase64()).isEqualTo(tested)
        }
    }
}
