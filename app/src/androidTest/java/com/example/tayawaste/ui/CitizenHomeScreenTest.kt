package com.example.tayawaste.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.tayawaste.ui.screens.citizen.CitizenHomeScreen
import com.example.tayawaste.ui.theme.TayaWasteTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CitizenHomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun citizenHomeScreen_displaysGreetingAndTiles() {
        var findCollectorsClicked = false
        var reportProblemClicked = false

        composeTestRule.setContent {
            TayaWasteTheme {
                CitizenHomeScreen(
                    userName = "John Moyo",
                    onFindCollectors = { findCollectorsClicked = true },
                    onReportProblem = { reportProblemClicked = true }
                )
            }
        }

        // 1. Verify Greeting Header and Action Tiles
        composeTestRule.onNodeWithText("Hello, John Moyo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Keep your community clean!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Find Collectors").assertIsDisplayed()
        composeTestRule.onNodeWithText("Request Collection").assertIsDisplayed()
        composeTestRule.onNodeWithText("Report Problem").assertIsDisplayed()
        composeTestRule.onNodeWithText("My Requests").assertIsDisplayed()

        // 2. Perform tile clicks
        composeTestRule.onNodeWithText("Find Collectors").performClick()
        assertTrue("Find Collectors action should be triggered", findCollectorsClicked)

        composeTestRule.onNodeWithText("Report Problem").performClick()
        assertTrue("Report Problem action should be triggered", reportProblemClicked)
    }
}
