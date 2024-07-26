package com.example.android_ipc_grpc

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature


class SecuritySystem {
    companion object {
        private const val ALIAS_KEYSTORE = "SecuritySystemIpcGrpc_7"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val ALGORITHM = "SHA256withRSA"
    }

    private lateinit var keys: KeyPair

    val publicKey: ByteArray?
        get() {
            return keys.public?.encoded
        }

    init {
        val keyLoaded = initKeyPair()

        if (!keyLoaded) {
            generateKeyPair()
        }
    }

    private fun initKeyPair(): Boolean {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        if (!keyStore.containsAlias(ALIAS_KEYSTORE)) {
            return false
        }

        val entry = keyStore.getEntry(ALIAS_KEYSTORE, null)
        val certificate = keyStore.getCertificate(ALIAS_KEYSTORE)

        if (entry !is KeyStore.PrivateKeyEntry || certificate == null) {
            return false
        }

        keys = KeyPair(certificate.publicKey, entry.privateKey)
        return true
    }

    private fun generateKeyPair() {
        val parameterSpec = KeyGenParameterSpec.Builder(
            ALIAS_KEYSTORE,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            build()
        }

        keys = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE).let {
            it.initialize(parameterSpec)
            it.generateKeyPair()
        }
    }

    fun signMessage(message: ByteArray): ByteArray? {
        return Signature.getInstance(ALGORITHM).run {
            initSign(keys.private)
            update(message)
            sign()
        }
    }
}