package com.example.tayawaste.ui.screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.UserRole
import com.example.tayawaste.firebase.FirebaseManager
import com.example.tayawaste.ui.components.PrimaryButton
import com.example.tayawaste.ui.components.TayaPasswordField
import com.example.tayawaste.ui.components.TayaTextField
import com.example.tayawaste.ui.components.TayaTopBar

@Composable
fun LoginScreen(
    role: UserRole = UserRole.CITIZEN,
    onBack: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val screenTitle = when (role) {
        UserRole.CITIZEN -> "Citizen Login"
        UserRole.COLLECTOR -> "Collector Login"
        UserRole.ADMIN -> "Admin Login"
    }

    Column(Modifier.fillMaxSize()) {
        TayaTopBar(screenTitle, onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            //TayaWaste Logo
            Icon(
                Icons.Filled.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(80.dp),
            )
            Text(
                "TayaWaste",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            TayaTextField(
                label = "Email Address",
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                placeholder = "Enter your email address",
                leadingIcon = Icons.Filled.Email,
            )

            TayaPasswordField(
                label = "Password",
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                placeholder = "Enter your password",
            )

            if (errorMessage.isNotBlank()) {
                Text(
                    errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                )
            }

            PrimaryButton(
                text = "Login",
                isLoading = isLoading,
                onClick = {
                    if (email.isBlank()) {
                        errorMessage = "Please enter your email address."
                    } else if (password.isBlank()) {
                        errorMessage = "Please enter your password."
                    } else {
                        isLoading = true
                        FirebaseManager.loginUser(email.trim(), password, role) { success, error ->
                            isLoading = false
                            if (success) {
                                onLoginSuccess()
                            } else {
                                errorMessage = error ?: "User not registered. Please register to continue"
                            }
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp),
            )

            if (role != UserRole.ADMIN) {
                TextButton(
                    onClick = onForgotPasswordClick,
                    modifier = Modifier.padding(top = 2.dp),
                ) {
                    Text(
                        "Forgot Password?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 2.dp),
                ) {
                    Text(
                        "Don't have an account?",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = onRegisterClick) {
                        Text(
                            "Register",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}
