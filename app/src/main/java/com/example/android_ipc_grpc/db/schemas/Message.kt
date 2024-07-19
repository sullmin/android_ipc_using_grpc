package com.example.android_ipc_grpc.db.schemas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "created_by")
    val createdBy: UUID,
    val content: String,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
)