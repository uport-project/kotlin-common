@file:UseSerializers(BigIntegerSerializer::class)

package me.uport.sdk.jsonrpc.model.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.uport.sdk.jsonrpc.model.BigIntegerSerializer
import java.math.BigInteger

/**
 * Encapsulates the request needed to make an eth_getLogs call
 */
@Suppress("unused")
@Serializable
internal class JsonRpcLogsRequestParams(
    private val fromBlock: BigInteger,
    private val toBlock: BigInteger,
    private val address: String,
    private val topics: List<String?>
)
