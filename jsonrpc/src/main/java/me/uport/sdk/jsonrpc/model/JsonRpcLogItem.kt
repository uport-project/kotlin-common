@file:UseSerializers(BigIntegerSerializer::class)

package me.uport.sdk.jsonrpc.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigInteger

/**
 * Encapsulates a log item for eth_getLogs
 */
@Serializable
data class JsonRpcLogItem(
    val address: String,
    val topics: List<String>,
    val data: String,
    val blockNumber: BigInteger,
    val transactionHash: String,
    val transactionIndex: BigInteger,
    val blockHash: String,
    val logIndex: BigInteger,
    val removed: Boolean
)
