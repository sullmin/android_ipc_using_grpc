package com.example.android_ipc_grpc.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android_ipc_grpc.db.dao.MessageDao
import com.example.android_ipc_grpc.db.schemas.Message

@Database(
    version = 1,
    entities = [
        Message::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}