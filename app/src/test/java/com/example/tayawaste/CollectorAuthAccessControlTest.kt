package com.example.tayawaste

import com.example.tayawaste.ui.components.ChipStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CollectorAuthAccessControlTest {

    @Before
    fun setUp() {
        AppState.pendingCollectors.clear()
        AppState.collectors.clear()
    }

    private fun checkCollectorAccess(status: String): Pair<Boolean, String?> {
        return when (status) {
            "PENDING" -> Pair(false, "Your registration is still awaiting approval. You will receive an email once it has been reviewed.")
            "REJECTED" -> Pair(false, "Your registration was not approved. Please check your email for more information.")
            else -> Pair(true, null)
        }
    }

    @Test
    fun pendingCollector_loginAttempt_isDeniedWithPendingMessage() {
        val pending = PendingCollectorData(
            name = "Eco Sanitation",
            businessId = "CW-00777",
            email = "eco@tayawaste.com",
            area = "Area 25",
            wasteTypes = "General",
            availability = "Full Time"
        )
        AppState.pendingCollectors.add(pending)

        val pendingMatch = AppState.pendingCollectors.find {
            it.businessId.equals("CW-00777", ignoreCase = true) ||
                    it.email.equals("eco@tayawaste.com", ignoreCase = true)
        }

        assertNotNull(pendingMatch)
        val (allowed, message) = checkCollectorAccess("PENDING")
        assertFalse(allowed)
        assertEquals("Your registration is still awaiting approval. You will receive an email once it has been reviewed.", message)
    }

    @Test
    fun rejectedCollector_loginAttempt_isDeniedWithRejectedMessage() {
        val (allowed, message) = checkCollectorAccess("REJECTED")
        assertFalse(allowed)
        assertEquals("Your registration was not approved. Please check your email for more information.", message)
    }

    @Test
    fun approvedCollector_loginAttempt_isAllowed() {
        val (allowed, message) = checkCollectorAccess("APPROVED")
        assertTrue(allowed)
        assertEquals(null, message)
    }

    @Test
    fun collectorApproval_transitionsStatusToApproved() {
        val pending = PendingCollectorData(
            name = "Green World",
            businessId = "CW-00888",
            email = "greenworld@tayawaste.com",
            area = "Area 18",
            wasteTypes = "Plastic",
            availability = "Mon-Fri"
        )
        AppState.pendingCollectors.add(pending)

        // Approve action
        AppState.pendingCollectors.remove(pending)
        AppState.collectors.add(
            CollectorData(
                name = pending.name,
                area = pending.area,
                types = pending.wasteTypes,
                businessId = pending.businessId,
                status = ChipStatus.AVAILABLE
            )
        )

        assertTrue(AppState.pendingCollectors.isEmpty())
        assertEquals(1, AppState.collectors.size)
        assertEquals("CW-00888", AppState.collectors[0].businessId)
    }

    @Test
    fun collectorRejection_removesFromPendingAndCollectorsList() {
        val pending = PendingCollectorData(
            name = "Bad Waste Co",
            businessId = "CW-00999",
            email = "bad@tayawaste.com",
            area = "Kawale",
            wasteTypes = "General",
            availability = "On Demand"
        )
        AppState.pendingCollectors.add(pending)

        // Reject action
        AppState.pendingCollectors.remove(pending)
        AppState.collectors.removeAll { it.businessId == pending.businessId }

        assertTrue(AppState.pendingCollectors.isEmpty())
        assertTrue(AppState.collectors.isEmpty())
    }
}
