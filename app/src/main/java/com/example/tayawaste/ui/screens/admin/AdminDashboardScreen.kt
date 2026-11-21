package com.example.tayawaste.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.AppState
import com.example.tayawaste.ui.components.ActionRow
import com.example.tayawaste.ui.components.StatTile
import com.example.tayawaste.ui.components.TayaTopBar
import com.example.tayawaste.ui.theme.StatusAcceptedBg
import com.example.tayawaste.ui.theme.StatusAcceptedText

@Composable
fun AdminDashboardScreen(
    onReviewPending: () -> Unit = {},
    onManageCollectors: () -> Unit = {},
    onViewReports: () -> Unit = {},
    onChangePassword: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TayaTopBar("Admin Dashboard")

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // 2x2 Grid of StatTiles matching Mockup 6
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    count = AppState.pendingCollectors.size.toString(),
                    label = "Pending Collectors",
                    bgColor = StatusAcceptedBg,
                    textColor = StatusAcceptedText,
                    icon = Icons.Filled.Engineering,
                    iconBgColor = Color(0xFFFED7AA),
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    count = AppState.collectors.size.toString(),
                    label = "Approved Collectors",
                    bgColor = Color(0xFFE0F2FE),
                    textColor = Color(0xFF0369A1),
                    icon = Icons.Filled.Groups,
                    iconBgColor = Color(0xFFBAE6FD),
                    modifier = Modifier.weight(1f)
                )
            }
            val totalUsersCount = (AppState.collectors.size + AppState.pendingCollectors.size +
                    AppState.councilReports.map { it.reportedBy }.filter { it.isNotBlank() }.distinct().size + 1).toString()

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    count = AppState.councilReports.size.toString(),
                    label = "Waste Reports",
                    bgColor = Color(0xFFFEE2E2),
                    textColor = Color(0xFFB91C1C),
                    icon = Icons.Filled.Warning,
                    iconBgColor = Color(0xFFFECACA),
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    count = totalUsersCount,
                    label = "Total Users",
                    bgColor = Color(0xFFEFF6FF),
                    textColor = Color(0xFF1D4ED8),
                    icon = Icons.Filled.People,
                    iconBgColor = Color(0xFFDBEAFE),
                    modifier = Modifier.weight(1f)
                )
            }

            // Quick Actions matching Mockup 6
            Text("Quick Actions", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            ActionRow(
                title = "Review Pending Collectors",
                subtitle = "Approve or reject registrations",
                icon = Icons.Filled.Engineering,
                onClick = onReviewPending
            )

            ActionRow(
                title = "Manage Collectors",
                subtitle = "View and manage approved collectors",
                icon = Icons.Filled.Groups,
                onClick = onManageCollectors
            )

            ActionRow(
                title = "View Waste Reports",
                subtitle = "Check reported issues and status",
                icon = Icons.Filled.Warning,
                onClick = onViewReports
            )

            ActionRow(
                title = "Change Password",
                subtitle = "Update your admin password",
                icon = Icons.Filled.Lock,
                onClick = onChangePassword
            )
        }
    }
}
