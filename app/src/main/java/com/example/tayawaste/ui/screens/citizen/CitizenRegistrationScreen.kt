package com.example.tayawaste.ui.screens.citizen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.AppState
import com.example.tayawaste.UserRole
import com.example.tayawaste.firebase.FirebaseManager
import com.example.tayawaste.ui.components.PrimaryButton
import com.example.tayawaste.ui.components.TayaPasswordField
import com.example.tayawaste.ui.components.TayaTextField
import com.example.tayawaste.ui.components.TayaTopBar
import com.example.tayawaste.ui.theme.GreenContainer

@Composable
fun CitizenRegistrationScreen(
    onBack: () -> Unit = {},
    onRegisterSuccess: (name: String, email: String, location: String) -> Unit = { _, _, _ -> }
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TayaTopBar("Citizen Registration", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(GreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text("Create Your Account", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                "Join TayaWaste and help keep your community clean and green.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TayaTextField(
                label = "Full Name",
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = ""
                },
                placeholder = "Enter your full name",
                leadingIcon = Icons.Filled.Person
            )

            TayaTextField(
                label = "Email Address",
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                placeholder = "Enter your email address",
                leadingIcon = Icons.Filled.Email
            )

            TayaTextField(
                label = "Location",
                value = location,
                onValueChange = {
                    location = it
                    errorMessage = ""
                },
                placeholder = "Enter your location",
                leadingIcon = Icons.Filled.LocationOn
            )

            TayaPasswordField(
                label = "Password",
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                placeholder = "Enter password"
            )

            TayaPasswordField(
                label = "Confirm Password",
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                placeholder = "Confirm password"
            )

            if (password.isNotBlank() && confirmPassword.isNotBlank() && password != confirmPassword) {
                Text(
                    "Password does not match",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            if (errorMessage.isNotBlank()) {
                Text(
                    errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            PrimaryButton(
                text = "Register",
                isLoading = isLoading,
                onClick = {
                    if (name.isBlank() || email.isBlank() || location.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        errorMessage = "Please fill in all required form fields."
                        return@PrimaryButton
                    }
                    if (password != confirmPassword) {
                        errorMessage = "Password does not match"
                        return@PrimaryButton
                    }
                    errorMessage = ""
                    isLoading = true
                    FirebaseManager.registerUserWithSeparateDetails(
                        name = name.trim(),
                        email = email.trim(),
                        password = password,
                        role = UserRole.CITIZEN,
                        location = location.trim()
                    ) { success, msg ->
                        isLoading = false
                        if (success) {
                            AppState.userName = name.trim()
                            AppState.userEmail = email.trim()
                            AppState.userLocation = location.trim()
                            onRegisterSuccess(name.trim(), email.trim(), location.trim())
                        } else {
                            errorMessage = msg ?: "Email already exists in the database. Please use a different email or log in."
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
