package com.example.tayawaste.ui.screens.citizen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.ui.components.IconCircle
import com.example.tayawaste.ui.components.TayaTopBar
import com.example.tayawaste.ui.theme.GreenLight

@Composable
fun CitizenHomeScreen(
    userName: String = "Citizen",
    onFindCollectors: () -> Unit = {},
    onRequestCollection: () -> Unit = {},
    onReportProblem: () -> Unit = {},
    onMyRequests: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TayaTopBar("TayaWaste")

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Greeting Header matching Mockup 8
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.clickable(onClick = onProfileClick)
            ) {
                IconCircle(Icons.Filled.Person)
                Column {
                    Text("Hello, $userName", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Keep your community clean!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2x2 Action Tiles matching Mockup 8
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionTile(
                    title = "Find Collectors",
                    subtitle = "View available\nwaste collectors",
                    icon = Icons.Filled.Search,
                    modifier = Modifier.weight(1f),
                    onClick = onFindCollectors
                )
                ActionTile(
                    title = "Request Collection",
                    subtitle = "Schedule waste\ncollection",
                    icon = Icons.Filled.CalendarMonth,
                    modifier = Modifier.weight(1f),
                    onClick = onRequestCollection
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionTile(
                    title = "Report Problem",
                    subtitle = "Report waste\nmanagement issues",
                    icon = Icons.Filled.Warning,
                    modifier = Modifier.weight(1f),
                    onClick = onReportProblem
                )
                ActionTile(
                    title = "My Requests",
                    subtitle = "Track your\ncollection requests",
                    icon = Icons.Filled.ListAlt,
                    modifier = Modifier.weight(1f),
                    onClick = onMyRequests
                )
            }

            // Quick Info banner matching Mockup 8
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GreenLight)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconCircle(Icons.Filled.Eco)
                Column {
                    Text("A cleaner city starts\nwith you!", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text(
                        "Reduce  •  Reuse  •  Recycle",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(GreenLight)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
        )
        Text(
            title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            subtitle,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
