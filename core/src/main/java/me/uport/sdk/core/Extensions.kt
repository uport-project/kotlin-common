@file:Suppress("unused", "TooManyFunctions", "MagicNumber")

package me.uport.sdk.core

import org.kethereum.extensions.hexToBigInteger
import org.komputing.khex.extensions.clean0xPrefix
import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.prepend0xPrefix
import org.komputing.khex.model.HexString
import org.spongycastle.util.encoders.Base64

fun String.clean0xPrefix() = HexString(this).clean0xPrefix().string
fun String.prepend0xPrefix() = HexString(this).prepend0xPrefix().string
fun String.hexToBigInteger() = HexString(this).hexToBigInteger()
fun String.hexToByteArray() = HexString(this).hexToByteArray()

//using spongy castle implementation because the android one can't be used properly in tests
/**
 * Creates a base64 representation of the given byteArray, without padding
 */
fun ByteArray.toBase64(): String = Base64.toBase64String(this).replace("=", "")

/**
 * Creates a base64 representation of the byteArray that backs this string, without padding
 */
fun String.toBase64() = this.toByteArray().toBase64()

/**
 * pads a base64 string with a proper number of '='
 */
fun String.padBase64() = this.padEnd(this.length + (4 - this.length % 4) % 4, '=')

/**
 * Converts the bytes of this string into a base64 string usable in a URL
 */
fun String.toBase64UrlSafe() = this.toBase64().replace('+', '-').replace('/', '_')

/**
 * Converts this byte array into a base64 string usable in a URL
 */
fun ByteArray.toBase64UrlSafe() = this.toBase64().replace('+', '-').replace('/', '_')

/**
 * Decodes a base64 string into a bytearray.
 * Supports unpadded and url-safe strings as well.
 */
fun String.decodeBase64(): ByteArray = this
    //force non-url safe and add padding so that it can be applied to all b64 formats
    .replace('-', '+')
    .replace('_', '/')
    .padBase64()
    .let {
        if (it.isEmpty())
            byteArrayOf()
        else
            Base64.decode(it)
    }
