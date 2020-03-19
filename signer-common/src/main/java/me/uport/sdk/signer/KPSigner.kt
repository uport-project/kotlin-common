package me.uport.sdk.signer

import me.uport.sdk.core.hexToBigInteger
import org.kethereum.crypto.signMessage
import org.kethereum.crypto.signMessageHash
import org.kethereum.crypto.toAddress
import org.kethereum.crypto.toECKeyPair
import org.kethereum.model.PrivateKey
import org.kethereum.model.SignatureData
import org.komputing.khash.sha256.extensions.sha256

/**
 * Simple [Signer] implementation that holds the KeyPair in memory.
 *
 * There is no special handling of threads for callbacks.
 */
@Suppress("TooGenericExceptionCaught")
class KPSigner(privateKey: String) : Signer {

    private val keyPair = PrivateKey(privateKey.hexToBigInteger()).toECKeyPair()

    override fun signJWT(
        rawPayload: ByteArray,
        callback: (err: Exception?, sigData: SignatureData) -> Unit
    ) {
        try {
            val sigData = signMessageHash(rawPayload.sha256(), keyPair, false)
            callback(null, sigData)
        } catch (err: Exception) {
            callback(err, SignatureData())
        }
    }

    override fun getAddress() = keyPair.toAddress().hex

    override fun signETH(
        rawMessage: ByteArray,
        callback: (err: Exception?, sigData: SignatureData) -> Unit
    ) {

        try {
            val sigData = keyPair.signMessage(rawMessage)
            callback(null, sigData)
        } catch (ex: Exception) {
            callback(ex, SignatureData())
        }

    }
}
