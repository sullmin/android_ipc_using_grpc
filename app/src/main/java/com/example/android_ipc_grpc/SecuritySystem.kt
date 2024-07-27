package com.example.android_ipc_grpc

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.Cipher


class SecuritySystem {
    companion object {
        private const val ALIAS_KEYSTORE = "A1234567890B12"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val ALGORITHM_KEY_STORE = "RSA"
        private const val ALGORITHM_CIPHER = "RSA/ECB/PKCS1Padding"
        private const val KEY_SIZE = 4096
        const val BLOCK_SIZE = KEY_SIZE / 8 - 11
    }

    private lateinit var keys: KeyPair

    init {
        val keyLoaded = initKeyPair()

        if (!keyLoaded) {
            generateKeyPair()
        }
    }

    private fun initKeyPair(): Boolean {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        val privateKeyEntry = keyStore.getEntry(ALIAS_KEYSTORE, null) as? KeyStore.PrivateKeyEntry
        val privateKey = privateKeyEntry?.privateKey
        val publicKey = privateKeyEntry?.certificate?.publicKey

        return if (privateKey != null && publicKey != null) {
            keys = KeyPair(publicKey, privateKey)
            true
        } else {
            false
        }
    }

    private fun generateKeyPair() {
        val parameterSpec = KeyGenParameterSpec.Builder(
            ALIAS_KEYSTORE,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            setKeySize(KEY_SIZE)
        }.build()
        val keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_KEY_STORE, ANDROID_KEYSTORE)

        keyPairGenerator.initialize(parameterSpec)
        keys = keyPairGenerator.genKeyPair()
    }

    fun encrypt(message: ByteArray): ByteArray {
        return Cipher.getInstance(ALGORITHM_CIPHER).run {
            init(Cipher.ENCRYPT_MODE, keys.public)
            doFinal(message)
        }
    }

    fun decrypt(encryptedMessage: ByteArray): ByteArray {
        return Cipher.getInstance(ALGORITHM_CIPHER).run {
            init(Cipher.DECRYPT_MODE, keys.private)
            doFinal(encryptedMessage)
        }
    }

    val encodedPublicKey: ByteArray
        get() {
            return keys.public.encoded
        }
}