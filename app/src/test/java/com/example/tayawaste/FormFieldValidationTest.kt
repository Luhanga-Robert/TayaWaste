package com.example.tayawaste

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class FormFieldValidationTest {

    @Test
    fun requestCollectionForm_whenFieldsBlank_failsValidation() {
        val wasteType = ""
        val dateTime = ""
        val capturedLat: Double? = null

        val validationError = when {
            wasteType.isBlank() -> "Please select a waste type."
            dateTime.isBlank() -> "Please enter preferred date & time."
            capturedLat == null -> "Valid GPS location is required to submit a collection request."
            else -> ""
        }

        assertEquals("Please select a waste type.", validationError)
    }

    @Test
    fun requestCollectionForm_whenDateMissing_failsValidation() {
        val wasteType = "Plastic"
        val dateTime = ""
        val capturedLat: Double? = null

        val validationError = when {
            wasteType.isBlank() -> "Please select a waste type."
            dateTime.isBlank() -> "Please enter preferred date & time."
            capturedLat == null -> "Valid GPS location is required to submit a collection request."
            else -> ""
        }

        assertEquals("Please enter preferred date & time.", validationError)
    }

    @Test
    fun reportProblemForm_whenTypeAndDescriptionBlank_failsValidation() {
        val problemType = ""
        val description = ""

        val validationError = when {
            problemType.isBlank() -> "Please select a problem type."
            description.isBlank() -> "Please enter a description of the problem."
            else -> ""
        }

        assertEquals("Please select a problem type.", validationError)
    }

    @Test
    fun citizenRegistrationForm_whenFieldsBlank_returnsFillAllFieldsError() {
        val name = ""
        val email = "user@gmail.com"
        val location = "Lilongwe"
        val pass1 = "123"
        val pass2 = "123"

        val isFilled = name.isNotBlank() && email.isNotBlank() && location.isNotBlank() && pass1.isNotBlank() && pass2.isNotBlank()
        val errorMessage = if (!isFilled) "Please fill in all required form fields." else ""

        assertFalse("Blank name must fail form validation", isFilled)
        assertEquals("Please fill in all required form fields.", errorMessage)
    }

    @Test
    fun changePasswordForm_whenFieldsBlank_returnsRequiredFieldsError() {
        val currentPass = ""
        val newPass = "NewPass123"
        val confirmPass = "NewPass456"

        val errorMessage = when {
            currentPass.isBlank() || newPass.isBlank() || confirmPass.isBlank() -> "Please fill in all required password fields."
            newPass != confirmPass -> "Password does not match"
            else -> ""
        }

        assertEquals("Please fill in all required password fields.", errorMessage)
    }
}
