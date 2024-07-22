package com.example.android_ipc_grpc.db.schemas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = Device::class,
            parentColumns = ["public_id"],
            childColumns = ["device_id"]
        )
    ]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "device_id")
    val deviceId: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    @ColumnInfo(name = "answered_at")
    val answeredAt: LocalDateTime? = null,
    @ColumnInfo(name = "revoked_at")
    val revokedAt: LocalDateTime? = null,
    @ColumnInfo(name = "response_success")
    val responseSuccess: Boolean? = null,
    @ColumnInfo(name = "raw_message", typeAffinity = ColumnInfo.BLOB)
    val rawMessage: ByteArray,
)