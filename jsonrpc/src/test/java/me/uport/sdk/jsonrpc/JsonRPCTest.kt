package me.uport.sdk.jsonrpc

import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import me.uport.sdk.core.HttpClient
import me.uport.sdk.core.Networks
import me.uport.sdk.core.hexToBigInteger
import me.uport.sdk.jsonrpc.model.JsonRpcLogItem
import me.uport.sdk.jsonrpc.model.TransactionInformation
import me.uport.sdk.jsonrpc.model.TransactionReceipt
import me.uport.sdk.jsonrpc.model.exceptions.JsonRpcException
import me.uport.sdk.jsonrpc.model.exceptions.JsonRpcInvalidArgumentException
import me.uport.sdk.jsonrpc.model.exceptions.TransactionNotFoundException
import me.uport.sdk.testhelpers.coAssert
import org.junit.Test
import java.math.BigInteger

class JsonRPCTest {

    @Test
    fun `can send raw transaction`() = runBlocking {
        val signedTx =
            "0xf864028504a817c800825208941fcf8ff78ac5117d9c99b830c74b6668d6ac322901801ba007b829ef52383ef898f98ee531a26ed08e78546746cfae8ed412f32c9183acdaa02dadb1939bef2ce21fd0e6be83b09247204a1b15395ed7c0e8571e891d7608b1"
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC("localhost", httpClient))
        val paramSlot = CapturingSlot<String>()

        coEvery {
            httpClient.urlPost(any(), capture(paramSlot))
        } returns """{"jsonrpc":"2.0","id":1,"result":"0xeb8daa5cb17c571256ec9347638d8d7ea854748cbde355f66bca4c9f19f2cd47"}"""

        val txHash = rpc.sendRawTransaction(signedTx)

        assertThat(paramSlot.captured).isEqualTo("""{"method":"eth_sendRawTransaction","params":["0xf864028504a817c800825208941fcf8ff78ac5117d9c99b830c74b6668d6ac322901801ba007b829ef52383ef898f98ee531a26ed08e78546746cfae8ed412f32c9183acdaa02dadb1939bef2ce21fd0e6be83b09247204a1b15395ed7c0e8571e891d7608b1"],"id":1,"jsonrpc":"2.0"}""")
        assertThat(txHash).isEqualTo("0xeb8daa5cb17c571256ec9347638d8d7ea854748cbde355f66bca4c9f19f2cd47")
    }

    @Test
    fun `can parse error response for transaction`() = runBlocking {
        val signedTx =
            "0xf864018504a817c800825208941fcf8ff78ac5117d9c99b830c74b6668d6ac322901801ba02f984b0676d5361ca2b44af85b5c8e6aa93685446f1d1cd627584e54b9d577dea0155ebfaf5a3860c5c2c30dd4fe3a0123a00e7f3f52659f1ae78685622b4601cb"
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC("localhost", httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"error":{"code":-32000,"message":"nonce too low"}}"""

        coAssert {
            rpc.sendRawTransaction(signedTx)
        }.thrownError {
            isInstanceOf(JsonRpcException::class)
            hasMessage("nonce too low")
        }
    }

    @Test
    fun `can get transaction information`() = runBlocking {
        val txHash = "0xeb8daa5cb17c571256ec9347638d8d7ea854748cbde355f66bca4c9f19f2cd47"
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC(Networks.rinkeby.rpcUrl, httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"result":{"blockHash":"0x80150eaebb888a6e05aca72553f15190c9cc507e15b0995397f01b28d41a18c2","blockNumber":"0x4d4bef","from":"0x1fcf8ff78ac5117d9c99b830c74b6668d6ac3229","gas":"0x5208","gasPrice":"0x4a817c800","hash":"0xeb8daa5cb17c571256ec9347638d8d7ea854748cbde355f66bca4c9f19f2cd47","input":"0x","nonce":"0x2","r":"0x7b829ef52383ef898f98ee531a26ed08e78546746cfae8ed412f32c9183acda","s":"0x2dadb1939bef2ce21fd0e6be83b09247204a1b15395ed7c0e8571e891d7608b1","to":"0x1fcf8ff78ac5117d9c99b830c74b6668d6ac3229","transactionIndex":"0x1","v":"0x1b","value":"0x1"}}"""

        val txInfo = rpc.getTransactionByHash(txHash)

        val referenceTx = TransactionInformation(
            txHash = "0xeb8daa5cb17c571256ec9347638d8d7ea854748cbde355f66bca4c9f19f2cd47",
            nonce = BigInteger("2"),
            blockHash = "0x80150eaebb888a6e05aca72553f15190c9cc507e15b0995397f01b28d41a18c2",
            blockNumber = "0x4d4bef".hexToBigInteger(),
            transactionIndex = BigInteger.ONE,
            from = "0x1fcf8ff78ac5117d9c99b830c74b6668d6ac3229",
            to = "0x1fcf8ff78ac5117d9c99b830c74b6668d6ac3229",
            value = BigInteger.ONE,
            gas = BigInteger("21000"),
            gasPrice = BigInteger("20000000000"),
            input = byteArrayOf(),
            r = "0x7b829ef52383ef898f98ee531a26ed08e78546746cfae8ed412f32c9183acda".hexToBigInteger(),
            s = "0x2dadb1939bef2ce21fd0e6be83b09247204a1b15395ed7c0e8571e891d7608b1".hexToBigInteger(),
            v = 27.toBigInteger()
        )

        assertThat(txInfo).isEqualTo(referenceTx)
    }

    @Test
    fun `throws TransactionNotFoundException for unknown tx`() = runBlocking {
        val txHash = "0xaaaaaaaaaaaaaa1256ec9347638d8d7ea854748cbde355f66bca4c9f19f2cd47"
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC(Networks.rinkeby.rpcUrl, httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"result":null}"""

        coAssert {
            rpc.getTransactionByHash(txHash)
        }.thrownError {
            isInstanceOf(TransactionNotFoundException::class)
        }
    }

    @Test
    fun `throws JsonRpcException for bad tx hash`() = runBlocking {
        val txHash = "0x1FCf8ff78aC5117d9c99B830c74b6668D6AC3229"
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC(Networks.rinkeby.rpcUrl, httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"error":{"code":-32602,"message":"invalid argument 0: hex string has length 40, want 64 for common.Hash"}}"""

        coAssert {
            rpc.getTransactionByHash(txHash)
        }.thrownError {
            isInstanceOf(JsonRpcInvalidArgumentException::class)
        }
    }

    @Test
    fun `can get simple transaction receipt`() = runBlocking {
        val txHash = "0x5df3d9c995969917c5bc59341732c032c9d7f6e1118003cfd2cd439513a81d06"
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC(Networks.rinkeby.rpcUrl, httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"result":{"blockHash":"0xdb2c26224365059d3b95dd3572c7dd7bbe70436bc008aa4074bf3e421437a122","blockNumber":"0x44dd5c","contractAddress":null,"cumulativeGasUsed":"0xcfecc","from":"0xcf03dd0a894ef79cb5b601a43c4b25e3ae4c67ed","gasUsed":"0x11170","logs":[],"logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000","status":"0x0","to":"0xdca7ef03e98e0dc2b855be647c39abe984fcf21b","transactionHash":"0x5df3d9c995969917c5bc59341732c032c9d7f6e1118003cfd2cd439513a81d06","transactionIndex":"0x9"}}"""

        val receipt = rpc.getTransactionReceipt(txHash)

        assertThat(receipt).isEqualTo(
            TransactionReceipt(
                transactionHash = "0x5df3d9c995969917c5bc59341732c032c9d7f6e1118003cfd2cd439513a81d06",
                transactionIndex = 9.toBigInteger(),
                blockNumber = 4513116L.toBigInteger(),
                blockHash = "0xdb2c26224365059d3b95dd3572c7dd7bbe70436bc008aa4074bf3e421437a122",
                cumulativeGasUsed = 851660L.toBigInteger(),
                gasUsed = 70_000L.toBigInteger(),
                from = "0xcf03dd0a894ef79cb5b601a43c4b25e3ae4c67ed",
                to = "0xdca7ef03e98e0dc2b855be647c39abe984fcf21b",
                contractAddress = null,
                logs = emptyList(),
                logsBloom = "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                status = BigInteger.ZERO
            )
        )
    }

    @Test
    fun `can get transaction receipt with logs`() = runBlocking {
        val txHash = "0x16f847d99c47b90f34c494588a31874dc2da081b00ff34968a86d6f7d15863af"
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC(Networks.rinkeby.rpcUrl, httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"result":{"blockHash":"0xa237598ed026c2872ed8272a02d9fe8174a64b5bef9286cb37190a854fec3706","blockNumber":"0x44dd7f","contractAddress":null,"cumulativeGasUsed":"0x1b8f0c","from":"0xcf03dd0a894ef79cb5b601a43c4b25e3ae4c67ed","gasUsed":"0x1137a","logs":[{"address":"0xdca7ef03e98e0dc2b855be647c39abe984fcf21b","blockHash":"0xa237598ed026c2872ed8272a02d9fe8174a64b5bef9286cb37190a854fec3706","blockNumber":"0x44dd7f","data":"0x536563703235366b31566572696669636174696f6e4b6579323031380000000000000000000000000000000062d283fe6939c01fc88f02c6d2c9a547cc3e265600000000000000000000000000000000000000000000000000000000621f6e260000000000000000000000000000000000000000000000000000000000000000","logIndex":"0x8","removed":false,"topics":["0x5a5084339536bcab65f20799fcc58724588145ca054bd2be626174b27ba156f7","0x000000000000000000000000cf03dd0a894ef79cb5b601a43c4b25e3ae4c67ed"],"transactionHash":"0x16f847d99c47b90f34c494588a31874dc2da081b00ff34968a86d6f7d15863af","transactionIndex":"0x8"}],"logsBloom":"0x00000000000000000000000000000000000000000000000000000000800000000000000000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000040000040000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000002100000000000000000000000000000000000004000000000000000000000000000000","status":"0x1","to":"0xdca7ef03e98e0dc2b855be647c39abe984fcf21b","transactionHash":"0x16f847d99c47b90f34c494588a31874dc2da081b00ff34968a86d6f7d15863af","transactionIndex":"0x8"}}"""

        val receipt = rpc.getTransactionReceipt(txHash)

        assertThat(receipt).isEqualTo(
            TransactionReceipt(
                transactionHash = "0x16f847d99c47b90f34c494588a31874dc2da081b00ff34968a86d6f7d15863af",
                transactionIndex = "0x8".hexToBigInteger(),
                blockNumber = "0x44dd7f".hexToBigInteger(),
                blockHash = "0xa237598ed026c2872ed8272a02d9fe8174a64b5bef9286cb37190a854fec3706",
                cumulativeGasUsed = "0x1b8f0c".hexToBigInteger(),
                gasUsed = 70522L.toBigInteger(),
                contractAddress = null,
                from = "0xcf03dd0a894ef79cb5b601a43c4b25e3ae4c67ed",
                to = "0xdca7ef03e98e0dc2b855be647c39abe984fcf21b",
                logs = listOf(
                    JsonRpcLogItem(
                        address = "0xdca7ef03e98e0dc2b855be647c39abe984fcf21b",
                        topics = listOf(
                            "0x5a5084339536bcab65f20799fcc58724588145ca054bd2be626174b27ba156f7",
                            "0x000000000000000000000000cf03dd0a894ef79cb5b601a43c4b25e3ae4c67ed"
                        ),
                        data = "0x536563703235366b31566572696669636174696f6e4b6579323031380000000000000000000000000000000062d283fe6939c01fc88f02c6d2c9a547cc3e265600000000000000000000000000000000000000000000000000000000621f6e260000000000000000000000000000000000000000000000000000000000000000",
                        blockNumber = 4513151L.toBigInteger(),
                        transactionHash = "0x16f847d99c47b90f34c494588a31874dc2da081b00ff34968a86d6f7d15863af",
                        transactionIndex = 8.toBigInteger(),
                        blockHash = "0xa237598ed026c2872ed8272a02d9fe8174a64b5bef9286cb37190a854fec3706",
                        logIndex = 8.toBigInteger(),
                        removed = false
                    )
                ),
                logsBloom = "0x00000000000000000000000000000000000000000000000000000000800000000000000000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000040000040000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000002100000000000000000000000000000000000004000000000000000000000000000000",
                status = BigInteger.ONE
            )
        )
    }

    @Test
    fun `can get transaction receipt for contract deployment`() = runBlocking {
        val txHash = "0x2eb6cd86977d32df769f52ad89e571d58daab723a62abd9c2fe66d280f39148c"
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC(Networks.rinkeby.rpcUrl, httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"result":{"blockHash":"0x7b6b04db479a498a7608a1571e39d21a43003e77c4fac0885aacbc7c41c60ff3","blockNumber":"0x2d98c5","contractAddress":"0x74f06a973046999fcd3905461baf8cbce596668f","cumulativeGasUsed":"0x6814a","from":"0xa4cade6ecbed8f75f6fd50b8be92feb144400cc4","gasUsed":"0x49b44","logs":[],"logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000","status":"0x1","to":null,"transactionHash":"0x2eb6cd86977d32df769f52ad89e571d58daab723a62abd9c2fe66d280f39148c","transactionIndex":"0x3"}}"""

        val receipt = rpc.getTransactionReceipt(txHash)

        assertThat(receipt).isEqualTo(
            TransactionReceipt(
                transactionHash = "0x2eb6cd86977d32df769f52ad89e571d58daab723a62abd9c2fe66d280f39148c",
                transactionIndex = 3L.toBigInteger(),
                blockNumber = 2988229L.toBigInteger(),
                blockHash = "0x7b6b04db479a498a7608a1571e39d21a43003e77c4fac0885aacbc7c41c60ff3",
                cumulativeGasUsed = 426314L.toBigInteger(),
                gasUsed = 301892L.toBigInteger(),
                from = "0xa4cade6ecbed8f75f6fd50b8be92feb144400cc4",
                to = null,
                contractAddress = "0x74f06a973046999fcd3905461baf8cbce596668f",
                logs = emptyList(),
                logsBloom = "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                status = BigInteger.ONE
            )
        )
    }

    @Test
    fun `can get account balance`() = runBlocking {
        val address = "0xCdc585823Aac34f29eE04e3D33E5275415821c76"
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC(Networks.rinkeby.rpcUrl, httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"result":"0x182a0760fd20ca6"}"""

        val weiBalance = rpc.getAccountBalance(address)
        assertThat(weiBalance).isEqualTo(BigInteger("108825769942322342"))
    }

    @Test
    fun `can get transaction count`() = runBlocking {
        val address = "0x354DB1Be7e59c8274fbbc183939BbecF1991b521"
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC(Networks.rinkeby.rpcUrl, httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"result":"0x5"}"""

        val txCount = rpc.getTransactionCount(address)
        assertThat(txCount).isEqualTo(BigInteger("5"))
    }


    @Test
    fun `can get a gas price`() = runBlocking {
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC(Networks.rinkeby.rpcUrl, httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"result":"0x3b9aca00"}"""

        val gasPrice = rpc.getGasPrice()
        assertThat(gasPrice).isEqualTo(1_000_000_000L.toBigInteger())
    }

    @Test
    fun `can get logs`() = runBlocking {
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC(Networks.rinkeby.rpcUrl, httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"result":[{"address":"0xdca7ef03e98e0dc2b855be647c39abe984fcf21b","blockHash":"0xa237598ed026c2872ed8272a02d9fe8174a64b5bef9286cb37190a854fec3706","blockNumber":"0x44dd7f","data":"0x536563703235366b31566572696669636174696f6e4b6579323031380000000000000000000000000000000062d283fe6939c01fc88f02c6d2c9a547cc3e265600000000000000000000000000000000000000000000000000000000621f6e260000000000000000000000000000000000000000000000000000000000000000","logIndex":"0x8","removed":false,"topics":["0x5a5084339536bcab65f20799fcc58724588145ca054bd2be626174b27ba156f7","0x000000000000000000000000cf03dd0a894ef79cb5b601a43c4b25e3ae4c67ed"],"transactionHash":"0x16f847d99c47b90f34c494588a31874dc2da081b00ff34968a86d6f7d15863af","transactionIndex":"0x8"}]}"""

        val logs = rpc.getLogs(
            address = "0xdca7ef03e98e0dc2b855be647c39abe984fcf21b",
            topics = listOf(
                null,
                "0x000000000000000000000000cf03dd0a894ef79cb5b601a43c4b25e3ae4c67ed"
            ),
            fromBlock = 4513151L.toBigInteger(),
            toBlock = 4513151L.toBigInteger()
        )

        assertThat(logs.size).isEqualTo(1)

        assertThat(logs[0]).isEqualTo(
            JsonRpcLogItem(
                address = "0xdca7ef03e98e0dc2b855be647c39abe984fcf21b",
                topics = listOf(
                    "0x5a5084339536bcab65f20799fcc58724588145ca054bd2be626174b27ba156f7",
                    "0x000000000000000000000000cf03dd0a894ef79cb5b601a43c4b25e3ae4c67ed"
                ),
                data = "0x536563703235366b31566572696669636174696f6e4b6579323031380000000000000000000000000000000062d283fe6939c01fc88f02c6d2c9a547cc3e265600000000000000000000000000000000000000000000000000000000621f6e260000000000000000000000000000000000000000000000000000000000000000",
                blockNumber = 4513151L.toBigInteger(),
                transactionHash = "0x16f847d99c47b90f34c494588a31874dc2da081b00ff34968a86d6f7d15863af",
                transactionIndex = 8.toBigInteger(),
                blockHash = "0xa237598ed026c2872ed8272a02d9fe8174a64b5bef9286cb37190a854fec3706",
                logIndex = 8.toBigInteger(),
                removed = false
            )
        )
    }

    @Test
    fun `can execute an eth_call`() = runBlocking {
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC(Networks.rinkeby.rpcUrl, httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"result":"0x000000000000000000000000000000000000000000000000000000000044dd7f"}"""

        val result = rpc.ethCall(
            address = "0xdca7ef03e98e0dc2b855be647c39abe984fcf21b",
            data = "0xf96d0f9f000000000000000000000000cf03dd0a894ef79cb5b601a43c4b25e3ae4c67ed"
        )

        assertThat(result).isEqualTo("0x000000000000000000000000000000000000000000000000000000000044dd7f")
    }


    @Test
    fun `can getChain ID`() = runBlocking {
        val httpClient = mockk<HttpClient>()
        val rpc = spyk(JsonRPC(Networks.rinkeby.rpcUrl, httpClient))

        coEvery {
            httpClient.urlPost(any(), any())
        } returns """{"jsonrpc":"2.0","id":1,"result":"0x4"}"""

        val result = rpc.getChainId()

        assertThat(result).isEqualTo(4.toBigInteger())
    }
}