package com.example.android_ipc_grpc.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
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
        val appMetadata = applicationContext.packageManager.getApplicationInfo(
            applicationContext.packageName,
            PackageManager.GET_META_DATA
        ).metaData
        val pkg = appMetadata?.getString("com.example.android_ipc_grpc.SERVICE_PACKAGE")
            ?: applicationContext.packageName
        val cls = IpcService::class.qualifiedName ?: ""

        Intent().setComponent(
            ComponentName(pkg, cls)
        ).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound.value = false
    }
}