package com.example.android_ipc_grpc.ipc.grpc_implem

import AuthenticationServiceGrpcKt
import AuthenticationServiceOuterClass
import com.example.android_ipc_grpc.IpcApplication
import com.example.android_ipc_grpc.db.schemas.Device
import com.example.android_ipc_grpc.db.schemas.Exercise
import com.example.android_ipc_grpc.utils.toByteString
import com.example.android_ipc_grpc.utils.toUUID
import com.google.protobuf.ByteString
import com.google.protobuf.ServiceException
import java.time.LocalDateTime
import kotlin.random.Random

class AuthenticationService : AuthenticationServiceGrpcKt.AuthenticationServiceCoroutineImplBase() {
    private val deviceDao = IpcApplication.database.deviceDao()
    private val exerciseDao = IpcApplication.database.exerciseDao()

    private fun verifyPublicKeyValidity(publicKey: ByteArray): Boolean = true
    private fun verifyExerciseValidity(
        exe: Exercise,
        device: Device,
        signedMessage: ByteArray
    ): Boolean = true

    private fun generateTokenForDevice(device: Device): String = ""

    override suspend fun registerDevice(request: AuthenticationServiceOuterClass.RegisterDeviceRequest): AuthenticationServiceOuterClass.RegisterDeviceResponse {
        val publicKey = request.publicKey.toByteArray().let {
            if (verifyPublicKeyValidity(it)) {
                it
            } else {
                null
            }
        } ?: throw ServiceException("Invalid public key")
        val device = deviceDao.insertSafety(
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
        val rawMessage = ByteArray(512).apply { Random.Default.nextBytes(this) }
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
        val device = deviceDao.find(request.device.toUUID())
        val exercise = exerciseDao.find(
            device.publicId
        ) ?: throw ServiceException("Invalid exercise")
        val exerciseStatus = verifyExerciseValidity(
            exe = exercise,
            device = device,
            signedMessage = request.signedMessage.toByteArray()
        )

        exerciseDao.update(
            exercise.copy(
                answeredAt = LocalDateTime.now(),
                responseSuccess = exerciseStatus
            )
        )
        if (!exerciseStatus) {
            throw ServiceException("Exercise response invalid")
        }
        return AuthenticationServiceOuterClass.ResolveExerciseResponse.newBuilder()
            .setToken(
                generateTokenForDevice(device)
            )
            .build()
    }
}