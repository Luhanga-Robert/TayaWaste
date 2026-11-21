package com.example.tayawaste.offline

data class PendingCollectionRequestEntity(
    val id: String,
    val citizenId: String,
    val title: String,
    val dateTime: String,
    val collector: String,
    val requestedBy: String,
    val description: String = "",
    val location: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String = "",
    val photoPath: String? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING_SYNC,
    val retryCount: Int = 0,
    val lastError: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class PendingWasteReportEntity(
    val id: String,
    val citizenId: String,
    val type: String,
    val location: String,
    val councilName: String,
    val description: String,
    val reportedBy: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String = "",
    val photoPath: String? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING_SYNC,
    val retryCount: Int = 0,
    val lastError: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
