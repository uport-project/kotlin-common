@file:Suppress("EXPERIMENTAL_API_USAGE", "unused")
@file:UseSerializers(BigIntegerSerializer::class)

package me.uport.sdk.jsonrpc.model.request

import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import me.uport.sdk.jsonrpc.model.BigIntegerSerializer

@ExperimentalSerializationApi
class DynamicLookupSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor =
        ContextualSerializer(Any::class, null, emptyArray()).descriptor

    @InternalSerializationApi
    override fun serialize(encoder: Encoder, value: Any) {
        val actualSerializer =
            encoder.serializersModule.getContextual(value::class) ?: value::class.serializer()
        encoder.encodeSerializableValue(actualSerializer as KSerializer<Any>, value)
    }

    override fun deserialize(decoder: Decoder): Any {
        error("Unsupported")
    }
}

/**
 * Encapsulates the body of an eth JsonRPC call
 */
@Serializable
internal class JsonRpcRequest(
    private val method: String,
    private val params: List<@Serializable(with = DynamicLookupSerializer::class) Any?>,
    @Required
    private val id: Int = 1,
    @Required
    private val jsonrpc: String = "2.0"
) {
    fun toJson() = Json.encodeToString(serializer(), this)
}

