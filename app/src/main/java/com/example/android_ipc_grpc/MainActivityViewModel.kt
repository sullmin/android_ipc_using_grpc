package com.example.android_ipc_grpc

import IpcCoreGrpcKt
import IpcCoreOuterClass
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val messageQueue: MutableStateFlow<List<String>> = MutableStateFlow(listOf())

    suspend fun sendMessage() {
        val request = IpcCoreOuterClass.SendMessageRequest.newBuilder()
            .setMessage(UUID.randomUUID().toString()).build()
        stub.sendMessage(request)
    }

    fun subscribe() {
        viewModelScope.launch {
            val request = IpcCoreOuterClass.SubscribeRequest.newBuilder().build()
             stub.subscribe(request).collect {
                 messageQueue.value = it.messagesList.map { messageIt ->
                    messageIt.message
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        channel.shutdown()
    }
}