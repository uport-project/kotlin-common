@file:UseSerializers(BigIntegerSerializer::class, ByteArraySerializer::class)

package me.uport.sdk.jsonrpc.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigInteger

/**
 * Data representing a Transaction receipt (eth_getTransactionReceipt)
 */
@Serializable
data class TransactionReceipt(
    val transactionHash: String? = "",
    val transactionIndex: BigInteger? = BigInteger.ZERO,
    val blockNumber: BigInteger? = BigInteger.ZERO,
    val blockHash: String? = "",
    val cumulativeGasUsed: BigInteger = BigInteger.ZERO,
    val gasUsed: BigInteger = BigInteger.ZERO,
    val from: String = "",
    val to: String? = null,
    val contractAddress: String? = null,
    val logs: List<JsonRpcLogItem?>? = null,
    val logsBloom: String? = "",
    val status: BigInteger = BigInteger.ZERO
)
