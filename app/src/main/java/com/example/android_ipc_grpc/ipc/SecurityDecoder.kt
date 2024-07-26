package com.example.android_ipc_grpc.ipc

import android.util.Log
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class SecurityDecoder(private val rawPublicKey: ByteArray) {
    companion object {
        private const val ALGORITHM = "RSA"
    }


    init {
    }

    fun compareMessage(
        signedMessage: ByteArray,
        rawMessage: ByteArray
    ): Boolean {
        return try {
            val keySpec = X509EncodedKeySpec(rawPublicKey)
            val keyFactory = KeyFactory.getInstance(ALGORITHM)
            val publicKey = keyFactory.generatePublic(keySpec)

            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, publicKey)

            val cipherMessage = cipher.doFinal(signedMessage)

            Log.e("DEBUG", "raw ${rawMessage.size} - ${rawMessage.toString(Charsets.UTF_8)}")
            Log.e(
                "DEBUG",
                "cipher ${cipherMessage.size} - ${cipherMessage.toString(Charsets.UTF_8)}"
            )
            rawMessage.contentEquals(cipherMessage)
        } catch (e: Throwable) {
            Log.e("DEBUG", "ERROR $e")
            throw e
        }
    }
}