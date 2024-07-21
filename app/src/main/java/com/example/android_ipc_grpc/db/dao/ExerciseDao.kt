package com.example.android_ipc_grpc.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android_ipc_grpc.db.schemas.Exercise
import java.util.UUID

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(exercise: Exercise)

    @Update
    fun update(exercise: Exercise)

    @Query("SELECT * FROM exercises WHERE device_id = :devicePublicId AND answered_at IS NULL ORDER BY created_at DESC LIMIT 1")
    fun find(devicePublicId: UUID): Exercise?
}