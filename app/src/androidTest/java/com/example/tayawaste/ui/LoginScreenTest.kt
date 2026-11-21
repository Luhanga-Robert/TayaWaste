package com.example.tayawaste.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.tayawaste.UserRole
import com.example.tayawaste.ui.screens.common.LoginScreen
import com.example.tayawaste.ui.theme.TayaWasteTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysElementsAndHandlesInput() {
        var forgotPasswordClicked = false

        composeTestRule.setContent {
            TayaWasteTheme {
                LoginScreen(
                    role = UserRole.CITIZEN,
                    onForgotPasswordClick = { forgotPasswordClicked = true }
                )
            }
        }

        // 1. Verify Brand Title, Fields, and Buttons exist
        composeTestRule.onNodeWithText("TayaWaste").assertIsDisplayed()
        composeTestRule.onNodeWithText("Phone Number or Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Forgot Password?").assertIsDisplayed()

        // 2. Perform text input
        composeTestRule.onNodeWithText("Enter your phone number or email")
            .performTextInput("0999123456")

        composeTestRule.onNodeWithText("Enter your password")
            .performTextInput("password123")

        // 3. Perform button click
        composeTestRule.onNodeWithText("Forgot Password?").performClick()
        assertTrue("Forgot Password callback should be triggered", forgotPasswordClicked)
    }
}
