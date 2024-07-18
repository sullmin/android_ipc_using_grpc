package com.example.android_ipc_grpc.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android_ipc_grpc.db.schemas.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: Message)

    @Query("SELECT * FROM messages")
    fun listMessages(): Flow<List<Message>>
}