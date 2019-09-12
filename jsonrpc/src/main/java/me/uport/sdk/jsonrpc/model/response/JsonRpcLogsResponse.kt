@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.uport.sdk.jsonrpc.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.uport.sdk.jsonrpc.model.exceptions.JsonRpcError
import me.uport.sdk.jsonrpc.model.JsonRpcLogItem

/**
 * Encapsulates the body of a eth_getLogs Json RPC response
 */
@Serializable
internal class JsonRpcLogsResponse(
    val result: List<JsonRpcLogItem>? = null,
    val error: JsonRpcError? = null
) {

    companion object {

        /**
         * Deserializer for [JsonRpcLogsResponse]
         */
        fun fromJson(jsonString: String): JsonRpcLogsResponse =
            Json.nonstrict.parse(serializer(), jsonString)

    }
}