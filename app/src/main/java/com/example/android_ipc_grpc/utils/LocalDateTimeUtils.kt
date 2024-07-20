package com.example.android_ipc_grpc.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


fun LocalDateTime.toStringFormat(): String = format(
    DateTimeFormatter.ofPattern("EEE dd MMM à HH:mm").withLocale(Locale.FRANCE)
)