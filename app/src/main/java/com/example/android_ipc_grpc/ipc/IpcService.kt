package com.example.android_ipc_grpc.ipc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.android_ipc_grpc.ipc.grpc_implem.AuthenticationInterceptor
import com.example.android_ipc_grpc.ipc.grpc_implem.AuthenticationService
import com.example.android_ipc_grpc.ipc.grpc_implem.IpcCoreService
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder

class IpcService : Service() {
    private var ipcServer: Server? = null

    override fun onCreate() {
        super.onCreate()
        ipcServer = try {
            NettyServerBuilder.forPort(8080)
                .intercept(
                    AuthenticationInterceptor()
                )
                .addService(
                    IpcCoreService()
                )
                .addService(
                    AuthenticationService()
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