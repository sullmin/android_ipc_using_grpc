package com.example.android_ipc_grpc.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android_ipc_grpc.db.schemas.Exercise
import java.time.LocalDateTime
import java.util.UUID

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(exercise: Exercise)

    @Update
    fun update(exercise: Exercise)

    @Query("SELECT * FROM exercises WHERE device_id = :devicePublicId AND (answered_at IS NULL OR revoked_at IS NULL) ORDER BY created_at DESC LIMIT 1")
    fun find(devicePublicId: UUID): Exercise?

    @Query("UPDATE exercises SET revoked_at = :revokedAt WHERE device_id = :devicePublicId AND answered_at IS NULL AND revoked_at IS NULL")
    fun revokedForDevice(
        devicePublicId: UUID,
        revokedAt: LocalDateTime = LocalDateTime.now()
    )
}