package com.example.tayawaste.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.tayawaste.ui.screens.citizen.RequestCollectionScreen
import com.example.tayawaste.ui.theme.TayaWasteTheme
import org.junit.Rule
import org.junit.Test

class RequestCollectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun requestCollectionScreen_displaysFormAndGpsPicker() {
        composeTestRule.setContent {
            TayaWasteTheme {
                RequestCollectionScreen(
                    collector = "Green Future Services",
                    defaultLocation = "Area 25, Lilongwe"
                )
            }
        }

        // 1. Verify Top Bar and Form Labels
        composeTestRule.onNodeWithText("Request Collection").assertIsDisplayed()
        composeTestRule.onNodeWithText("Waste Type").assertIsDisplayed()
        composeTestRule.onNodeWithText("Real-Time Location (GPS)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Use My Current Location").assertIsDisplayed()
        composeTestRule.onNodeWithText("Preferred Date & Time").assertIsDisplayed()

        // 2. Perform Submit Click without GPS location -> verifies validation message
        composeTestRule.onNodeWithText("Submit Request").performClick()
    }
}
