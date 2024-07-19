package com.example.android_ipc_grpc.ipc

import IpcCoreGrpcKt
import IpcCoreOuterClass
import com.example.android_ipc_grpc.IpcApplication
import com.example.android_ipc_grpc.db.schemas.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IpcCoreService : IpcCoreGrpcKt.IpcCoreCoroutineImplBase() {
    private val messageDao = IpcApplication.database.messageDao()

    override suspend fun sendMessage(request: IpcCoreOuterClass.SendMessageRequest): IpcCoreOuterClass.SendMessageResponse {
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