package com.example.tayawaste.ui.screens.collector

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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.AppState
import com.example.tayawaste.PendingCollectorData
import com.example.tayawaste.UserRole
import com.example.tayawaste.firebase.FirebaseManager
import com.example.tayawaste.ui.components.DropdownField
import com.example.tayawaste.ui.components.PrimaryButton
import com.example.tayawaste.ui.components.TayaPasswordField
import com.example.tayawaste.ui.components.TayaTextField
import com.example.tayawaste.ui.components.TayaTopBar
import com.example.tayawaste.ui.theme.GreenContainer

@Composable
fun CollectorRegistrationScreen(
    onBack: () -> Unit = {},
    onRegisterSubmitted: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var businessId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var wasteTypes by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showPendingDialog by remember { mutableStateOf(false) }

    if (showPendingDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Registration Submitted") },
            text = {
                Text("Your application as a waste collector (Business ID: $businessId) has been submitted to the Admin. You will receive an email notification once approved so you can log in.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPendingDialog = false
                        onRegisterSubmitted()
                    }
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        TayaTopBar("Collector Registration", onBack = onBack)
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
                    Icons.Filled.Engineering,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text("Register as Waste Collector", fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(
                "Provide your business details to get verified by the admin.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TayaTextField(
                label = "Full Name",
                value = name,
                onValueChange = { name = it },
                placeholder = "Enter your full name",
                leadingIcon = Icons.Filled.Person
            )

            TayaTextField(
                label = "Business ID",
                value = businessId,
                onValueChange = { businessId = it },
                placeholder = "Enter your unique business ID",
                leadingIcon = Icons.Filled.Badge
            )

            TayaTextField(
                label = "Email Address",
                value = email,
                onValueChange = { email = it },
                placeholder = "Enter your email address",
                leadingIcon = Icons.Filled.Email
            )

            TayaTextField(
                label = "Service Area",
                value = area,
                onValueChange = {
                    area = it
                    errorMessage = ""
                },
                placeholder = "Enter your service area (e.g. Area 25, Chikanda, MchengaUtuwa)",
                leadingIcon = Icons.Filled.LocationOn
            )

            DropdownField(
                label = "Waste Types",
                placeholder = "Select waste types",
                options = listOf("Plastic, Paper, General", "Organic, General", "Plastic, Metal, General", "All Types"),
                selected = wasteTypes,
                onSelected = { wasteTypes = it },
                leadingIcon = Icons.Filled.Recycling
            )

            DropdownField(
                label = "Availability",
                placeholder = "Select availability",
                options = listOf("Mon - Sat (8:00 AM - 5:00 PM)", "Weekdays Only", "Full Time", "On Demand"),
                selected = availability,
                onSelected = { availability = it },
                leadingIcon = Icons.Filled.CalendarMonth
            )

            TayaPasswordField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                placeholder = "Enter password"
            )

            TayaPasswordField(
                label = "Confirm Password",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
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
                    if (name.isBlank() || businessId.isBlank() || email.isBlank() || area.isBlank() || wasteTypes.isBlank() || availability.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        errorMessage = "Please fill in all required form fields."
                        return@PrimaryButton
                    }
                    if (password != confirmPassword) {
                        errorMessage = "Password does not match"
                        return@PrimaryButton
                    }
                    errorMessage = ""
                    isLoading = true
                    FirebaseManager.registerUser(
                        name = name.trim(),
                        emailOrPhone = email.trim(),
                        password = password,
                        role = UserRole.COLLECTOR,
                        businessId = businessId.trim(),
                        area = area,
                        wasteTypes = wasteTypes,
                        availability = availability
                    ) { success, msg ->
                        isLoading = false
                        if (success) {
                            showPendingDialog = true
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
