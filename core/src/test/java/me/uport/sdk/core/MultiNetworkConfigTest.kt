package me.uport.sdk.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import org.junit.Test

class MultiNetworkConfigTest {

    private val mainnetLocal = EthNetwork("mainnet", "0x1", "http://localhost:8545")
    private val rinkebyLocal = EthNetwork("rinkeby", "0x04", "http://localhost:8545")

    @Test
    fun `can get network by direct ID`() {
        val tested = MultiNetworkConfig()
            .registerNetwork(mainnetLocal)
            .registerNetwork(rinkebyLocal)

        assertThat(tested["0x1"]).isEqualTo(mainnetLocal)
        assertThat(tested["0x04"]).isEqualTo(rinkebyLocal)
    }

    @Test
    fun `can get network by clean ID`() {
        val tested = MultiNetworkConfig()
            .registerNetwork(mainnetLocal)
            .registerNetwork(rinkebyLocal)

        assertThat(tested["0x01"]).isEqualTo(mainnetLocal)
        assertThat(tested["0x4"]).isEqualTo(rinkebyLocal)
    }

    @Test
    fun `can get network by name`() {
        val tested = MultiNetworkConfig()
            .registerNetwork(mainnetLocal)
            .registerNetwork(rinkebyLocal)

        assertThat(tested["mainnet"]).isEqualTo(mainnetLocal)
    }

    @Test
    fun `throws when no network found`() {
        val tested = MultiNetworkConfig()
            .registerNetwork(mainnetLocal)
            .registerNetwork(rinkebyLocal)

        assertThat {
            val (_) = tested["0x6123"]
        }.thrownError {
            isInstanceOf(IllegalArgumentException::class)
        }
    }
}