package com.example.android_ipc_grpc.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android_ipc_grpc.db.schemas.Device
import java.util.UUID

@Dao
interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(device: Device)

    @Query("SELECT * FROM devices WHERE public_id = :publicId")
    fun find(publicId: UUID): Device
}