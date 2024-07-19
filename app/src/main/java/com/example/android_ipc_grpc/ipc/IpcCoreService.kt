package com.example.android_ipc_grpc.ipc

import IpcCoreGrpcKt
import IpcCoreOuterClass
import com.example.android_ipc_grpc.IpcApplication
import com.example.android_ipc_grpc.db.schemas.Message
import com.example.android_ipc_grpc.utils.toByteString
import com.example.android_ipc_grpc.utils.toTimestamp
import com.example.android_ipc_grpc.utils.toUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class IpcCoreService : IpcCoreGrpcKt.IpcCoreCoroutineImplBase() {
    private val messageDao = IpcApplication.database.messageDao()

    override suspend fun sendMessage(request: IpcCoreOuterClass.SendMessageRequest): IpcCoreOuterClass.SendMessageResponse {
        messageDao.insertMessage(
            Message(
                createdBy = request.temporaryIdentifier.toUUID(),
                content = request.message,
                createdAt = LocalDateTime.now()
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
                            .setSource(it.createdBy.toByteString())
                            .setMessage(it.content)
                            .setCreatedAt(it.createdAt.toTimestamp())
                            .build()
                    }
                )
                .build()
        }
    }
}