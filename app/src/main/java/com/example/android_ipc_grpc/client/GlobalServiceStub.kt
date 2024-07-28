package com.example.android_ipc_grpc.client

import AuthenticationServiceGrpcKt
import IpcCoreGrpcKt
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

class GlobalServiceStub(token: String? = null) {
    companion object {
        private const val PORT = 8080
        private const val NAME = "localhost"
    }

    private val channel: ManagedChannel by lazy {
        ManagedChannelBuilder.forAddress(NAME, PORT)
            .let {
                if (!token.isNullOrBlank()) {
                    it.intercept(
                        BearerInterceptor(token)
                    )
                } else {
                    it
                }
            }
            .usePlaintext()
            .build()
    }
    val massagingStub: IpcCoreGrpcKt.IpcCoreCoroutineStub by lazy {
        IpcCoreGrpcKt.IpcCoreCoroutineStub(channel)
    }
    val authenticationStub: AuthenticationServiceGrpcKt.AuthenticationServiceCoroutineStub by lazy {
        AuthenticationServiceGrpcKt.AuthenticationServiceCoroutineStub(channel)
    }

    fun shutdown() {
        channel.shutdownNow()
    }
}