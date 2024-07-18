package com.example.android_ipc_grpc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.android_ipc_grpc.ipc.IpcService


abstract class AbstractServiceActivity : ComponentActivity() {
    private lateinit var mService: IBinder
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mService = service
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        val pkg = "com.example.android_ipc_grpc.process_2"
        val cls = "com.example.android_ipc_grpc.ipc.IpcService"

        Intent().setComponent(
            ComponentName(pkg, cls)
        ).also { intent ->
            intent.action = "com.example.android_ipc_grpc.START_SERVICE"
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }
}