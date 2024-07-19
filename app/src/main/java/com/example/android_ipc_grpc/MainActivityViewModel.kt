package com.example.android_ipc_grpc

import IpcCoreGrpcKt
import IpcCoreOuterClass
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivityViewModel : ViewModel() {
    private val channel: ManagedChannel by lazy {
        ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build()
    }
    private val stub: IpcCoreGrpcKt.IpcCoreCoroutineStub by lazy {
        IpcCoreGrpcKt.IpcCoreCoroutineStub(channel)
    }
    var messageQueue: Flow<List<String>> = emptyFlow()

    suspend fun sendMessage() {
        val request = IpcCoreOuterClass.SendMessageRequest.newBuilder()
            .setMessage(UUID.randomUUID().toString()).build()
        Log.e("debug", "messageQueue content ${messageQueue.count()}")
        stub.sendMessage(request)
    }

    fun subscribe() {
        Log.e("DEBUG", "here start")
        val request = IpcCoreOuterClass.SubscribeRequest.newBuilder().build()

        viewModelScope.launch {
            messageQueue = stub.subscribe(request).map {
                Log.e("DEBUG", "here ${it.messagesList.size}")
                it.messagesList.map { messageIt ->
                    Log.e("DEBUG", "here ${messageIt.source} ${messageIt.message}")
                    messageIt.message
                }
            }
        }
        Log.e("DEBUG", "here END")
    }

    override fun onCleared() {
        super.onCleared()
        channel.shutdown()
    }
}