package me.uport.sdk.jsonrpc.model.exceptions

/**
 * Thrown when the argument to an eth JsonRpc call returns error code -32602
 */
class JsonRpcInvalidArgumentException(
    override val code : Int,
    override val message : String
) : JsonRpcException(code, message)
