package me.uport.sdk.jsonrpc.model.exceptions

/**
 * Exception equivalent of a [JsonRpcError]
 */
open class JsonRpcException(
    open val code: Int = JSON_RPC_INTERNAL_ERROR_CODE,
    override val message: String = "Internal error"
) : Exception(message)

