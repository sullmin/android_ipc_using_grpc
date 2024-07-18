package com.example.android_ipc_grpc

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.util.UUID

class MainActivityViewModel : ViewModel() {
    private val channel: ManagedChannel by lazy {
        ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build()
    }
    private val stub: IpcCoreGrpc.IpcCoreBlockingStub by lazy {
        IpcCoreGrpc.newBlockingStub(channel)
    }
    val messageQueue = mutableStateListOf<String>()

    fun sendMessage() {
        val request = IpcCoreOuterClass.SendMessageRequest.newBuilder()
            .setMessage(UUID.randomUUID().toString()).build()
        stub.sendMessage(request)
    }

    fun subscribe() {
        val request = IpcCoreOuterClass.SubscribeRequest.newBuilder().build()
        val response = stub.subscribe(request)

        messageQueue.clear()
        response.forEachRemaining {
            Log.e("DEBUG", "here ${it.source} ${it.message}")
            messageQueue.add("${it.source} ${it.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        channel.shutdown()
    }
}