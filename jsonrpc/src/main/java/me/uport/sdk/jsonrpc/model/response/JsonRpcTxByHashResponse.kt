@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.uport.sdk.jsonrpc.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.uport.sdk.jsonrpc.model.exceptions.JsonRpcError
import me.uport.sdk.jsonrpc.model.TransactionInformation

/**
 * Wrapper for a TransactionInformation response resulting from an `eth_getTransactionByHash` call
 */
@Serializable
internal class JsonRpcTxByHashResponse(
    val result: TransactionInformation? = null,
    val error: JsonRpcError? = null
) {

    companion object {
        /**
         * Deserializer for [JsonRpcTxByHashResponse]
         */
        fun fromJson(jsonString: String): JsonRpcTxByHashResponse =
            Json.nonstrict.parse(serializer(), jsonString)
    }
}