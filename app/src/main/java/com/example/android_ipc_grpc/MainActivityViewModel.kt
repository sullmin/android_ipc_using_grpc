package com.example.android_ipc_grpc

import AuthenticationServiceOuterClass
import IpcCoreOuterClass
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_ipc_grpc.client.GlobalServiceStub
import com.example.android_ipc_grpc.ui.models.UiMessage
import com.example.android_ipc_grpc.utils.toByteString
import com.example.android_ipc_grpc.utils.toLocalDateTime
import com.example.android_ipc_grpc.utils.toUUID
import com.google.protobuf.ByteString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

class MainActivityViewModel : ViewModel() {
    private var globalApi: GlobalServiceStub = GlobalServiceStub()
    private val me: MutableStateFlow<UUID> = MutableStateFlow(
        UUID.fromString("00000000-0000-0000-0000-000000000000")
    )

    val messageQueue: MutableStateFlow<List<UiMessage>> = MutableStateFlow(listOf())
    val message: MutableStateFlow<String> = MutableStateFlow("")

    suspend fun authenticate() {
        try {
            val securitySystem = SecuritySystem()
            val pbKey = securitySystem.encodedPublicKey.let { ByteString.copyFrom(it) }
            val requestRegisterDevice =
                AuthenticationServiceOuterClass.RegisterDeviceRequest.newBuilder()
                    .setPublicKey(pbKey)
                    .build()
            me.value =
                globalApi.authenticationStub.registerDevice(requestRegisterDevice).device.toUUID()
            val requestExercise =
                AuthenticationServiceOuterClass.GenerateExerciseRequest.newBuilder()
                    .setDevice(me.value.toByteString())
                    .build()
            val exercise =
                globalApi.authenticationStub.generateExercise(requestExercise).signedMessage.toByteArray()
            val encodedMsg = securitySystem.decrypt(exercise).let { ByteString.copyFrom(it) }
            val requestResponse =
                AuthenticationServiceOuterClass.ResolveExerciseRequest.newBuilder()
                    .setDevice(me.value.toByteString())
                    .setRawMessage(encodedMsg)
                    .build()
            val token = globalApi.authenticationStub.resolveExercise(requestResponse).token

            globalApi.shutdown()
            globalApi = GlobalServiceStub(token)
            Log.e("DEBUG", "token -> $token")
        } catch (e: Throwable) {
            Log.e("DEBUG", "Throw exception $e")
            e.printStackTrace()
        }
    }

    suspend fun sendMessage() {
        val msg = message.value.ifBlank { null } ?: return
        val request = IpcCoreOuterClass.SendMessageRequest.newBuilder()
            .setMessage(msg)
            .build()
        message.value = ""
        globalApi.massagingStub.sendMessage(request)
    }

    fun subscribe() {
        viewModelScope.launch {
            val request = IpcCoreOuterClass.SubscribeRequest.newBuilder().build()
            globalApi.massagingStub.subscribe(request).collect {
                var previousCreatedAt: LocalDateTime? = null
                messageQueue.value = it.messagesList.map { messageIt ->
                    val currentLocalDateTime = messageIt.createdAt.toLocalDateTime()
                    val uiMessage = UiMessage(
                        message = messageIt.message,
                        sendAt = currentLocalDateTime,
                        isOwner = when (messageIt.source.toUUID()) {
                            me.value -> UiMessage.OwnerType.ME
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
        globalApi.shutdown()
    }
}