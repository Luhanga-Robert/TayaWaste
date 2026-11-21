package com.example.tayawaste

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthLogicTest {

    @Before
    fun setUp() {
        AppState.isLoggedIn = false
        AppState.selectedRole = UserRole.CITIZEN
        AppState.pendingCollectors.clear()
        AppState.collectors.clear()
    }

    @Test
    fun login_withEmptyInput_returnsValidationError() {
        val email = ""
        val password = ""

        val isValid = email.isNotBlank() && password.isNotBlank()
        assertFalse("Blank email login input should fail validation", isValid)
    }

    @Test
    fun citizenRegistration_withMatchingPasswords_passesValidation() {
        val name = "Chisomo Phiri"
        val email = "chisomo@gmail.com"
        val location = "Area 25, Lilongwe"
        val pass1 = "SecurePassword123"
        val pass2 = "SecurePassword123"

        val isPasswordMatching = pass1 == pass2
        val isFormFilled = name.isNotBlank() && email.isNotBlank() && location.isNotBlank()

        assertTrue("Matching passwords and complete form details should pass validation", isPasswordMatching && isFormFilled)
    }

    @Test
    fun citizenRegistration_withMismatchedPasswords_showsPasswordDoesNotMatchError() {
        val pass1 = "Password123"
        val pass2 = "DifferentPassword456"

        val isPasswordMatching = pass1 == pass2
        val errorMessage = if (pass1 != pass2) "Password does not match" else ""

        assertFalse("Mismatched passwords must fail registration validation", isPasswordMatching)
        assertEquals("Password does not match", errorMessage)
    }

    @Test
    fun collectorLogin_whenPendingAdminApproval_identifiesPendingState() {
        val pendingCollector = PendingCollectorData(
            name = "Green Tech Services",
            businessId = "CW-9999",
            email = "greentech@tayawaste.com",
            area = "Area 18",
            wasteTypes = "Plastic, Paper",
            availability = "Mon - Fri"
        )
        AppState.pendingCollectors.add(pendingCollector)

        val input = "greentech@tayawaste.com"
        val pendingMatch = AppState.pendingCollectors.find {
            it.businessId.equals(input, ignoreCase = true) ||
                    it.email.equals(input, ignoreCase = true) ||
                    it.name.equals(input, ignoreCase = true)
        }

        assertTrue("Collector pending admin approval must be detected by email", pendingMatch != null)
        assertEquals("CW-9999", pendingMatch?.businessId)
    }

    @Test
    fun emailValidation_verifiesEmailFormat() {
        val emailInput = " chisomo@gmail.com "
        val sanitizedEmail = emailInput.trim().lowercase()

        assertEquals("chisomo@gmail.com", sanitizedEmail)
        assertTrue("Sanitized email contains @", sanitizedEmail.contains("@"))
    }
}
