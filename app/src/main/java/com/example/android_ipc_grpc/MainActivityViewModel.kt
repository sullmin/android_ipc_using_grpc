package com.example.android_ipc_grpc

import AuthenticationServiceGrpcKt
import AuthenticationServiceOuterClass
import IpcCoreGrpcKt
import IpcCoreOuterClass
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_ipc_grpc.ui.models.UiMessage
import com.example.android_ipc_grpc.utils.toByteString
import com.example.android_ipc_grpc.utils.toLocalDateTime
import com.example.android_ipc_grpc.utils.toUUID
import com.google.protobuf.ByteString
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

class MainActivityViewModel : ViewModel() {
    private val channel: ManagedChannel by lazy {
        ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build()
    }
    private val massagingStub: IpcCoreGrpcKt.IpcCoreCoroutineStub by lazy {
        IpcCoreGrpcKt.IpcCoreCoroutineStub(channel)
    }
    private val authenticationStub: AuthenticationServiceGrpcKt.AuthenticationServiceCoroutineStub by lazy {
        AuthenticationServiceGrpcKt.AuthenticationServiceCoroutineStub(channel)
    }
    val messageQueue: MutableStateFlow<List<UiMessage>> = MutableStateFlow(listOf())
    val message: MutableStateFlow<String> = MutableStateFlow("")

    private fun getTemporaryIdentifier(pkg: String): UUID = when {
        pkg.endsWith("1") -> UUID.fromString("6919b702-9cec-445f-8678-eea4e2da912f")
        else -> UUID.fromString("50f15c64-49ed-4f66-b456-a81c4ffa926c")
    }

    suspend fun authenticate() {
        try {
            val securitySystem = SecuritySystem()
            val pbKey = securitySystem.encodedPublicKey.let { ByteString.copyFrom(it) }
            val requestRegisterDevice =
                AuthenticationServiceOuterClass.RegisterDeviceRequest.newBuilder()
                    .setPublicKey(pbKey)
                    .build()
            val deviceId = authenticationStub.registerDevice(requestRegisterDevice).device.toUUID()
            val requestExercise =
                AuthenticationServiceOuterClass.GenerateExerciseRequest.newBuilder()
                    .setDevice(deviceId.toByteString())
                    .build()
            val exercise =
                authenticationStub.generateExercise(requestExercise).signedMessage.toByteArray()
            val encodedMsg = securitySystem.decrypt(exercise).let { ByteString.copyFrom(it) }
            val requestResponse =
                AuthenticationServiceOuterClass.ResolveExerciseRequest.newBuilder()
                    .setDevice(deviceId.toByteString())
                    .setRawMessage(encodedMsg)
                    .build()
            val response = authenticationStub.resolveExercise(requestResponse)
            Log.e("DEBUG", "token -> ${response.token}")
        } catch (e: Throwable) {
            Log.e("DEBUG", "Throw exception $e")
            e.printStackTrace()
        }
    }

    suspend fun sendMessage(pkg: String) {
        val msg = message.value.ifBlank { null } ?: return
        val request = IpcCoreOuterClass.SendMessageRequest.newBuilder()
            .setMessage(msg)
            .setTemporaryIdentifier(
                getTemporaryIdentifier(pkg).toByteString()
            )
            .build()
        message.value = ""
        massagingStub.sendMessage(request)
    }

    fun subscribe(pkg: String) {
        viewModelScope.launch {
            val request = IpcCoreOuterClass.SubscribeRequest.newBuilder().build()
            massagingStub.subscribe(request).collect {
                var previousCreatedAt: LocalDateTime? = null
                messageQueue.value = it.messagesList.map { messageIt ->
                    val currentLocalDateTime = messageIt.createdAt.toLocalDateTime()
                    val uiMessage = UiMessage(
                        message = messageIt.message,
                        sendAt = currentLocalDateTime,
                        isOwner = when (messageIt.source.toUUID()) {
                            getTemporaryIdentifier(pkg) -> UiMessage.OwnerType.ME
                            else -> UiMessage.OwnerType.OTHER
                        },
                        isMessageGroup = computeGroupMassage(
                            previousCreatedAt,
                            currentLocalDateTime
                        )
                    )
                    previousCreatedAt = currentLocalDateTime
                    uiMessage
                }
            }
        }
    }

    private fun computeGroupMassage(prev: LocalDateTime?, current: LocalDateTime): Boolean {
        val minutes = prev?.let {
            Duration.between(it, current)
        }?.toMinutes() ?: Long.MAX_VALUE

        return minutes <= 10
    }

    override fun onCleared() {
        super.onCleared()
        channel.shutdown()
    }
}