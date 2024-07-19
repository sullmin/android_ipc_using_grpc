package com.example.android_ipc_grpc

import android.app.Application
import androidx.room.Room
import com.example.android_ipc_grpc.db.AppDatabase
import com.example.android_ipc_grpc.db.converter.LocalDateTimeConverter

class IpcApplication : Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "android_ipc_grpc"
        )
            .addTypeConverter(
                LocalDateTimeConverter()
            )
            .build()
    }
}
