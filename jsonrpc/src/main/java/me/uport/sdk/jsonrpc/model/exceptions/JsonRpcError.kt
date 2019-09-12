package me.uport.sdk.jsonrpc.model.exceptions

import kotlinx.serialization.Serializable

/**
 * Class that represents an error returned by a JsonRPC endpoint
 */
@Serializable
internal class JsonRpcError(private val code: Int, private val message: String) {
    /**
     * Convert this to an [JsonRpcException] so it can be thrown
     */
    fun toException() = when (code) {
        JSON_RPC_ERROR_INVALID_ARGUMENT -> JsonRpcInvalidArgumentException(code, message)
        else -> JsonRpcException(code, message)
    }
}

const val JSON_RPC_INTERNAL_ERROR_CODE = -32603
const val JSON_RPC_ERROR_INVALID_ARGUMENT = -32602
