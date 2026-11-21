package com.example.tayawaste.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.tayawaste.ui.screens.admin.AdminDashboardScreen
import com.example.tayawaste.ui.theme.TayaWasteTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AdminDashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun adminDashboardScreen_displaysStatTilesAndQuickActions() {
        var reviewPendingClicked = false

        composeTestRule.setContent {
            TayaWasteTheme {
                AdminDashboardScreen(
                    onReviewPending = { reviewPendingClicked = true }
                )
            }
        }

        // 1. Verify Header and Stat Tile Labels
        composeTestRule.onNodeWithText("Admin Dashboard").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pending Collectors").assertIsDisplayed()
        composeTestRule.onNodeWithText("Approved Collectors").assertIsDisplayed()
        composeTestRule.onNodeWithText("Waste Reports").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Users").assertIsDisplayed()

        // 2. Verify Quick Actions Section
        composeTestRule.onNodeWithText("Quick Actions").assertIsDisplayed()
        composeTestRule.onNodeWithText("Review Pending Collectors").assertIsDisplayed()
        composeTestRule.onNodeWithText("Manage Collectors").assertIsDisplayed()

        // 3. Perform click
        composeTestRule.onNodeWithText("Review Pending Collectors").performClick()
        assertTrue("Review Pending callback should be triggered", reviewPendingClicked)
    }
}
