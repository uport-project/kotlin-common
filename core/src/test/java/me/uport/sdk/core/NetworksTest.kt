@file:Suppress("DEPRECATION")

package me.uport.sdk.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import org.junit.Test

class NetworksTest {

    @Test
    fun `registering an existing network overrides it`() {
        //override default registry address for mainnet
        Networks.registerNetwork(
            Networks.mainnet.copy(rpcUrl = "http://localhost:8545")
        )

        assertThat(Networks.mainnet.rpcUrl).isEqualTo("http://localhost:8545")
        assertThat(Networks["0x1"].rpcUrl).isEqualTo("http://localhost:8545")
    }

    @Test
    fun `registering a new network works`() {
        //register a clone of mainnet with different id
        Networks.registerNetwork(
            Networks.mainnet.copy(networkId = "0x4321")
        )

        Networks.registerNetwork(
            EthNetwork(
                name = "local",
                networkId = "0x1234",
                rpcUrl = "http://localhost:8545",
                ethrDidRegistry = "0xdca7ef03e98e0dc2b855be647c39abe984fcf21b"
            )
        )

        //other fields match
        assertThat(Networks["0x4321"].ethrDidRegistry)
            .isEqualTo(Networks.mainnet.ethrDidRegistry)
    }

    @Test
    fun `getting an unknown network throws`() {
        assertThat {
            Networks["0x1badc0de"]
        }.thrownError {
            isInstanceOf(IllegalArgumentException::class)
        }
    }
}