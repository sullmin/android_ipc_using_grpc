package com.example.android_ipc_grpc.utils

import com.google.protobuf.ByteString
import java.nio.ByteBuffer
import java.util.UUID

fun UUID.toByteString(): ByteString = ByteString.copyFrom(
    ByteBuffer.allocate(16)
        .putLong(this@toByteString.mostSignificantBits)
        .putLong(this@toByteString.leastSignificantBits)
        .apply { position(0) }
)

fun ByteString.toUUID(): UUID = ByteBuffer.wrap(toByteArray()).let {
    UUID(it.getLong(), it.getLong())
}