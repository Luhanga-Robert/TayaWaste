package com.example.tayawaste.offline

enum class SyncStatus {
    PENDING_SYNC,
    SYNCING,
    SYNCED,
    FAILED;

    fun getDisplayText(): String {
        return when (this) {
            PENDING_SYNC -> "Your request uploaded when internet connectivity is restored."
            SYNCING -> "Uploading your request..."
            SYNCED -> "Your request has been successfully uploaded."
            FAILED -> "Your request could not be synchronized. It will be retried automatically."
        }
    }
}
