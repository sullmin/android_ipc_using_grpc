package com.example.android_ipc_grpc.ipc

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class JwtSecurity {
    lateinit var secretKey: SecretKey

    init {
        val init = initKey()

        if (!init) {
            generateKey()
        }
    }

    private fun generateKey() {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, "AndroidKeyStore")
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                "HMACKey",
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
                .setKeySize(256)
                .build()
        )
        secretKey = keyGenerator.generateKey()
    }

    fun initKey(): Boolean = try {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        secretKey = keyStore.getKey("HMACKey", null) as SecretKey
        true
    } catch (e: Throwable) {
        false
    }
}