package com.example.android_ipc_grpc.ipc.grpc_implem

import android.util.Log
import io.grpc.Context
import io.grpc.Contexts
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status
import io.jsonwebtoken.Jwts
import java.util.UUID


val DEVICE_CONTEXT_KEY: Context.Key<UUID> = Context.key("device_context")

class AuthenticationInterceptor : ServerInterceptor {
    companion object {
        private val OPENED_API_CALL = listOf(
            "registerDevice",
            "resolveExercise",
            "generateExercise",
        )
    }

    private val authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>?,
        headers: Metadata?,
        next: ServerCallHandler<ReqT, RespT>?
    ): ServerCall.Listener<ReqT> {
        Log.e("DEBUG", "method ${call?.methodDescriptor?.bareMethodName}")
        if (OPENED_API_CALL.contains(call?.methodDescriptor?.bareMethodName)) {
            return next?.startCall(call, headers) ?: object : ServerCall.Listener<ReqT>() {}
        }
        return try {
            Log.e("DEBUG", "LABEL")
            val rawJwt = headers?.get(authKey)?.substringAfter("Bearer ")
            Log.e("DEBUG", "rawJwt $rawJwt")
            // val jwt = Jwts.parser().verifyWith(key).build().parseSignedClaims(rawJwt).getPayload().getSubject()
            val jwt = Jwts.parser().build().parseSignedClaims(rawJwt)
            Log.e("DEBUG", "jwt $jwt")
            val extractDeviceId = jwt.payload["device_public_id"].toString()

            Log.e("DEBUG", "extractDeviceId $extractDeviceId")
            // TODO VERIFY JWT VALIDITY
            if (extractDeviceId.isBlank()) {
                call?.close(Status.UNAUTHENTICATED.withDescription("Invalid token"), headers)
                object : ServerCall.Listener<ReqT>() {}
            } else {
                val currentDeviceId = UUID.fromString(extractDeviceId)
                val context = Context.current().withValue(DEVICE_CONTEXT_KEY, currentDeviceId)
                Contexts.interceptCall(context, call, headers, next)
            }
        } catch (e: Throwable) {
            Log.e("AuthenticationInterceptor", "AuthenticationInterceptor.interceptCall $e")
            call?.close(Status.ABORTED.withDescription("Error during authentication"), headers)
            object : ServerCall.Listener<ReqT>() {}
        }
    }
}