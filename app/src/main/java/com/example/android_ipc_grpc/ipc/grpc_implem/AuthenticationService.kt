package com.example.android_ipc_grpc.ipc.grpc_implem

import AuthenticationServiceGrpcKt
import AuthenticationServiceOuterClass
import android.util.Log
import com.example.android_ipc_grpc.IpcApplication
import com.example.android_ipc_grpc.db.schemas.Device
import com.example.android_ipc_grpc.db.schemas.Exercise
import com.example.android_ipc_grpc.utils.toByteString
import com.example.android_ipc_grpc.utils.toUUID
import com.google.protobuf.ByteString
import com.google.protobuf.ServiceException
import java.time.LocalDateTime

class AuthenticationService : AuthenticationServiceGrpcKt.AuthenticationServiceCoroutineImplBase() {
    private val deviceDao = IpcApplication.database.deviceDao()
    private val exerciseDao = IpcApplication.database.exerciseDao()

    private fun verifyExerciseValidity(
        exe: Exercise,
        device: Device,
        signedMessage: ByteArray
    ): Boolean {
        return try {
            TODO()
            //SecurityDecoder(device.publicKey).compareMessage(signedMessage, exe.rawMessage)
        } catch (e: Throwable) {
            Log.e("DEBUG", "ERROR $e")
            throw e
        }
    }

    private fun generateTokenForDevice(device: Device): String = ""

    override suspend fun registerDevice(request: AuthenticationServiceOuterClass.RegisterDeviceRequest): AuthenticationServiceOuterClass.RegisterDeviceResponse {
        val publicKey = request.publicKey.toByteArray()
        val device = deviceDao.upsertSafety(
            Device(
                publicKey = publicKey
            )
        )
        return AuthenticationServiceOuterClass.RegisterDeviceResponse.newBuilder()
            .setDevice(device.publicId.toByteString())
            .build()
    }

    override suspend fun generateExercise(request: AuthenticationServiceOuterClass.GenerateExerciseRequest): AuthenticationServiceOuterClass.GenerateExerciseResponse {
        val device = deviceDao.find(request.device.toUUID())
        //val rawMessage = ByteArray(512).apply { Random.Default.nextBytes(this) }
        val rawMessage =
            "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012".toByteArray(
                Charsets.UTF_8
            )
        val exercise = Exercise(
            deviceId = device.publicId,
            createdAt = LocalDateTime.now(),
            rawMessage = rawMessage
        )

        exerciseDao.revokedForDevice(device.publicId)
        exerciseDao.insert(exercise)
        return AuthenticationServiceOuterClass.GenerateExerciseResponse.newBuilder()
            .setRawMessage(ByteString.copyFrom(rawMessage))
            .build()
    }

    override suspend fun resolveExercise(request: AuthenticationServiceOuterClass.ResolveExerciseRequest): AuthenticationServiceOuterClass.ResolveExerciseResponse {
        Log.e("DEBUG", "resolveExercise")
        val device = deviceDao.find(request.device.toUUID())
        Log.e("DEBUG", "device")
        val exercise = exerciseDao.find(
            device.publicId
        ) ?: throw ServiceException("Invalid exercise")
        Log.e("DEBUG", "exercise")
        val exerciseStatus = verifyExerciseValidity(
            exe = exercise,
            device = device,
            signedMessage = request.signedMessage.toByteArray()
        )
        Log.e("DEBUG", "exercise status")

        exerciseDao.update(
            exercise.copy(
                answeredAt = LocalDateTime.now(),
                responseSuccess = exerciseStatus
            )
        )
        Log.e("DEBUG", "exercise Update $exerciseStatus")
        if (!exerciseStatus) {
            throw ServiceException("Exercise response invalid")
        }
        Log.e("DEBUG", "exercise response")
        return AuthenticationServiceOuterClass.ResolveExerciseResponse.newBuilder()
            .setToken(
                generateTokenForDevice(device)
            )
            .build()
    }
}