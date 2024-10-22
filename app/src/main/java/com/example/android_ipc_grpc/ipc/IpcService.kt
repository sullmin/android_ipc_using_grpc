package com.example.android_ipc_grpc.ipc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.android_ipc_grpc.db.dao.DeviceDao
import com.example.android_ipc_grpc.db.dao.ExerciseDao
import com.example.android_ipc_grpc.db.dao.MessageDao
import com.example.android_ipc_grpc.ipc.grpc_implem.AuthenticationInterceptor
import com.example.android_ipc_grpc.ipc.grpc_implem.AuthenticationService
import com.example.android_ipc_grpc.ipc.grpc_implem.IpcCoreService
import dagger.hilt.android.AndroidEntryPoint
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder
import javax.inject.Inject


/*
@AndroidEntryPoint
class IpcService @Inject constructor(
    private val deviceDao: DeviceDao,
    private val messageDao: MessageDao,
    private val exerciseDao: ExerciseDao,
) : Service() {
 */


@AndroidEntryPoint
class IpcService : Service() {
    @Inject
    lateinit var deviceDao: DeviceDao

    @Inject
    lateinit var messageDao: MessageDao

    @Inject
    lateinit var exerciseDao: ExerciseDao

    private var ipcServer: Server? = null

    override fun onCreate() {
        super.onCreate()
        ipcServer = try {
            NettyServerBuilder.forPort(8080)
                .intercept(
                    AuthenticationInterceptor()
                )
                .addService(
                    IpcCoreService(messageDao)
                )
                .addService(
                    AuthenticationService(deviceDao, exerciseDao)
                )
                .build()
                .start()
        } catch (e: Throwable) {
            Log.e("DEBUG", "SERVICE NOT STARTED $e")
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ipcServer?.shutdownNow()
        ipcServer = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}