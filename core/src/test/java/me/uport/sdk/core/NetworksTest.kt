package me.uport.sdk.core

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
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
        assertThat(Networks.get("0x1").rpcUrl).isEqualTo("http://localhost:8545")
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
        assertThat(Networks.get("0x4321").ethrDidRegistry)
            .isEqualTo(Networks.mainnet.ethrDidRegistry)
    }

    @Test
    fun `can find a registered network using the name`() {

        assertThat(Networks.get("mainnet")).isEqualTo(Networks.mainnet)

        assertThat(Networks.get("rinkeby")).isEqualTo(Networks.rinkeby)

        assertThat(Networks.get("kovan")).isEqualTo(Networks.kovan)

        assertThat(Networks.get("ropsten")).isEqualTo(Networks.ropsten)
    }

    @Test
    fun `can find a registered network using the different case name`() {
        assertThat(Networks.get("MainNet")).isEqualTo(Networks.mainnet)
    }

    @Test
    fun `throws error if the network name does not match`() {
        assertThat {
            Networks.get("manet")
        }.isFailure().all {
            isInstanceOf(IllegalStateException::class)
        }
    }

    @Test
    fun `getting an unknown network throws`() {
        assertThat {
            Networks.get("0x1badc0de")
        }.isFailure().all {
            isInstanceOf(IllegalStateException::class)
        }
    }
}
