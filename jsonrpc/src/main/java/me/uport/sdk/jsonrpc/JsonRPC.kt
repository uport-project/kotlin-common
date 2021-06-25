@file:Suppress("LiftReturnOrAssignment", "UnnecessaryVariable", "EXPERIMENTAL_API_USAGE")

package me.uport.sdk.jsonrpc

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import me.uport.sdk.core.HttpClient
import me.uport.sdk.core.hexToBigInteger
import me.uport.sdk.jsonrpc.model.JsonRpcLogItem
import me.uport.sdk.jsonrpc.model.TransactionInformation
import me.uport.sdk.jsonrpc.model.TransactionReceipt
import me.uport.sdk.jsonrpc.model.exceptions.JsonRpcException
import me.uport.sdk.jsonrpc.model.exceptions.TransactionNotFoundException
import me.uport.sdk.jsonrpc.model.request.EthCallParams
import me.uport.sdk.jsonrpc.model.request.JsonRpcLogsRequestParams
import me.uport.sdk.jsonrpc.model.request.JsonRpcRequest
import me.uport.sdk.jsonrpc.model.response.JsonRpcResponse
import java.io.IOException
import java.math.BigInteger

/**
 * Partial wrapper for JsonRPC methods supported by ethereum nodes.
 */
open class JsonRPC(
    private val rpcEndpoint: String,
    private val httpClient: HttpClient = HttpClient()
) {

    private val lenientJson = Json {
        isLenient = true
        ignoreUnknownKeys = true
        useArrayPolymorphism = true
    }

//=============================
// eth_call
//=============================

    /**
     * performs an eth_call
     * the `result` of the JsonRPC call is returned as String.
     * Known parsing errors are caught and rethrown, network errors are bubbled up.
     *
     * See also: https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_call
     *
     * @throws JsonRpcException for error replies coming from the endpoint
     * @throws IOException for network errors or unexpected reply formats
     */
    open suspend fun ethCall(address: String, data: String): String {
        val payloadRequest = JsonRpcRequest(
            method = "eth_call",
            params = listOf(
                EthCallParams(
                    to = address,
                    data = data
                ),
                "latest"
            )
        ).toJson()

        return jsonRpcGenericCall(rpcEndpoint, payloadRequest)
    }


//=============================
// eth_getLogs
//=============================

    /**
     * obtains the list of [JsonRpcLogItem]s corresponding to a given [address] and [topics]
     * between [[fromBlock]..[toBlock]]
     *
     * See also: https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_getlogs
     *
     * @throws JsonRpcException for error replies coming from the endpoint
     * @throws IOException for network errors or unexpected reply formats
     */
    suspend fun getLogs(
        address: String,
        topics: List<String?> = emptyList(),
        fromBlock: BigInteger,
        toBlock: BigInteger
    ): List<JsonRpcLogItem> {
        val payloadRequest = JsonRpcRequest(
            method = "eth_getLogs",
            params = listOf(
                JsonRpcLogsRequestParams(
                    fromBlock,
                    toBlock,
                    address,
                    topics
                )
            )
        ).toJson()

        val rawResult = httpClient.urlPost(rpcEndpoint, payloadRequest)

        val parsedResponse = lenientJson.decodeFromString(
            JsonRpcResponse.serializer(ListSerializer(JsonRpcLogItem.serializer())),
            rawResult
        )

        parsedResponse.error?.let {
            throw it.toException()
        }
        return parsedResponse.result ?: emptyList()
    }

//=============================
// eth_gasPrice
//=============================

    /**
     * Obtains the gas price in Wei or throws an error if one occurred
     *
     * See also: https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_gasPrice
     */
    suspend fun getGasPrice(): BigInteger {
        val payloadRequest = JsonRpcRequest(
            method = "eth_gasPrice",
            params = emptyList<String>()
        ).toJson()

        val priceHex = jsonRpcGenericCall(rpcEndpoint, payloadRequest)
        return priceHex.hexToBigInteger()
    }


//=============================
// eth_chainId
//=============================

    /**
     * Obtains the chainID as reported by the node using EIP-695 `eth_chainId` method
     *
     * See also: http://eips.ethereum.org/EIPS/eip-695
     */
    suspend fun getChainId(): BigInteger {
        val payloadRequest = JsonRpcRequest(
            method = "eth_chainId",
            params = emptyList<String>()
        ).toJson()

        val chainId = jsonRpcGenericCall(rpcEndpoint, payloadRequest)
        return chainId.hexToBigInteger()
    }


//=============================
//eth_getTransactionCount
//=============================

    /**
     * Calls back with the number of already mined transactions made from the given address.
     * The number is usable as `nonce` (since nonce is zero indexed)
     *
     * See also: https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_getTransactionCount
     */
    suspend fun getTransactionCount(address: String): BigInteger {
        val payloadRequest = JsonRpcRequest(
            method = "eth_getTransactionCount",
            params = listOf(address, "latest")
        ).toJson()

        val nonceHex = jsonRpcGenericCall(rpcEndpoint, payloadRequest)
        return nonceHex.hexToBigInteger()
    }


//=============================
//eth_getBalance
//=============================

    /**
     * Calls back with the ETH balance of an account (expressed in Wei)
     *
     * See also: https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_getBalance
     */
    suspend fun getAccountBalance(address: String): BigInteger {
        val payloadRequest = JsonRpcRequest(
            method = "eth_getBalance",
            params = listOf(address, "latest")
        ).toJson()

        val weiCountHex = jsonRpcGenericCall(rpcEndpoint, payloadRequest)
        return weiCountHex.hexToBigInteger()
    }


//=============================
// eth_getTransactionReceipt
//=============================

    /**
     * Obtains a transaction receipt for a given [txHash]
     *
     * See also: https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_getTransactionReceipt
     */
    suspend fun getTransactionReceipt(txHash: String): TransactionReceipt {
        val payloadRequest = JsonRpcRequest(
            method = "eth_getTransactionReceipt",
            params = listOf(txHash)
        ).toJson()

        val rawResult = httpClient.urlPost(rpcEndpoint, payloadRequest)

        val parsedResponse = lenientJson.decodeFromString(
            JsonRpcResponse.serializer(TransactionReceipt.serializer()),
            rawResult
        )

        if (parsedResponse.error != null) {
            throw parsedResponse.error.toException()
        }

        if (parsedResponse.result != null) {
            return parsedResponse.result
        } else {
            throw TransactionNotFoundException(txHash)
        }
    }


//=============================
// eth_getTransactionByHash
//=============================

    /**
     * Obtains the transaction information corresponding to a given [txHash]
     *
     * See also: https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_getTransactionByHash
     */
    suspend fun getTransactionByHash(txHash: String): TransactionInformation {
        val payloadRequest = JsonRpcRequest(
            method = "eth_getTransactionByHash",
            params = arrayListOf(txHash)
        ).toJson()

        val rawResult = httpClient.urlPost(rpcEndpoint, payloadRequest)

        val parsedResponse = lenientJson.decodeFromString(
            JsonRpcResponse.serializer(TransactionInformation.serializer()),
            rawResult
        )

        if (parsedResponse.error != null) {
            throw parsedResponse.error.toException()
        }

        if (parsedResponse.result != null) {
            return parsedResponse.result
        } else {
            throw TransactionNotFoundException(txHash)
        }
    }


//=============================
//eth_sendRawTransaction
//=============================

    /**
     * Sends a hex string representing a [signedTransactionHex] to be mined by the ETH network.
     *
     * @return the txHash of the transaction if it is accepted by the JsonRPC node.
     *
     * @throws JsonRpcException for error replies coming from the [rpcEndpoint]
     * @throws IOException for network errors or unexpected reply formats
     */
    suspend fun sendRawTransaction(
        signedTransactionHex: String
    ): String {

        val payloadRequest = JsonRpcRequest(
            method = "eth_sendRawTransaction",
            params = listOf(signedTransactionHex)
        ).toJson()

        return jsonRpcGenericCall(rpcEndpoint, payloadRequest)
    }

    /**
     * Make a base JsonRPCRequest to the [url] with the given [payloadRequest]
     * and attempt to parse the response body into a [JsonRpcResponse]
     * @throws IOException if response is null or if it can't be parsed from JSON
     * @throws JsonRpcException if the response was parsed and an error field was present
     *
     * @hide
     */
    private suspend fun jsonRpcGenericCall(url: String, payloadRequest: String): String {
        val rawResult = httpClient.urlPost(url, payloadRequest)

        val parsedResponse = lenientJson.decodeFromString(
            JsonRpcResponse.serializer(String.serializer()),
            rawResult
        )

        parsedResponse.error?.let { throw it.toException() }

        return parsedResponse.result.toString()
    }

}
