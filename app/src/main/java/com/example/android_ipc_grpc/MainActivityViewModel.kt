package com.example.android_ipc_grpc

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

class MainActivityViewModel : ViewModel() {
    private val channel: ManagedChannel by lazy {
        ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build()
    }
    private val stub: IpcCoreGrpc.IpcCoreBlockingStub by lazy {
        IpcCoreGrpc.newBlockingStub(channel)
    }
    var messageQueue: Flow<List<String>> = emptyFlow()

    fun sendMessage() {
        val request = IpcCoreOuterClass.SendMessageRequest.newBuilder()
            .setMessage(UUID.randomUUID().toString()).build()
        stub.sendMessage(request)
    }

    fun subscribe() {
        Log.e("DEBUG", "here start")
        val request = IpcCoreOuterClass.SubscribeRequest.newBuilder().build()

        messageQueue = stub.subscribe(request).asFlow().map {
            Log.e("DEBUG", "here ${it.messagesList.size}")
            it.messagesList.map { messageIt ->
                Log.e("DEBUG", "here ${messageIt.source} ${messageIt.message}")
                messageIt.message
            }
        }
        Log.e("DEBUG", "here END")
    }

    override fun onCleared() {
        super.onCleared()
        channel.shutdown()
    }
}