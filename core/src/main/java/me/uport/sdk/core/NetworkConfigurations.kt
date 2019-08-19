package me.uport.sdk.core

import org.walleth.khex.clean0xPrefix
import org.walleth.khex.prepend0xPrefix

/**
 * Defines a collection of settings for multiple ethereum networks.
 */
interface NetworkConfigurations {

    /**
     * Register an ethereum network.
     * Implementations can use the [EthNetwork.networkId] and/or [EthNetwork.name]
     * to map them to the provided [network]
     *
     * This method can be chained to register multiple nets.
     */
    fun registerNetwork(network: EthNetwork): NetworkConfigurations

    /**
     * Try to obtain a previously registered network.
     * @throws IllegalArgumentException if the networkIdentifier
     * doesn't match any previously registered network
     */
    operator fun get(networkIdentifier: String): EthNetwork

}

/**
 * The default implementation of [NetworkConfigurations]
 * This implementation allows networks to be indexed by
 * [EthNetwork.name] as well as by [EthNetwork.networkId]
 *
 */
open class MultiNetworkConfig : NetworkConfigurations {

    /**
     * Register an ETH network configuration.
     * This overrides any previously registered network with the same `networkId`
     */
    override fun registerNetwork(network: EthNetwork): MultiNetworkConfig {
        val normalizedId = cleanId(network.networkId)

        _networkConfigMap[normalizedId] = network
        _networkConfigMap[network.name] = network

        return this
    }

    /**
     * Gets an [EthNetwork] based on a [networkIdentifier]
     */
    override fun get(networkIdentifier: String): EthNetwork {
        val cleanNetId = cleanId(networkIdentifier)
        return _networkConfigMap[cleanNetId]
            ?: _networkConfigMap[networkIdentifier]
            ?: throw IllegalArgumentException("No known configuration for the `[$networkIdentifier]` ethereum network")
    }

    private val _networkConfigMap = emptyMap<String, EthNetwork>().toMutableMap()

    companion object {
        private fun cleanId(id: String) = id.clean0xPrefix().trimStart('0').prepend0xPrefix()
    }

}
