package me.uport.sdk.testhelpers

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class TestTimeProviderTest {

    @Test
    fun `can provide fixed time`() {
        assertThat(TestTimeProvider(1234L).nowMs()).isEqualTo(1234L)
    }
}