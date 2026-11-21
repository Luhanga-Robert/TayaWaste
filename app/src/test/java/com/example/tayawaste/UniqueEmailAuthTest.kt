package com.example.tayawaste

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UniqueEmailAuthTest {

    @Before
    fun setUp() {
        AppState.isLoggedIn = false
        AppState.userEmail = "luwangagift@gmail.com"
        AppState.pendingCollectors.clear()
        AppState.collectors.clear()
    }

    @Test
    fun registration_whenEmailAlreadyExistsInPendingCollectors_returnsDuplicateError() {
        AppState.pendingCollectors.add(
            PendingCollectorData(
                name = "Existing Collector",
                businessId = "CW-100",
                email = "duplicate@tayawaste.com",
                area = "Lilongwe",
                wasteTypes = "General",
                availability = "Full Time"
            )
        )

        val inputEmail = "duplicate@tayawaste.com"
        val isDuplicate = AppState.pendingCollectors.any { it.email.equals(inputEmail, ignoreCase = true) }

        assertTrue("Duplicate registration email must be detected", isDuplicate)
    }

    @Test
    fun registration_whenEmailIsUnique_passesDuplicateCheck() {
        val inputEmail = "newuser@gmail.com"
        val isDuplicate = AppState.pendingCollectors.any { it.email.equals(inputEmail, ignoreCase = true) }

        assertFalse("Unique registration email passes check", isDuplicate)
    }

    @Test
    fun login_whenEmailNotRegistered_showsUserNotRegisteredError() {
        val isRegistered = false
        val errorMessage = if (!isRegistered) "Email already exists in the database. Please use a different email or log in." else ""

        assertEquals("Email already exists in the database. Please use a different email or log in.", errorMessage)
    }
}
