package com.example.android_ipc_grpc.db

import com.example.android_ipc_grpc.IpcApplication
import com.example.android_ipc_grpc.db.dao.DeviceDao
import com.example.android_ipc_grpc.db.dao.ExerciseDao
import com.example.android_ipc_grpc.db.dao.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDeviceDao(): DeviceDao = IpcApplication.database.deviceDao()

    @Provides
    @Singleton
    fun provideMessageDao(): MessageDao = IpcApplication.database.messageDao()

    @Provides
    @Singleton
    fun provideExerciseDao(): ExerciseDao = IpcApplication.database.exerciseDao()
}