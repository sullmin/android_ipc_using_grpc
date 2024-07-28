package com.example.android_ipc_grpc.ipc.grpc_implem

import IpcCoreGrpcKt
import IpcCoreOuterClass
import com.example.android_ipc_grpc.IpcApplication
import com.example.android_ipc_grpc.db.schemas.Message
import com.example.android_ipc_grpc.utils.toByteString
import com.example.android_ipc_grpc.utils.toTimestamp
import io.grpc.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID

class IpcCoreService : IpcCoreGrpcKt.IpcCoreCoroutineImplBase() {
    private val messageDao = IpcApplication.database.messageDao()
    private val currentDeviceId: UUID
        get() {
            return DEVICE_CONTEXT_KEY.get(Context.current())
        }

    override suspend fun sendMessage(request: IpcCoreOuterClass.SendMessageRequest): IpcCoreOuterClass.SendMessageResponse {
        messageDao.insertMessage(
            Message(
                createdBy = currentDeviceId,
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