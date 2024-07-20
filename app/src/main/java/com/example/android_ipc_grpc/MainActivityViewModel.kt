package com.example.android_ipc_grpc

import IpcCoreGrpcKt
import IpcCoreOuterClass
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_ipc_grpc.ui.models.UiMessage
import com.example.android_ipc_grpc.utils.toByteString
import com.example.android_ipc_grpc.utils.toLocalDateTime
import com.example.android_ipc_grpc.utils.toUUID
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivityViewModel : ViewModel() {
    private val channel: ManagedChannel by lazy {
        ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build()
    }
    private val stub: IpcCoreGrpcKt.IpcCoreCoroutineStub by lazy {
        IpcCoreGrpcKt.IpcCoreCoroutineStub(channel)
    }
    val messageQueue: MutableStateFlow<List<UiMessage>> = MutableStateFlow(listOf())
    val message: MutableStateFlow<String> = MutableStateFlow("")

    private fun getTemporaryIdentifier(pkg: String): UUID = when {
        pkg.endsWith("1") -> UUID.fromString("6919b702-9cec-445f-8678-eea4e2da912f")
        else -> UUID.fromString("50f15c64-49ed-4f66-b456-a81c4ffa926c")
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
        stub.sendMessage(request)
    }

    fun subscribe(pkg: String) {
        viewModelScope.launch {
            val request = IpcCoreOuterClass.SubscribeRequest.newBuilder().build()
            stub.subscribe(request).collect {
                messageQueue.value = it.messagesList.map { messageIt ->
                    UiMessage(
                        message = messageIt.message,
                        sendAt = messageIt.createdAt.toLocalDateTime(),
                        isOwner = when (messageIt.source.toUUID()) {
                            getTemporaryIdentifier(pkg) -> UiMessage.OwnerType.ME
                            else -> UiMessage.OwnerType.OTHER
                        }
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        channel.shutdown()
    }
}