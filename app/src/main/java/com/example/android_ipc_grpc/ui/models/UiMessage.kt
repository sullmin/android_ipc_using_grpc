package com.example.android_ipc_grpc.ui.models

data class UiMessage(
    val message: String,
    val sendAt: String,
    val isOwner: OwnerType
) {
    enum class OwnerType {
        ME,
        OTHER
    }
}