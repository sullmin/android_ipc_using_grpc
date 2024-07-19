package com.example.android_ipc_grpc.ipc

import IpcCoreGrpcKt
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

class IpcCoreService : IpcCoreGrpcKt.IpcCoreCoroutineImplBase() {
    private val messageDao = IpcApplication.database.messageDao()

    override suspend fun sendMessage(request: IpcCoreOuterClass.SendMessageRequest): IpcCoreOuterClass.SendMessageResponse {
        Log.e("DEBUG", "message send ${request.message}")
        messageDao.insertMessage(
            Message(
                id = 0,
                content = request.message
            )
        )
        return IpcCoreOuterClass.SendMessageResponse.newBuilder().build()
    }

    override fun subscribe(request: IpcCoreOuterClass.SubscribeRequest): Flow<IpcCoreOuterClass.SubscribeResponse> {
        return messageDao.listMessages().map { messages ->
            IpcCoreOuterClass.SubscribeResponse.newBuilder()
                .addAllMessages(
                    messages.map {
                        Log.e("DEBUG", "subscribe message ${it.content}")
                        IpcCoreOuterClass.Message.newBuilder()
                            .setSource("")
                            .setMessage(it.content)
                            .build()
                    }
                )
                .build()
        }
    }
}