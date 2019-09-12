package me.uport.sdk.jsonrpc.model.exceptions

import kotlinx.serialization.Serializable

/**
 * Class that represents an error returned by a JsonRPC endpoint
 */
@Serializable
class JsonRpcError(private val code: Int, private val message: String) {
    /**
     * Convert this to an [JsonRpcException] so it can be thrown
     */
    fun toException() = JsonRpcException(code, message)
}

const val JSON_RPC_INTERNAL_ERROR_CODE = -32603

/**
 * Exception equivalent of a [JsonRpcError]
 */
class JsonRpcException(
    val code: Int = JSON_RPC_INTERNAL_ERROR_CODE,
    override val message: String = "Internal error"
) : Exception(message)