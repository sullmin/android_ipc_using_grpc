package com.example.android_ipc_grpc.ipc.security

import android.util.Log
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class PublicKeySecurity(
    publicKeyEncoded: ByteArray
) {
    companion object {
        private const val ALGORITHM_KEY_STORE = "RSA"
        private const val ALGORITHM_CIPHER = "RSA/ECB/PKCS1Padding"
        private const val KEY_SIZE = 4096
        const val BLOCK_SIZE = KEY_SIZE / 8 - 11
    }

    private val publicKey: PublicKey

    init {
        publicKey = regenPublicKeyFromBytes(publicKeyEncoded)
    }

    private fun regenPublicKeyFromBytes(publicKeyBytes: ByteArray): PublicKey {
        val keySpec = X509EncodedKeySpec(publicKeyBytes)
        return KeyFactory.getInstance(ALGORITHM_KEY_STORE).generatePublic(keySpec)
    }

    fun encrypt(message: ByteArray): ByteArray? = try {
        Cipher.getInstance(ALGORITHM_CIPHER).run {
            init(Cipher.ENCRYPT_MODE, publicKey)
            doFinal(message)
        }
    } catch (e: Throwable) {
        Log.e("SecurityKeyManager", "SecurityKeyManager.decode() throw $e")
        null
    }
}