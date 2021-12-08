package org.cyanotic.butterfly.protocol.security

import Base64
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.floor

class TicketEncoder {

    fun encode(key: String, nonce: String): String {
        // now, we have to create a first, base64 decoded key, which we get from the input key
        val key1: ByteArray = Base64.decode(key)

        // then we calculate a second key through a specific algorithm (see function DeriveKey())
        val key2: ByteArray = deriveKey(key1, "WS-SecureConversationSESSION KEY HASH")

        // ...and a third key with the same algorithm...
        val key3: ByteArray = deriveKey(key1, "WS-SecureConversationSESSION KEY ENCRYPTION")

        // compute the hash
        val hash: ByteArray = hmac(key2, nonce.toByteArray())!!

        // fill random iv
        val iv = ByteArray(8)
        for (i in 0..7) {
            val rand = floor(Math.random() * 256).toInt().toByte()
            iv[i] = rand
        }

        // we have to fill the nonce with 8*8
        val restOfNonce = byteArrayOf(0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08)

        val output: ByteArray = des3(key3, combine(nonce.toByteArray(), restOfNonce), iv)!!

        // the final key will be a base64 encoded structure, composed by the beginning of the structure, the initialization vector, the SHA1 - Hash and the transformed block
        // string struc = Encoding.Default.GetString(Beginning) + Encoding.Default.GetString(iv) + Encoding.Default.GetString(hash) + Encoding.Default.GetString(output);
        // StructHeaderSize = 28
        // CryptMode = 1
        // CypherType = 0x6603
        // HashType = 0x8004
        // IVLength = 8
        // Hash length = 20
        // Cipher length = 72
        val beginning = byteArrayOf(
            0x1c, 0x00, 0x00, 0x00,  // StructHeaderSize = 28
            0x01, 0x00, 0x00, 0x00,  // CryptMode = 1
            0x03, 0x66, 0x00, 0x00,  // CypherType = 0x6603
            0x04, 0x80.toByte(), 0x00, 0x00,  // HashType = 0x8004
            0x08, 0x00, 0x00, 0x00,  // IVLength = 8
            0x14, 0x00, 0x00, 0x00,  // Hash length = 20
            0x48, 0x00, 0x00, 0x00 // Cipher length = 72
        )
        val struc = String(beginning, StandardCharsets.ISO_8859_1) + String(iv, StandardCharsets.ISO_8859_1) + String(
            hash,
            StandardCharsets.ISO_8859_1
        ) + String(output, StandardCharsets.ISO_8859_1)


        return String(Base64.encode(struc.toByteArray(StandardCharsets.ISO_8859_1)))
    }

    // combine two byte arrays
    private fun combine(a: ByteArray?, b: ByteArray): ByteArray {
        val c = ByteArray(a!!.size + b.size)
        System.arraycopy(a, 0, c, 0, a.size)
        System.arraycopy(b, 0, c, a.size, b.size)
        return c
    }

    // specific algorithm to calculate a key...
    private fun deriveKey(key: ByteArray, magic: String): ByteArray {
        val hash1 = hmac(key, magic.toByteArray())
        val hash2 = hmac(key, combine(hash1, magic.toByteArray()))
        val hash3 = hmac(key, hash1)
        val hash4 = hmac(key, combine(hash3, magic.toByteArray()))
        val out = ByteArray(4)
        out[0] = hash4!![0]
        out[1] = hash4[1]
        out[2] = hash4[2]
        out[3] = hash4[3]
        return combine(hash2, out)
    }

    private fun hmac(key: ByteArray, subject: ByteArray?): ByteArray? {
        try {
            val mac = Mac.getInstance("HmacSHA1")
            val sk = SecretKeySpec(key, "HmacSHA1")
            mac.init(sk)
            return mac.doFinal(subject)
        } catch (ex: NoSuchAlgorithmException) {
            ex.printStackTrace()
        } catch (ex: InvalidKeyException) {
            ex.printStackTrace()
        }
        return null
    }

    private fun des3(key: ByteArray, subject: ByteArray, iv: ByteArray): ByteArray? {
        try {
            val cipher = Cipher.getInstance("DESede/CBC/NoPadding")
            val sk = SecretKeySpec(key, "DESede")
            val sr = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, sk, sr)
            return cipher.doFinal(subject)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }
}