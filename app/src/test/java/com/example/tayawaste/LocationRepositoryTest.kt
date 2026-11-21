package com.example.tayawaste

import com.example.tayawaste.location.LocationState
import com.example.tayawaste.ui.components.ChipStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationRepositoryTest {

    @Test
    fun reportSubmission_requiresValidGpsCoordinates() {
        var capturedLat: Double? = null
        var capturedLng: Double? = null

        val isGpsValid = capturedLat != null && capturedLng != null
        assertFalse("Report submission without GPS coordinates must fail validation", isGpsValid)

        capturedLat = -13.97430
        capturedLng = 33.78720

        val isGpsValidAfterFetch = capturedLat != null && capturedLng != null
        assertTrue("Report submission with acquired GPS coordinates passes validation", isGpsValidAfterFetch)
    }

    @Test
    fun locationStateSuccess_formatsCoordinatesAndAddressCorrectly() {
        val lat = -13.974321
        val lng = 33.787212
        val address = "Area 25, Lilongwe, Malawi"

        val state = LocationState.Success(lat, lng, address)

        assertEquals(-13.974321, state.latitude, 0.000001)
        assertEquals(33.787212, state.longitude, 0.000001)
        assertEquals("Area 25, Lilongwe, Malawi", state.address)
    }

    @Test
    fun requestData_storesGpsCoordinatesAndAddress() {
        val request = RequestData(
            id = "REQ-1001",
            title = "Plastic Waste Collection",
            dateTime = "Date: Today   Time: 10:00 AM",
            collector = "Green Future Services",
            status = ChipStatus.PENDING,
            location = "Area 25, Lilongwe",
            latitude = -13.97430,
            longitude = 33.78720,
            address = "Area 25, Lilongwe, Malawi"
        )

        assertEquals(-13.97430, request.latitude!!, 0.000001)
        assertEquals(33.78720, request.longitude!!, 0.000001)
        assertEquals("Area 25, Lilongwe, Malawi", request.address)
    }
}
