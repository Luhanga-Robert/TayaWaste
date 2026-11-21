package com.example.tayawaste.offline

import android.content.Context
import android.util.Log
import java.io.File

object AdditionalWorkers {
    private const val TAG = "AdditionalWorkers"

    //EmailNotificationWorker logic
    fun executeEmailNotificationTask(context: Context, recipientEmail: String, subject: String, body: String): Boolean {
        Log.d(TAG, "Executing EmailNotificationTask for recipient: $recipientEmail")
        return true
    }

    // B. PickupReminderWorker logic
    fun executePickupReminderTask(context: Context, requestId: String, reminderTime: String): Boolean {
        Log.d(TAG, "Executing PickupReminderTask for request $requestId at $reminderTime")
        return true
    }

    // C. CouncilCacheWorker logic
    fun executeCouncilCacheTask(context: Context): Map<String, List<Double>> {
        Log.d(TAG, "Executing CouncilCacheTask: Refreshing boundary mappings for Mzuzu, Zomba, Blantyre, Lilongwe City Councils")
        return mapOf(
            "Mzuzu City Council" to listOf(-11.4583, 34.0151),
            "Zomba City Council" to listOf(-15.3833, 35.3333),
            "Blantyre City Council" to listOf(-15.7861, 35.0058),
            "Lilongwe City Council" to listOf(-13.9743, 33.7872)
        )
    }

    // D. MediaCleanupWorker logic
    fun executeMediaCleanupTask(context: Context): Int {
        Log.d(TAG, "Executing MediaCleanupTask: Cleaning uploaded temporary media files...")
        var deletedCount = 0
        try {
            val storageDir = File(context.filesDir, "pending_photos")
            if (storageDir.exists()) {
                val pendingPhotoPaths = TayaOfflineRepository.pendingRequests
                    .filter { it.syncStatus != SyncStatus.SYNCED }
                    .mapNotNull { it.photoPath }
                    .toSet() + TayaOfflineRepository.pendingReports
                    .filter { it.syncStatus != SyncStatus.SYNCED }
                    .mapNotNull { it.photoPath }
                    .toSet()

                storageDir.listFiles()?.forEach { file ->
                    if (!pendingPhotoPaths.contains(file.absolutePath)) {
                        if (file.delete()) {
                            deletedCount++
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error running MediaCleanupTask", e)
        }
        return deletedCount
    }
}
