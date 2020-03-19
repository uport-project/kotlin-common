package me.uport.sdk.jsonrpc.model

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Serializer
import me.uport.sdk.core.clean0xPrefix
import me.uport.sdk.core.hexToBigInteger
import me.uport.sdk.core.hexToByteArray
import org.kethereum.extensions.toHexString
import org.komputing.khex.extensions.toHexString
import java.math.BigInteger

/**
 * This is a (de)serialization helper for BigInteger encoded as hex string,
 * optionally prefixed with `0x`. This is usable for eth_QUANTITY
 */
@Serializer(forClass = BigInteger::class)
object BigIntegerSerializer : KSerializer<BigInteger> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("BigIntegerSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigInteger) {
        encoder.encodeString(value.toHexString())
    }

    override fun deserialize(decoder: Decoder): BigInteger = decoder
        .decodeString()
        .clean0xPrefix()
        .run {
            when {
                length == 0 -> prependIndent("00")
                length % 2 != 0 -> prependIndent("0")
                else -> this
            }
        }
        .hexToBigInteger()
}

/**
 * This is a (de)serialization helper for ByteArray encoded as hex string,
 * optionally prefixed with `0x`. This is usable for eth_DATA
 */
@Serializer(forClass = ByteArray::class)
object ByteArraySerializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("ByteArraySerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteArray) {
        encoder.encodeString(value.toHexString())
    }

    override fun deserialize(decoder: Decoder): ByteArray = decoder
        .decodeString()
        .clean0xPrefix()
        .run {
            when {
                length % 2 != 0 -> prependIndent("0")
                else -> this
            }
        }
        .hexToByteArray()
}
