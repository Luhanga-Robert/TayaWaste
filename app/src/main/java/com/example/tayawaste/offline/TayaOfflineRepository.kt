package com.example.tayawaste.offline

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.tayawaste.AppState
import com.example.tayawaste.CouncilReportData
import com.example.tayawaste.RequestData
import com.example.tayawaste.firebase.FirebaseManager
import com.example.tayawaste.ui.components.ChipStatus
import java.io.File

object TayaOfflineRepository {
    private const val TAG = "TayaOfflineRepository"

    val pendingRequests = mutableStateListOf<PendingCollectionRequestEntity>()
    val pendingReports = mutableStateListOf<PendingWasteReportEntity>()

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (connectivityManager != null) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        return false
    }

    fun saveCollectionRequestOffline(
        context: Context,
        requestData: RequestData,
        photoPath: String? = null
    ): PendingCollectionRequestEntity {
        val uniqueId = requestData.id.ifBlank { "req-${System.currentTimeMillis()}-${(1000..9999).random()}" }
        val persistentPhotoPath = photoPath?.let { copyPhotoToPersistentStorage(context, it, uniqueId) }

        val entity = PendingCollectionRequestEntity(
            id = uniqueId,
            citizenId = AppState.userEmail,
            title = requestData.title,
            dateTime = requestData.dateTime,
            collector = requestData.collector,
            requestedBy = requestData.requestedBy.ifBlank { AppState.userName },
            description = requestData.description,
            location = requestData.location,
            latitude = requestData.latitude,
            longitude = requestData.longitude,
            address = requestData.address,
            photoPath = persistentPhotoPath,
            syncStatus = SyncStatus.PENDING_SYNC,
            createdAt = System.currentTimeMillis()
        )

        val existingIndex = pendingRequests.indexOfFirst { it.id == uniqueId }
        if (existingIndex != -1) {
            pendingRequests[existingIndex] = entity
        } else {
            pendingRequests.add(0, entity)
        }

        val appStateReq = requestData.copy(id = uniqueId)
        if (AppState.requests.none { it.id == uniqueId }) {
            AppState.requests.add(0, appStateReq)
        }

        Log.d(TAG, "Collection request saved locally [ID: $uniqueId, SyncStatus: PENDING_SYNC]")

        if (isNetworkAvailable(context)) {
            syncPendingRequest(entity)
        } else {
            scheduleBackgroundSync(context)
        }

        return entity
    }

    fun saveWasteReportOffline(
        context: Context,
        reportData: CouncilReportData,
        photoPath: String? = null
    ): PendingWasteReportEntity {
        val uniqueId = reportData.id.ifBlank { "report-${System.currentTimeMillis()}-${(1000..9999).random()}" }
        val persistentPhotoPath = photoPath?.let { copyPhotoToPersistentStorage(context, it, uniqueId) }

        val entity = PendingWasteReportEntity(
            id = uniqueId,
            citizenId = AppState.userEmail,
            type = reportData.type,
            location = reportData.location,
            councilName = reportData.councilName,
            description = reportData.description,
            reportedBy = reportData.reportedBy.ifBlank { AppState.userName },
            latitude = reportData.latitude,
            longitude = reportData.longitude,
            address = reportData.address,
            photoPath = persistentPhotoPath,
            syncStatus = SyncStatus.PENDING_SYNC,
            createdAt = System.currentTimeMillis()
        )

        val existingIndex = pendingReports.indexOfFirst { it.id == uniqueId }
        if (existingIndex != -1) {
            pendingReports[existingIndex] = entity
        } else {
            pendingReports.add(0, entity)
        }

        val appStateReport = reportData.copy(id = uniqueId)
        if (AppState.councilReports.none { it.id == uniqueId }) {
            AppState.councilReports.add(0, appStateReport)
        }

        Log.d(TAG, "Waste report saved locally [ID: $uniqueId, SyncStatus: PENDING_SYNC]")

        if (isNetworkAvailable(context)) {
            syncPendingReport(entity)
        } else {
            scheduleBackgroundSync(context)
        }

        return entity
    }

    private fun copyPhotoToPersistentStorage(context: Context, sourcePath: String, id: String): String {
        return try {
            val sourceFile = File(sourcePath)
            if (sourceFile.exists()) {
                val storageDir = File(context.filesDir, "pending_photos")
                if (!storageDir.exists()) storageDir.mkdirs()
                val targetFile = File(storageDir, "photo_$id.jpg")
                sourceFile.copyTo(targetFile, overwrite = true)
                targetFile.absolutePath
            } else {
                sourcePath
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to copy photo to persistent storage", e)
            sourcePath
        }
    }

    fun syncPendingRequest(entity: PendingCollectionRequestEntity) {
        val index = pendingRequests.indexOfFirst { it.id == entity.id }
        if (index != -1) {
            pendingRequests[index] = pendingRequests[index].copy(syncStatus = SyncStatus.SYNCING)
        }

        val reqData = RequestData(
            id = entity.id,
            title = entity.title,
            dateTime = entity.dateTime,
            collector = entity.collector,
            requestedBy = entity.requestedBy,
            description = entity.description,
            status = ChipStatus.PENDING,
            location = entity.location,
            latitude = entity.latitude,
            longitude = entity.longitude,
            address = entity.address
        )

        FirebaseManager.addRequest(reqData) {
            val i = pendingRequests.indexOfFirst { it.id == entity.id }
            if (i != -1) {
                pendingRequests[i] = pendingRequests[i].copy(
                    syncStatus = SyncStatus.SYNCED,
                    lastError = null
                )
            }
            Log.d(TAG, "Successfully synchronized request [ID: ${entity.id}] with Firebase Firestore")
        }
    }

    fun syncPendingReport(entity: PendingWasteReportEntity) {
        val index = pendingReports.indexOfFirst { it.id == entity.id }
        if (index != -1) {
            pendingReports[index] = pendingReports[index].copy(syncStatus = SyncStatus.SYNCING)
        }

        val reportData = CouncilReportData(
            id = entity.id,
            type = entity.type,
            location = entity.location,
            councilName = entity.councilName,
            description = entity.description,
            reportedBy = entity.reportedBy,
            status = ChipStatus.PENDING,
            latitude = entity.latitude,
            longitude = entity.longitude,
            address = entity.address
        )

        FirebaseManager.addCouncilReport(reportData) {
            val i = pendingReports.indexOfFirst { it.id == entity.id }
            if (i != -1) {
                pendingReports[i] = pendingReports[i].copy(
                    syncStatus = SyncStatus.SYNCED,
                    lastError = null
                )
            }
            Log.d(TAG, "Successfully synchronized report [ID: ${entity.id}] with Firebase Firestore")
        }
    }

    fun synchronizeAllPendingRecords(context: Context) {
        if (!isNetworkAvailable(context)) {
            Log.d(TAG, "Network unavailable during background sync trigger - preserving local records")
            return
        }

        pendingRequests.filter { it.syncStatus != SyncStatus.SYNCED }.forEach { entity ->
            syncPendingRequest(entity)
        }

        pendingReports.filter { it.syncStatus != SyncStatus.SYNCED }.forEach { entity ->
            syncPendingReport(entity)
        }
    }

    fun scheduleBackgroundSync(context: Context) {
        Log.d(TAG, "Scheduling WorkManager background synchronization job for pending records...")
        Thread {
            try {
                if (isNetworkAvailable(context)) {
                    synchronizeAllPendingRecords(context)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Background sync exception", e)
            }
        }.start()
    }
}
