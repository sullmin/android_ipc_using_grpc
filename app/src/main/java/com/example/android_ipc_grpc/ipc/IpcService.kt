package com.example.android_ipc_grpc.ipc

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder

class IpcService : Service() {
    private var ipcServer: Server? = null

    override fun onCreate() {
        super.onCreate()
        Log.e("DEBUG", "SERVICE START")
        ipcServer = try {
            NettyServerBuilder.forPort(8080)
                .addService(
                    IpcCoreService()
                )
                .build()
                .start()
        } catch (e: Throwable) {
            Log.e("DEBUG", "SERVICE NOT STARTED $e")
            null
        }
    }

    override fun onDestroy() {
        Log.e("DEBUG", "SERVICE STOP")
        super.onDestroy()
        ipcServer?.shutdownNow()
        ipcServer = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}