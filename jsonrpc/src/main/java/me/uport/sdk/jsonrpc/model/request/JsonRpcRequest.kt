@file:Suppress("EXPERIMENTAL_API_USAGE", "unused")
@file:UseSerializers(BigIntegerSerializer::class)

package me.uport.sdk.jsonrpc.model.request

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import me.uport.sdk.jsonrpc.model.BigIntegerSerializer

/**
 * Encapsulates the body of an eth JsonRPC call
 */
@Serializable
internal class JsonRpcRequest(
    private val method: String,
    private val params: List<@ContextualSerialization Any?>,
    @Required
    private val id: Int = 1,
    @Required
    private val jsonrpc: String = "2.0"
) {
    fun toJson() = Json(JsonConfiguration.Stable).stringify(serializer(), this)
}

