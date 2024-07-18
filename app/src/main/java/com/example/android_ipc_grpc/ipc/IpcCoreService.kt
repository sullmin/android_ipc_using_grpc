package com.example.android_ipc_grpc.ipc

import android.util.Log
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.mutableStateOf
import com.example.android_ipc_grpc.IpcApplication
import com.example.android_ipc_grpc.db.schemas.Message
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val messageDao = IpcApplication.database.messageDao()

    override fun sendMessage(
        request: IpcCoreOuterClass.SendMessageRequest?,
        responseObserver: StreamObserver<IpcCoreOuterClass.SendMessageResponse>?
    ) {
        Log.e("DEBUG", "message ${request?.message}")
        messageDao.insertMessage(
            Message(
                id = 0,
                content = request!!.message
            )
        )

        val response = IpcCoreOuterClass.SendMessageResponse.newBuilder().build()

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }

    override fun subscribe(
        request: IpcCoreOuterClass.SubscribeRequest?,
        responseObserver: StreamObserver<IpcCoreOuterClass.SubscribeResponse>?
    ) {
        Log.e("DEBUG", "subscribe START")



        CoroutineScope(git add).launch {
            messageDao.listMessages().collect { messages ->
                messages.forEach {
                    val response = IpcCoreOuterClass.SubscribeResponse.newBuilder()
                        .setMessage(it.content)
                        .build()
                    Log.e("DEBUG", "FLOW $it")
                    responseObserver?.onNext(response)
                }
            }
            Log.e("DEBUG", "subscribe END")
            responseObserver?.onCompleted()
        }
    }
}