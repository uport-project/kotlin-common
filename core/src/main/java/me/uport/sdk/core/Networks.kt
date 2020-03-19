@file:Suppress("unused")

package me.uport.sdk.core

import org.komputing.khex.extensions.clean0xPrefix
import org.komputing.khex.extensions.prepend0xPrefix

/**
 * Convenience singleton that holds URLs and addresses for different eth networks.
 *
 * use the `registerNetwork` method to override defaults
 */
object Networks {

    private const val defaultFaucetUrl = "https://sensui.uport.me/api/v1/fund/"
    private const val defaultTxRelayUrl = "https://sensui.uport.me/api/v2/relay/"
    private const val mainnetId = "0x1"
    private const val ropstenId = "0x3"
    private const val rinkebyId = "0x4"
    private const val kovanId = "0x2a"
    private const val DEFAULT_ERC1056_REGISTRY = "0xdca7ef03e98e0dc2b855be647c39abe984fcf21b"

    private val NETWORK_CONFIG = emptyMap<String, EthNetwork>().toMutableMap()

    init {
        registerAllNetworksWithInfura("e72b472993ff46d3b5b88faa47214d7f")
    }

    fun registerAllNetworksWithInfura(infuraId: String) {
        registerNetwork(
            EthNetwork(
                name = "mainnet",
                networkId = mainnetId,
                rpcUrl = "https://mainnet.infura.io/v3/${infuraId}",
                ethrDidRegistry = DEFAULT_ERC1056_REGISTRY,
                explorerUrl = "https://etherscan.io",
                // MNID.encode(mainnetId, "0xab5c8051b9a1df1aab0149f8b0630848b7ecabf6"),
                uPortRegistry = "2ngV6QowStW3ebKXYUhy43wCqeLXcuTfHj2",
                faucetUrl = defaultFaucetUrl,
                relayUrl = defaultTxRelayUrl,
                txRelayAddress = "0xec2642cd5a47fd5cca2a8a280c3b5f88828aa578"
            )
        )
        registerNetwork(
            EthNetwork(
                name = "rinkeby",
                networkId = rinkebyId,
                rpcUrl = "https://rinkeby.infura.io/v3/${infuraId}",
                ethrDidRegistry = DEFAULT_ERC1056_REGISTRY,
                explorerUrl = "https://rinkeby.etherscan.io",
                //MNID.encode(rinkebyId, "0x2cc31912b2b0f3075a87b3640923d45a26cef3ee"),
                uPortRegistry = "2ogxWTKKfy6kwfqdgdEE6GCdoFD4vm4YRZe",
                faucetUrl = "https://api.uport.me/sensui/fund/",
                relayUrl = "https://api.uport.me/sensui/relay/",
                txRelayAddress = "0xda8c6dce9e9a85e6f9df7b09b2354da44cb48331"
            )
        )
        registerNetwork(
            EthNetwork(
                name = "ropsten",
                networkId = ropstenId,
                rpcUrl = "https://ropsten.infura.io/v3/${infuraId}",
                ethrDidRegistry = DEFAULT_ERC1056_REGISTRY,
                explorerUrl = "https://ropsten.etherscan.io",
                //MNID.encode(ropstenId, "0x41566e3a081f5032bdcad470adb797635ddfe1f0"),
                uPortRegistry = "2oKVhUttUcwaFAopRBGA21NDJoYcBb3a6iz",
                faucetUrl = defaultFaucetUrl,
                relayUrl = defaultTxRelayUrl,
                txRelayAddress = "0xa5e04cf2942868f5a66b9f7db790b8ab662039d5"
            )
        )
        registerNetwork(
            EthNetwork(
                name = "kovan",
                networkId = kovanId,
                rpcUrl = "https://kovan.infura.io/v3/${infuraId}",
                ethrDidRegistry = DEFAULT_ERC1056_REGISTRY,
                explorerUrl = "https://kovan.etherscan.io",
                //MNID.encode(kovanId, "0x5f8e9351dc2d238fb878b6ae43aa740d62fc9758"),
                uPortRegistry = "354S1QuCzkmKoQ3ADSLp1KtqAe8gZ74F9am",
                faucetUrl = defaultFaucetUrl,
                relayUrl = defaultTxRelayUrl,
                txRelayAddress = "0xa9235151d3afa7912e9091ab76a36cbabe219a0c"
            )
        )
    }

    /**
     * shorthand for getting the configuration for ETH mainnet (equivalent with Networks.get("0x1"))
     */
    val mainnet
        get() = get(mainnetId)

    /**
     * shorthand for getting the configuration for rinkeby test network (equivalent with Networks.get("0x4"))
     */
    val rinkeby
        get() = get(rinkebyId)

    /**
     * shorthand for getting the configuration for the ropsten test network (equivalent with Networks.get("0x3"))
     */
    val ropsten
        get() = get(ropstenId)

    /**
     * shorthand for getting the configuration for the kovan test network (equivalent with Networks.get("0x2a"))
     */
    val kovan
        get() = get(kovanId)

    /**
     * Register an ETH network configuration.
     * This overrides any previously registered network with the same `networkId`
     */
    fun registerNetwork(network: EthNetwork) {
        val normalizedId = cleanId(network.networkId)

        NETWORK_CONFIG[normalizedId] = network
    }

    /**
     * Gets an [EthNetwork] based on a [networkId] or [name]
     */
    fun get(nameOrId: String): EthNetwork {
        val cleanNetId = cleanId(nameOrId)
        return NETWORK_CONFIG[cleanNetId]
            ?: NETWORK_CONFIG[nameOrId]
            ?: getNetworkByName(nameOrId)
            ?: throw IllegalStateException("network [$nameOrId] not configured")
    }

    /**
     * Searches for an [EthNetwork] based on a [name]
     */
    private fun getNetworkByName(name: String): EthNetwork? {
        val queryName = name.toLowerCase()
        return NETWORK_CONFIG.values.find { it.name.toLowerCase() == queryName }
    }

    private fun cleanId(id: String) = id.clean0xPrefix().trimStart('0').prepend0xPrefix()

}

/**
 * A class that encapsulates the endpoints and metadata related to a particular ETH network
 */
data class EthNetwork(

    /**
     * Name of the network
     *
     * Example: "kovan"
     */
    val name: String,

    /**
     * network ID - hex encoded number or first 4 bytes of genesis block hash
     *
     * Example: "0x2a"
     */
    val networkId: String,

    /**
     * Json RPC endpoint to be used with this network.
     * For public networks this defaults to the infura URLs
     *
     * Example: "https://kovan.infura.io/uport"
     */
    val rpcUrl: String,

    /**
     *
     * **optional**
     *
     * hex encoded contract address for the
     * [EIP 1056 (ETHR DID) registry](https://github.com/uport-project/ethr-did-registry)
     * on this network
     *
     * Example: "0xdca7ef03e98e0dc2b855be647c39abe984fcf21b"
     */
    val ethrDidRegistry: String = "",

    /**
     * **optional**
     *
     * Block explorer URL.
     * For public networks, this defaults to etherscan
     *
     * Example: "https://kovan.etherscan.io"
     */
    val explorerUrl: String = "",

    /**
     * **optional**
     *
     * MNID encoded contract address for the `did:uport:` registry.
     * This is used by the uPort DID resolver to fetch the location of the DID document
     * for a particular uPort DID.
     *
     * Example:
     * `MNID.encode({address: '0x5f8e9351dc2d238fb878b6ae43aa740d62fc9758', network: '0x2a'})`
     *
     * **NOTE** uPort DIDs are being deprecated in favor of ethr DID
     */
    val uPortRegistry: String = "",

    /**
     * **optional**
     *
     * metaTX faucet URL
     *
     * Example: "https://sensui.uport.me/api/v1/fund/"
     */
    @Deprecated("uPort proxy contracts and meta TX functionality is not supported")
    val faucetUrl: String = "",

    /**
     * **optional**
     *
     * transaction relay URL
     * Example: "https://sensui.uport.me/api/v2/relay/"
     */
    @Deprecated("uPort proxy contracts and meta TX functionality is not supported")
    val relayUrl: String = "",

    /**
     * **optional**
     *
     * hex encoded transaction relay contract address
     *
     * Example: "0xa9235151d3afa7912e9091ab76a36cbabe219a0c"
     */
    @Deprecated("uPort proxy contracts and meta TX functionality is not supported")
    val txRelayAddress: String = ""
)



