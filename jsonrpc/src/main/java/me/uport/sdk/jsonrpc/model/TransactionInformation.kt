@file:UseSerializers(BigIntegerSerializer::class, ByteArraySerializer::class)

package me.uport.sdk.jsonrpc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigInteger
import javax.annotation.Generated

/**
 * Data representing the transaction information resulting from `eth_getTransactionByHash`
 */
@Serializable
data class TransactionInformation(
    @SerialName("hash")
    val txHash: String? = null,
    val nonce: BigInteger = BigInteger.ZERO,
    val blockHash: String? = null,
    val blockNumber: BigInteger? = null,
    val transactionIndex: BigInteger? = null,
    val from: String = "",
    val to: String? = null,
    val value: BigInteger = BigInteger.ZERO,
    val gas: BigInteger = BigInteger.ZERO,
    val gasPrice: BigInteger = BigInteger.ZERO,
    val input: ByteArray = byteArrayOf(),
    val v : BigInteger = BigInteger.ZERO,
    val r : BigInteger = BigInteger.ZERO,
    val s : BigInteger = BigInteger.ZERO
)

{
    @Suppress("ComplexMethod")
    @Generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransactionInformation

        if (txHash != other.txHash) return false
        if (nonce != other.nonce) return false
        if (blockHash != other.blockHash) return false
        if (blockNumber != other.blockNumber) return false
        if (transactionIndex != other.transactionIndex) return false
        if (from != other.from) return false
        if (to != other.to) return false
        if (value != other.value) return false
        if (gas != other.gas) return false
        if (gasPrice != other.gasPrice) return false
        if (!input.contentEquals(other.input)) return false
        if (v != other.v) return false
        if (r != other.r) return false
        if (s != other.s) return false

        return true
    }

    @Generated
    override fun hashCode(): Int {
        var result = txHash?.hashCode() ?: 0
        result = 31 * result + nonce.hashCode()
        result = 31 * result + (blockHash?.hashCode() ?: 0)
        result = 31 * result + (blockNumber?.hashCode() ?: 0)
        result = 31 * result + (transactionIndex?.hashCode() ?: 0)
        result = 31 * result + from.hashCode()
        result = 31 * result + (to?.hashCode() ?: 0)
        result = 31 * result + value.hashCode()
        result = 31 * result + gas.hashCode()
        result = 31 * result + gasPrice.hashCode()
        result = 31 * result + input.contentHashCode()
        result = 31 * result + v.hashCode()
        result = 31 * result + r.hashCode()
        result = 31 * result + s.hashCode()
        return result
    }
}
