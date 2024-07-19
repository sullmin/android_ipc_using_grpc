package com.example.android_ipc_grpc.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android_ipc_grpc.db.converter.LocalDateTimeConverter
import com.example.android_ipc_grpc.db.dao.MessageDao
import com.example.android_ipc_grpc.db.schemas.Message

@Database(
    version = 1,
    entities = [
        Message::class
    ]
)
@TypeConverters(
    LocalDateTimeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}