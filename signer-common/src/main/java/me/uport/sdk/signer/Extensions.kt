@file:Suppress("unused", "TooManyFunctions", "MagicNumber")

package me.uport.sdk.signer

import me.uport.sdk.core.decodeBase64
import me.uport.sdk.core.padBase64
import me.uport.sdk.core.toBase64
import me.uport.sdk.core.toBase64UrlSafe
import org.kethereum.crypto.decompressKey
import org.kethereum.extensions.toBigInteger
import org.kethereum.extensions.toBytesPadded
import org.kethereum.extensions.toHexStringNoPrefix
import org.kethereum.model.ECKeyPair
import org.kethereum.model.PRIVATE_KEY_SIZE
import org.kethereum.model.PUBLIC_KEY_SIZE
import org.kethereum.model.PublicKey
import org.kethereum.model.SignatureData
import org.komputing.khex.extensions.clean0xPrefix
import org.komputing.khex.extensions.prepend0xPrefix
import org.komputing.khex.extensions.toNoPrefixHexString
import org.spongycastle.asn1.ASN1EncodableVector
import org.spongycastle.asn1.ASN1Encoding
import org.spongycastle.asn1.ASN1Integer
import org.spongycastle.asn1.DERSequence
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.Charset

/**
 * Converts a hex string to another hex string pre-padded with zeroes until it represents at least 32 bytes
 */
fun String.hexToBytes32() = clean0xPrefix().padStart(64, '0').prepend0xPrefix()

/**
 * Converts this BigInteger into a hex string that is represented by at least 32 bytes.
 * The hex representation will be left-padded with zeroes if needed
 */
fun BigInteger.toBytes32String() = toHexStringNoPrefix().padStart(64, '0').prepend0xPrefix()

/**
 * shorthand to use the utf8 Charset
 */
val utf8: Charset = Charset.forName("UTF-8")

/**
 * interprets the underlying ByteArray as String
 */
fun ByteArray.bytes32ToString() = this.toString(utf8)


/**
 * size(in bytes) of the R and S components of an EC signature
 */
const val SIG_COMPONENT_SIZE = 32

/**
 * total size (bytes) of a non-recoverable signature
 */
const val SIG_SIZE = SIG_COMPONENT_SIZE * 2

/**
 * total size (bytes) of a recoverable signature
 */
const val SIG_RECOVERABLE_SIZE = SIG_SIZE + 1

/**
 * The number of bytes needed to represent an uncompressed public key, including prefix.
 * By default this is 65
 */
const val UNCOMPRESSED_PUBLIC_KEY_SIZE = PUBLIC_KEY_SIZE + 1

/**
 * The number of bytes needed to represent a compressed public key, including prefix.
 * By default this is 33
 */
const val COMPRESSED_PUBLIC_KEY_SIZE = PRIVATE_KEY_SIZE + 1

/**
 * Returns the JOSE encoding of the standard signature components (joined by empty string)
 *
 * @param recoverable If this is true then the buffer returned gets an extra byte with the
 *          recovery param shifted back to [0, 1] ( as opposed to [27,28] )
 */
fun SignatureData.getJoseEncoded(recoverable: Boolean = false): String {
    val size = if (recoverable)
        SIG_RECOVERABLE_SIZE
    else
        SIG_SIZE

    val bos = ByteArrayOutputStream(size)
    bos.write(this.r.toBytesPadded(SIG_COMPONENT_SIZE))
    bos.write(this.s.toBytesPadded(SIG_COMPONENT_SIZE))
    if (recoverable) {
        bos.write(byteArrayOf((this.v.toByte() - 27).toByte()))
    }
    return bos.toByteArray().toBase64UrlSafe()
}

/**
 * Decodes a JOSE encoded signature string.
 * @param recoveryParam can be used in case the signature is non recoverable to be added as recovery byte
 */
fun String.decodeJose(recoveryParam: Byte = 27): SignatureData =
    this.decodeBase64().decodeJose(recoveryParam)

/**
 * Decodes a JOSE encoded signature ByteArray.
 * @param recoveryParam can be used in case the signature is non recoverable to be added as recovery byte
 */
fun ByteArray.decodeJose(recoveryParam: Byte = 27): SignatureData {
    val rBytes = this.copyOfRange(0, SIG_COMPONENT_SIZE)
    val sBytes = this.copyOfRange(SIG_COMPONENT_SIZE, SIG_SIZE)
    val v = if (this.size > SIG_SIZE)
        this[SIG_SIZE].let {
            if (it < 27) (it + 27).toByte() else it
        }
    else
        recoveryParam
    return SignatureData(BigInteger(1, rBytes), BigInteger(1, sBytes), v.toInt().toBigInteger())
}

/**
 * Returns the DER encoding of the standard signature components
 */
@Deprecated("This method will be removed in the next major release")
fun SignatureData.getDerEncoded(): String {

    val v = ASN1EncodableVector()
    v.add(ASN1Integer(this.r))
    v.add(ASN1Integer(this.s))
    return DERSequence(v)
        .getEncoded(ASN1Encoding.DER)
        .toNoPrefixHexString()
}

/**
 * Decompresses the public key of this pair and returns the uncompressed version, including prefix
 */
fun ECKeyPair.getUncompressedPublicKeyWithPrefix(): ByteArray {
    val pubBytes = this.publicKey.normalize().key.toBytesPadded(UNCOMPRESSED_PUBLIC_KEY_SIZE)
    pubBytes[0] = 0x04
    return pubBytes
}

/**
 * Returns the uncompressed version of this publicKey, including prefix
 */
fun PublicKey.getUncompressedPublicKeyWithPrefix(): ByteArray {
    val pubBytes = this.normalize().key.toBytesPadded(UNCOMPRESSED_PUBLIC_KEY_SIZE)
    pubBytes[0] = 0x04
    return pubBytes
}

/**
 * Transforms a PublicKey into its normalized version which is decompressed and has no prefix
 */
fun PublicKey.normalize(): PublicKey {
    val pubBytes = this.key.toByteArray()
    val normalizedBytes = when (pubBytes.size) {
        UNCOMPRESSED_PUBLIC_KEY_SIZE -> pubBytes.copyOfRange(1, pubBytes.size)
        COMPRESSED_PUBLIC_KEY_SIZE -> decompressKey(pubBytes)
        else -> pubBytes
    }
    return PublicKey(normalizedBytes.toBigInteger())
}

/**
 * Encodes a BigInteger as a base64 string of a fixed size ByteArray.
 * The [keySize] defaults to 32 bytes (the size of a private key)
 *
 * This is useful for situations when the byte representation of the key starts with zeroes
 * so a direct transformation would yield shorter ByteArray
 */
fun BigInteger.keyToBase64(keySize: Int = PRIVATE_KEY_SIZE): String =
    this.toBytesPadded(keySize).toBase64().padBase64()
