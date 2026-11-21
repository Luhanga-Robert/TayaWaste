package com.example.tayawaste.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
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
import com.example.tayawaste.UserRole
import com.example.tayawaste.firebase.FirebaseManager
import com.example.tayawaste.ui.components.PrimaryButton
import com.example.tayawaste.ui.components.TayaPasswordField
import com.example.tayawaste.ui.components.TayaTextField
import com.example.tayawaste.ui.components.TayaTopBar
import com.example.tayawaste.ui.theme.GreenContainer

@Composable
fun ForgotPasswordScreen(
    role: UserRole = UserRole.CITIZEN,
    onBack: () -> Unit = {},
    onResetSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val roleTitle = when (role) {
        UserRole.CITIZEN -> "Citizen Account"
        UserRole.COLLECTOR -> "Collector Account"
        UserRole.ADMIN -> "Admin Account"
    }

    val canSubmit = email.isNotBlank() &&
            newPassword.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            newPassword == confirmPassword

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Password Reset Successful") },
            text = { Text("Your password has been reset successfully. You can now login with your new password.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onResetSuccess()
                    }
                ) {
                    Text("Go to Login", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        TayaTopBar("Forgot Password", onBack = onBack)

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
                    Icons.Filled.LockReset,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                "Reset Your Password",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Resetting password for $roleTitle. Enter your registered email address to set a new password.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
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

            TayaPasswordField(
                label = "New Password",
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = ""
                },
                placeholder = "Enter new password"
            )

            TayaPasswordField(
                label = "Confirm New Password",
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                placeholder = "Confirm new password"
            )

            if (newPassword.isNotBlank() && confirmPassword.isNotBlank() && newPassword != confirmPassword) {
                Text(
                    "Passwords do not match",
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
                text = "Reset Password",
                isLoading = isLoading,
                onClick = {
                    isLoading = true
                    FirebaseManager.resetPassword(email.trim(), newPassword, role) { success, msg ->
                        isLoading = false
                        if (success) {
                            showSuccessDialog = true
                        } else {
                            errorMessage = msg ?: "Failed to reset password. Please check your details."
                        }
                    }
                },
                enabled = canSubmit,
                modifier = Modifier.padding(top = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    "Remember your password?",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onBack) {
                    Text(
                        "Login",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
