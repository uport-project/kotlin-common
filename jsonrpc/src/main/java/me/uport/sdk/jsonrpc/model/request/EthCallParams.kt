package me.uport.sdk.jsonrpc.model.request

import kotlinx.serialization.Serializable

/**
 * encapsulates the params used to execute an eth_call
 */
@Serializable
internal class EthCallParams(
    private val to: String,
    private val data: String
)
