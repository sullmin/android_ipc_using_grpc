package com.example.android_ipc_grpc.ipc.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class JwtSecurity {
    companion object {
        private const val ALIAS_KEYSTORE = "JwtSecurity"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_SIZE = 256
    }

    private lateinit var secretKey: SecretKey
    val secret: SecretKey
        get() {
            return secretKey
        }

    init {
        val init = initKey()

        if (!init) {
            generateKey()
        }
    }

    private fun generateKey() {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, ANDROID_KEYSTORE)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                ALIAS_KEYSTORE,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
                .setKeySize(KEY_SIZE)
                .build()
        )
        secretKey = keyGenerator.generateKey()
    }

    private fun initKey(): Boolean = try {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        secretKey = keyStore.getKey(ALIAS_KEYSTORE, null) as SecretKey
        true
    } catch (e: Throwable) {
        false
    }
}