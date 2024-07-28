package com.example.android_ipc_grpc.ipc.grpc_implem

import AuthenticationServiceGrpcKt
import AuthenticationServiceOuterClass
import android.util.Log
import com.example.android_ipc_grpc.IpcApplication
import com.example.android_ipc_grpc.db.schemas.Device
import com.example.android_ipc_grpc.db.schemas.Exercise
import com.example.android_ipc_grpc.ipc.JwtSecurity
import com.example.android_ipc_grpc.ipc.SecurityKeyManager
import com.example.android_ipc_grpc.utils.toByteString
import com.example.android_ipc_grpc.utils.toUUID
import com.google.protobuf.ByteString
import io.grpc.Status
import io.grpc.StatusException
import io.jsonwebtoken.Jwts
import java.time.LocalDateTime
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random


class AuthenticationService : AuthenticationServiceGrpcKt.AuthenticationServiceCoroutineImplBase() {
    private val deviceDao = IpcApplication.database.deviceDao()
    private val exerciseDao = IpcApplication.database.exerciseDao()

    @OptIn(ExperimentalEncodingApi::class)
    private fun generateTokenForDevice(device: Device): String {
        return try {
            val key = JwtSecurity().secretKey
            Jwts.builder()
                .claim("device_public_id", device.publicId.toString())
                .signWith(key, Jwts.SIG.HS256)
                .compact()
        } catch (e: Throwable) {
            Log.e("DEBUG", "generateTokenForDevice $e")
            throw e
        }
    }

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
        val rawMessage = ByteArray(SecurityKeyManager.BLOCK_SIZE).apply {
            Random.Default.nextBytes(this)
        }
        val exercise = Exercise(
            deviceId = device.publicId,
            createdAt = LocalDateTime.now(),
            rawMessage = rawMessage
        )
        val signedMessage = SecurityKeyManager(device.publicKey).encrypt(rawMessage)

        exerciseDao.revokedForDevice(device.publicId)
        exerciseDao.insert(exercise)
        return AuthenticationServiceOuterClass.GenerateExerciseResponse.newBuilder()
            .setSignedMessage(ByteString.copyFrom(signedMessage))
            .build()
    }

    override suspend fun resolveExercise(request: AuthenticationServiceOuterClass.ResolveExerciseRequest): AuthenticationServiceOuterClass.ResolveExerciseResponse {
        val device = deviceDao.find(request.device.toUUID())
        val exercise = exerciseDao.find(
            device.publicId
        ) ?: throw StatusException(Status.INVALID_ARGUMENT.withDescription("Invalid exercise."))
        val exerciseStatus = exercise.rawMessage.contentEquals(request.rawMessage.toByteArray())

        exerciseDao.update(
            exercise.copy(
                answeredAt = LocalDateTime.now(),
                responseSuccess = exerciseStatus
            )
        )
        if (!exerciseStatus) {
            throw StatusException(Status.PERMISSION_DENIED.withDescription("Exercise response invalid."))
        }
        return AuthenticationServiceOuterClass.ResolveExerciseResponse.newBuilder()
            .setToken(
                generateTokenForDevice(device)
            )
            .build()
    }
}