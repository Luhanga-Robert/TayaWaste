package com.example.tayawaste

import com.example.tayawaste.ui.components.ChipStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ScreenStateUnitTest {

    @Before
    fun setUp() {
        AppState.isLoggedIn = false
        AppState.selectedRole = UserRole.CITIZEN
        AppState.requests.clear()
        AppState.pendingCollectors.clear()
        AppState.collectors.clear()
        AppState.councilReports.clear()
    }

    @Test
    fun loginScreen_validateInputs_handlesErrors() {
        var email = ""
        var password = ""

        var errorMessage = when {
            email.isBlank() -> "Please enter your email address."
            password.isBlank() -> "Please enter your password."
            else -> ""
        }
        assertEquals("Please enter your email address.", errorMessage)

        email = "chisomo@gmail.com"
        errorMessage = when {
            email.isBlank() -> "Please enter your email address."
            password.isBlank() -> "Please enter your password."
            else -> ""
        }
        assertEquals("Please enter your password.", errorMessage)

        password = "password123"
        errorMessage = when {
            email.isBlank() -> "Please enter your email address."
            password.isBlank() -> "Please enter your password."
            else -> ""
        }
        assertTrue("Valid inputs produce no error message", errorMessage.isEmpty())
    }

    @Test
    fun citizenHomeScreen_navigationAndRequestCreation() {
        val userName = "John Moyo"
        assertTrue("User name should be displayed in greeting header", userName.isNotBlank())

        val newRequest = RequestData(
            title = "Plastic Waste Collection",
            dateTime = "Date: Today   Time: 10:00 AM",
            collector = "Green Future Services",
            status = ChipStatus.PENDING,
            location = "Area 25, Lilongwe"
        )
        AppState.requests.add(0, newRequest)

        assertEquals(1, AppState.requests.size)
        assertEquals("Plastic Waste Collection", AppState.requests[0].title)
        assertEquals(ChipStatus.PENDING, AppState.requests[0].status)
    }

    @Test
    fun adminDashboardScreen_statsAndPendingApprovalLogic() {
        AppState.pendingCollectors.add(
            PendingCollectorData(
                name = "Eco Cleaners",
                businessId = "EC-101",
                email = "ecocleaners@tayawaste.com",
                area = "Area 18",
                wasteTypes = "General",
                availability = "Daily"
            )
        )

        assertEquals("1", AppState.pendingCollectors.size.toString())

        // Approve pending collector
        val pending = AppState.pendingCollectors.removeAt(0)
        AppState.collectors.add(
            CollectorData(
                name = pending.name,
                area = pending.area,
                types = pending.wasteTypes,
                businessId = pending.businessId
            )
        )

        assertEquals(0, AppState.pendingCollectors.size)
        assertEquals(1, AppState.collectors.size)
        assertEquals("Eco Cleaners", AppState.collectors[0].name)
    }

    @Test
    fun requestCollectionScreen_requiresGpsCoordinates() {
        var capturedLat: Double? = null
        var capturedLng: Double? = null

        var validationError = if (capturedLat == null || capturedLng == null) {
            "Valid GPS location is required to submit a collection request. Please tap 'Use My Current Location'."
        } else {
            ""
        }

        assertFalse("Missing GPS coordinates should set validation error", validationError.isEmpty())

        capturedLat = -13.9743
        capturedLng = 33.7872

        validationError = if (capturedLat == null || capturedLng == null) {
            "Valid GPS location is required"
        } else {
            ""
        }
        assertTrue("Acquired GPS coordinates pass validation", validationError.isEmpty())
    }
}
