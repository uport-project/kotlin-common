package me.uport.sdk.jsonrpc.model.exceptions

/**
 * Thrown when a `txHash` is not known by the ETH network
 */
class TransactionNotFoundException(txHash: String) :
    RuntimeException("The transaction with hash=$txHash has not been mined yet")
