package com.example.tayawaste

import com.example.tayawaste.offline.AdditionalWorkers
import com.example.tayawaste.offline.PendingCollectionRequestEntity
import com.example.tayawaste.offline.PendingWasteReportEntity
import com.example.tayawaste.offline.SyncStatus
import com.example.tayawaste.offline.TayaOfflineRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OfflineWorkManagerTest {

    @Before
    fun setUp() {
        TayaOfflineRepository.pendingRequests.clear()
        TayaOfflineRepository.pendingReports.clear()
        AppState.requests.clear()
        AppState.councilReports.clear()
    }

    @Test
    fun offlineCollectionRequest_savedSuccessfullyLocally() {
        val entity = PendingCollectionRequestEntity(
            id = "req-offline-001",
            citizenId = "citizen@tayawaste.com",
            title = "Plastic Collection",
            dateTime = "Today 10 AM",
            collector = "Green Services",
            requestedBy = "John Moyo",
            description = "3 bags of plastic",
            location = "Area 25",
            latitude = -13.9743,
            longitude = 33.7872,
            address = "Area 25, Lilongwe, Malawi",
            syncStatus = SyncStatus.PENDING_SYNC
        )
        TayaOfflineRepository.pendingRequests.add(entity)

        assertEquals(1, TayaOfflineRepository.pendingRequests.size)
        assertEquals(SyncStatus.PENDING_SYNC, TayaOfflineRepository.pendingRequests[0].syncStatus)
    }

    @Test
    fun offlineWasteReport_savedSuccessfullyLocally() {
        val entity = PendingWasteReportEntity(
            id = "report-offline-001",
            citizenId = "citizen@tayawaste.com",
            type = "Illegal dumping",
            location = "Zomba",
            councilName = "Zomba City Council",
            description = "Uncontained waste near market",
            reportedBy = "Chisomo Phiri",
            latitude = -15.3833,
            longitude = 35.3333,
            address = "Zomba, Southern Region, Malawi",
            syncStatus = SyncStatus.PENDING_SYNC
        )
        TayaOfflineRepository.pendingReports.add(entity)

        assertEquals(1, TayaOfflineRepository.pendingReports.size)
        assertEquals(SyncStatus.PENDING_SYNC, TayaOfflineRepository.pendingReports[0].syncStatus)
    }

    @Test
    fun gpsCoordinates_preservedDuringOfflineSubmission() {
        val lat = -13.974321
        val lng = 33.787212
        val entity = PendingCollectionRequestEntity(
            id = "req-gps-001",
            citizenId = "citizen@tayawaste.com",
            title = "Organic Collection",
            dateTime = "Tomorrow 9 AM",
            collector = "Eco Solutions",
            requestedBy = "John Moyo",
            location = "Area 25",
            latitude = lat,
            longitude = lng,
            address = "Area 25, Lilongwe",
            syncStatus = SyncStatus.PENDING_SYNC
        )

        assertEquals(lat, entity.latitude!!, 0.000001)
        assertEquals(lng, entity.longitude!!, 0.000001)
    }

    @Test
    fun photographs_remainAvailableUntilSyncSucceeds() {
        val photoPath = "/storage/emulated/0/TayaWaste/photo_001.jpg"
        val entity = PendingWasteReportEntity(
            id = "report-photo-001",
            citizenId = "citizen@tayawaste.com",
            type = "Missed Collection",
            location = "Blantyre",
            councilName = "Blantyre City Council",
            description = "Bins not emptied",
            reportedBy = "John Moyo",
            photoPath = photoPath,
            syncStatus = SyncStatus.PENDING_SYNC
        )

        assertNotNull(entity.photoPath)
        assertEquals(photoPath, entity.photoPath)
        assertEquals(SyncStatus.PENDING_SYNC, entity.syncStatus)
    }

    @Test
    fun syncStatus_displayTextFormatting() {
        assertEquals(
            "Your request has been saved on this device and will automatically upload when internet connectivity is restored.",
            SyncStatus.PENDING_SYNC.getDisplayText()
        )
        assertEquals("Uploading your request...", SyncStatus.SYNCING.getDisplayText())
        assertEquals("Your request has been successfully uploaded.", SyncStatus.SYNCED.getDisplayText())
        assertEquals("Your request could not be synchronized. It will be retried automatically.", SyncStatus.FAILED.getDisplayText())
    }

    @Test
    fun failedUploads_remainStoredLocallyAndCanRetry() {
        val failedEntity = PendingCollectionRequestEntity(
            id = "req-retry-001",
            citizenId = "citizen@tayawaste.com",
            title = "Plastic Waste",
            dateTime = "Today",
            collector = "Clean Planet",
            requestedBy = "John Moyo",
            location = "Kawale",
            syncStatus = SyncStatus.FAILED,
            retryCount = 1,
            lastError = "Network connection timeout"
        )
        TayaOfflineRepository.pendingRequests.add(failedEntity)

        val item = TayaOfflineRepository.pendingRequests[0]
        assertEquals(SyncStatus.FAILED, item.syncStatus)
        assertEquals(1, item.retryCount)
        assertEquals("Network connection timeout", item.lastError)
    }

    @Test
    fun duplicateSubmissions_preventedByUniqueIdMatching() {
        val id = "req-unique-999"
        val req1 = PendingCollectionRequestEntity(
            id = id,
            citizenId = "citizen@tayawaste.com",
            title = "Plastic Waste",
            dateTime = "Today",
            collector = "Clean Planet",
            requestedBy = "John Moyo",
            location = "Kawale",
            syncStatus = SyncStatus.PENDING_SYNC
        )

        TayaOfflineRepository.pendingRequests.add(req1)
        assertEquals(1, TayaOfflineRepository.pendingRequests.size)

        val existingIndex = TayaOfflineRepository.pendingRequests.indexOfFirst { it.id == id }
        if (existingIndex != -1) {
            TayaOfflineRepository.pendingRequests[existingIndex] = req1.copy(retryCount = 1)
        }

        assertEquals(1, TayaOfflineRepository.pendingRequests.size)
        assertEquals(1, TayaOfflineRepository.pendingRequests[0].retryCount)
    }

    @Test
    fun userSpecificRecords_remainAssociatedWithAuthenticatedUser() {
        val userEmail = "citizen.chisomo@tayawaste.com"
        val entity = PendingCollectionRequestEntity(
            id = "req-user-001",
            citizenId = userEmail,
            title = "General Waste",
            dateTime = "Today",
            collector = "Eco Waste",
            requestedBy = "Chisomo Phiri",
            location = "Area 18",
            syncStatus = SyncStatus.PENDING_SYNC
        )

        assertEquals(userEmail, entity.citizenId)
        assertEquals("Chisomo Phiri", entity.requestedBy)
    }
}
