package com.example.android_ipc_grpc.db.schemas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "devices",
    indices = [
        Index(
            value = ["public_id"],
            unique = true
        )
    ]
)
data class Device(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "public_id")
    val publicId: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "public_key", typeAffinity = ColumnInfo.BLOB)
    val publicKey: ByteArray,
)