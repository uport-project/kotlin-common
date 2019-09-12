@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.uport.sdk.jsonrpc.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.uport.sdk.jsonrpc.model.exceptions.JsonRpcError

/**
 * Encapsulates the body of a Json RPC response with a string result
 */
@Serializable
internal class JsonRpcStringResponse(
    val result: String? = null,
    val error: JsonRpcError? = null
) {

    companion object {

        /**
         * Deserializer for [JsonRpcStringResponse]
         */
        fun fromJson(jsonString: String): JsonRpcStringResponse =
            Json.nonstrict.parse(serializer(), jsonString)

    }
}
