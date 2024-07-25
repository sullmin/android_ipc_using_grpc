package com.example.android_ipc_grpc.ipc

import android.util.Log
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

class SecurityDecoder(private val rawPublicKey: ByteArray) {
    companion object {
        private const val ALGORITHM = "SHA256withECDSA"
    }


    init {
    }

    fun compareMessage(
        signedMessage: ByteArray,
        rawMessage: ByteArray
    ): Boolean {
        return try {
            val keySpec = X509EncodedKeySpec(rawPublicKey)
            val keyFactory = KeyFactory.getInstance("EC")
            val publicKey = keyFactory.generatePublic(keySpec)

            Signature.getInstance(ALGORITHM).run {
                initVerify(publicKey)
                update(signedMessage)
                verify(rawMessage)
            }
        } catch (e: Throwable) {
            Log.e("DEBUG", "ERROR $e")
            throw e
        }
    }
}