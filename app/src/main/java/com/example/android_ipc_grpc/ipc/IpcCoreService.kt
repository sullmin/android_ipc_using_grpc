package com.example.android_ipc_grpc.ipc

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.LinkedList
import java.util.Queue
import java.util.stream.Stream

class IpcCoreService : IpcCoreGrpc.IpcCoreImplBase() {
    /*private var iteratorPing = 0

    override fun ping(
        request: IpcCoreOuterClass.PingRequest?,
        responseObserver: StreamObserver<IpcCoreOuterClass.PingResponse>?
    ) {
        val requestPing = request?.body
        val response = IpcCoreOuterClass.PingResponse.newBuilder()
            .setEcho("$requestPing ${iteratorPing++}")
            .build()

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }*/
    private val messageQueue: MutableStateFlow<Queue<String>> = MutableStateFlow(LinkedList())

    override fun sendMessage(
        request: IpcCoreOuterClass.SendMessageRequest?,
        responseObserver: StreamObserver<IpcCoreOuterClass.SendMessageResponse>?
    ) {
        Log.e("DEBUG", "message ${request?.message}")
        messageQueue.value.add(request!!.message)

        val response = IpcCoreOuterClass.SendMessageResponse.newBuilder().build()

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }

    override fun subscribe(
        request: IpcCoreOuterClass.SubscribeRequest?,
        responseObserver: StreamObserver<IpcCoreOuterClass.SubscribeResponse>?
    ) {
        Log.e("DEBUG", "subscribe START")

        GlobalScope.launch {
            messageQueue.collect { messages ->
                messages.forEach {
                    val response = IpcCoreOuterClass.SubscribeResponse.newBuilder()
                        .setMessage(it)
                        .build()
                    Log.e("DEBUG", "FLOW $it")
                    responseObserver?.onNext(response)
                }
                Log.e("DEBUG", "subscribe END")
                responseObserver?.onCompleted()
            }
        }
    }
}