package com.example.android_ipc_grpc.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.MutableStateFlow


abstract class AbstractServiceActivity : ComponentActivity() {
    private val mBound: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mBound.value = true
            onServiceBound()
        }

        override fun onNullBinding(name: ComponentName?) {
            mBound.value = true
            onServiceBound()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound.value = false
        }
    }

    abstract fun onServiceBound()

    fun serviceBound(): MutableStateFlow<Boolean> = mBound

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
        mBound.value = false
    }
}