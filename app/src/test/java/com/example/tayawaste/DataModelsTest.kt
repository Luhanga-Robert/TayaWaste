package com.example.tayawaste

import com.example.tayawaste.ui.components.ChipStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DataModelsTest {

    @Test
    fun chipStatus_enumNameMapping() {
        assertEquals("PENDING", ChipStatus.PENDING.name)
        assertEquals("ACCEPTED", ChipStatus.ACCEPTED.name)
        assertEquals("COMPLETED", ChipStatus.COMPLETED.name)
        assertEquals("AVAILABLE", ChipStatus.AVAILABLE.name)
    }

    @Test
    fun userRole_enumNameMapping() {
        assertEquals("CITIZEN", UserRole.CITIZEN.name)
        assertEquals("COLLECTOR", UserRole.COLLECTOR.name)
        assertEquals("ADMIN", UserRole.ADMIN.name)
    }

    @Test
    fun councilReportData_creationAndFormatting() {
        val report = CouncilReportData(
            id = "REP-500",
            type = "Illegal dumping",
            location = "Kawale, Lilongwe",
            description = "Uncontained waste pile near market",
            reportedBy = "John Moyo",
            status = ChipStatus.PENDING,
            latitude = -13.9900,
            longitude = 33.7900,
            address = "Kawale, Lilongwe, Malawi"
        )

        assertEquals("Illegal dumping", report.type)
        assertEquals("Kawale, Lilongwe", report.location)
        assertEquals("John Moyo", report.reportedBy)
        assertEquals(ChipStatus.PENDING, report.status)
        assertEquals(-13.9900, report.latitude!!, 0.0001)
    }

    @Test
    fun appState_collectionsManagement() {
        AppState.requests.clear()
        val testRequest = RequestData(
            title = "Organic Collection",
            dateTime = "Today 14:00",
            collector = "Clean Planet Collectors",
            status = ChipStatus.ACCEPTED
        )
        AppState.requests.add(testRequest)

        assertEquals(1, AppState.requests.size)
        assertTrue(AppState.requests.contains(testRequest))
    }
}
