package com.example.android_ipc_grpc

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import kotlin.io.encoding.ExperimentalEncodingApi


class SecuritySystem {
    companion object {
        private const val ALIAS_KEYSTORE = "A1234567890B12"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val ALGORITHM_KEY_STORE = "RSA"
        private const val ALGORITHM_CIPHER = "RSA/ECB/PKCS1Padding"
        private const val KEY_SIZE = 4096
        const val BLOCK_SIZE = KEY_SIZE / 8 - 11
    }

    lateinit var keys: KeyPair

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

        Log.e("DEBUG", "KeyStore ${privateKey != null} && ${publicKey != null}")
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

    @OptIn(ExperimentalEncodingApi::class)
    fun encrypt(message: ByteArray, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM_CIPHER)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(message)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun decrypt(encryptedMessage: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM_CIPHER)

        cipher.init(Cipher.DECRYPT_MODE, keys.private)
        return cipher.doFinal(encryptedMessage)
    }

    fun regenKeyFromBytes(publicKeyBytes: ByteArray): PublicKey {
        val keySpec = X509EncodedKeySpec(publicKeyBytes)
        val keyFactory = KeyFactory.getInstance(ALGORITHM_KEY_STORE)

        return keyFactory.generatePublic(keySpec)
    }
}

/*class SecuritySystemTest {
    fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.genKeyPair()
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun encrypt(message: String, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(message.toByteArray(Charsets.UTF_8))
        return Base64.encode(encryptedBytes)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun decrypt(encryptedMessage: String, privateKey: PrivateKey): String {
        val bytes = Base64.decode(encryptedMessage)
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = cipher.doFinal(bytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    fun regenKeyFromBytes(publicKeyBytes: ByteArray): PublicKey {
        val keySpec = X509EncodedKeySpec(publicKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePublic(keySpec)
    }
}*/