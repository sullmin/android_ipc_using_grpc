package com.example.android_ipc_grpc.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.android_ipc_grpc.db.schemas.Device
import java.util.UUID

@Dao
abstract class DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(device: Device): Long

    @Query("SELECT * FROM devices WHERE id = :id")
    abstract fun get(id: Long): Device?

    @Transaction
    fun insertSafety(device: Device): Device {
        val id = insert(device)
        return get(id)!!
    }

    @Query("SELECT * FROM devices WHERE public_id = :publicId")
    abstract fun find(publicId: UUID): Device
}