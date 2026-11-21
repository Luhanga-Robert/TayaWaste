package com.example.tayawaste.ui.screens.collector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.AppState
import com.example.tayawaste.ui.components.ActionRow
import com.example.tayawaste.ui.components.ChipStatus
import com.example.tayawaste.ui.components.IconCircle
import com.example.tayawaste.ui.components.StatTile
import com.example.tayawaste.ui.components.TayaTopBar
import com.example.tayawaste.ui.theme.GreenLight
import com.example.tayawaste.ui.theme.StatusAcceptedBg
import com.example.tayawaste.ui.theme.StatusAcceptedText
import com.example.tayawaste.ui.theme.StatusCompletedBg
import com.example.tayawaste.ui.theme.StatusCompletedText

@Composable
fun CollectorDashboardScreen(
    onViewRequests: () -> Unit = {},
    onUpdateAvailability: () -> Unit = {},
    onViewProfile: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TayaTopBar("Collector Dashboard")

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Header card matching Mockup 9
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GreenLight)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconCircle(Icons.Filled.Engineering)
                Column(modifier = Modifier.weight(1f)) {
                    Text("Good morning,", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(AppState.userName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Business ID: ${AppState.businessId}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                BoxStatusTag("Approved", StatusCompletedBg, StatusCompletedText)
            }

            // Stat Tiles matching Mockup 9 (3 tiles using live real-time storage)
            val acceptedCount = AppState.requests.count { it.status == ChipStatus.ACCEPTED }.toString()
            val totalRequestsCount = AppState.requests.size.toString()
            val pendingCount = AppState.requests.count { it.status == ChipStatus.PENDING }.toString()

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile(acceptedCount, "Active Accepted", Color(0xFFE0F2FE), Color(0xFF0284C7), Modifier.weight(1f))
                StatTile(totalRequestsCount, "Total Requests", GreenLight, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                StatTile(pendingCount, "Pending", StatusAcceptedBg, StatusAcceptedText, Modifier.weight(1f))
            }

            // Quick Actions section matching Mockup 9
            Text("Quick Actions", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            ActionRow(
                title = "View Requests",
                subtitle = "Manage collection requests",
                icon = Icons.AutoMirrored.Filled.Assignment,
                onClick = onViewRequests
            )

            ActionRow(
                title = "Update Availability",
                subtitle = "Set your working hours",
                icon = Icons.Filled.CalendarMonth,
                onClick = onUpdateAvailability
            )

            ActionRow(
                title = "View Profile",
                subtitle = "Check your business details",
                icon = Icons.Filled.Person,
                onClick = onViewProfile
            )
        }
    }
}

@Composable
private fun BoxStatusTag(text: String, bg: Color, fg: Color) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = fg,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
