@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.uport.sdk.jsonrpc.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.uport.sdk.jsonrpc.model.exceptions.JsonRpcError
import me.uport.sdk.jsonrpc.model.TransactionReceipt

/**
 * Wrapper for a transaction receipt response resulting from an `eth_getTransactionReceipt` call
 */
@Serializable
internal class JsonRpcReceiptResponse(
    val result: TransactionReceipt? = null,
    val error: JsonRpcError? = null
) {

    companion object {
        /**
         * Deserializer for [JsonRpcReceiptResponse]
         */
        fun fromJson(jsonString: String): JsonRpcReceiptResponse =
            Json.nonstrict.parse(serializer(), jsonString)
    }
}