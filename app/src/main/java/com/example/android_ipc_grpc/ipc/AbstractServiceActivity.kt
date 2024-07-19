package com.example.android_ipc_grpc.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.activity.ComponentActivity


abstract class AbstractServiceActivity : ComponentActivity() {
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mBound = true
            onServiceBound()
        }

        override fun onNullBinding(name: ComponentName?) {
            mBound = true
            onServiceBound()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    abstract fun onServiceBound()

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