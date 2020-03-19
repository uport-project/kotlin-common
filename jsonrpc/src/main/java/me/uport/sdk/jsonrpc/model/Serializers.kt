package me.uport.sdk.jsonrpc.model

import kotlinx.serialization.*
import org.kethereum.extensions.hexToBigInteger
import org.kethereum.extensions.toHexString
import org.komputing.khex.extensions.clean0xPrefix
import org.komputing.khex.extensions.hexToByteArray
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