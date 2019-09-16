@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.uport.sdk.jsonrpc.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.uport.sdk.jsonrpc.model.exceptions.JsonRpcError

/**
 * Encapsulates a generic response from a JsonRPC endpoint
 */
@Serializable
internal class JsonRpcResponse<T>(
    val result: T? = null,
    val error: JsonRpcError? = null
)