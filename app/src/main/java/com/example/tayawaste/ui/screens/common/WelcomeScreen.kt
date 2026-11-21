package com.example.tayawaste.ui.screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.UserRole
import com.example.tayawaste.ui.components.RoleCard

@Composable
fun WelcomeScreen(
    onSelectRole: (UserRole) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Filled.Eco,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 24.dp)
                .size(72.dp)
        )
        Text(
            "TayaWaste",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Cleaner Communities,\nHealthier Tomorrow.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Welcome",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Choose how you want to access your account.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            RoleCard(
                title = "Admin",
                description = "Manage collectors, reports and system settings.",
                icon = Icons.Filled.Shield,
                onClick = { onSelectRole(UserRole.ADMIN) }
            )

            RoleCard(
                title = "Collector",
                description = "Manage your collections, view requests and more.",
                icon = Icons.Filled.Engineering,
                onClick = { onSelectRole(UserRole.COLLECTOR) }
            )

            RoleCard(
                title = "Citizen",
                description = "Find collectors, request collection and report issues.",
                icon = Icons.Filled.Person,
                onClick = { onSelectRole(UserRole.CITIZEN) }
            )
        }
    }
}
