package com.example.android_ipc_grpc.utils

import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

fun LocalDateTime.toTimestamp(): Timestamp = Timestamp.newBuilder()
    .setSeconds(
        toEpochSecond(ZoneOffset.UTC)
    )
    .setNanos(
        this.nano
    )
    .build()

fun Timestamp.toLocalDateTime(): LocalDateTime = LocalDateTime.ofEpochSecond(
    this.seconds,
    this.nanos,
    ZoneOffset.UTC
)