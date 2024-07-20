package com.example.android_ipc_grpc.ui.models

import java.time.LocalDateTime

data class UiMessage(
    val message: String,
    val sendAt: LocalDateTime,
    val isOwner: OwnerType,
    val isMessageGroup: Boolean
) {
    enum class OwnerType {
        ME,
        OTHER
    }
}