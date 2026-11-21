package com.example.tayawaste.ui.screens.citizen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import com.example.tayawaste.AppState
import com.example.tayawaste.ui.components.ChipStatus
import com.example.tayawaste.ui.components.RequestCard
import com.example.tayawaste.ui.components.TayaTopBar

@Composable
fun MyRequestsScreen(onBack: (() -> Unit)? = null) {
    var tabIndex by remember { mutableIntStateOf(0) }

    val activeRequests = AppState.requests.filter { it.status != ChipStatus.COMPLETED && it.status != ChipStatus.REJECTED }
    val reportedProblems = AppState.councilReports
    val historyRequests = AppState.requests.filter { it.status == ChipStatus.COMPLETED || it.status == ChipStatus.REJECTED }

    Column(Modifier.fillMaxSize()) {
        TayaTopBar("My Requests & Reports", onBack = onBack)
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 },
                text = { Text("Requests (${activeRequests.size})") }
            )
            Tab(
                selected = tabIndex == 1,
                onClick = { tabIndex = 1 },
                text = { Text("Reports (${reportedProblems.size})") }
            )
            Tab(
                selected = tabIndex == 2,
                onClick = { tabIndex = 2 },
                text = { Text("History (${historyRequests.size})") }
            )
        }

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (tabIndex) {
                0 -> {
                    items(activeRequests) { req ->
                        RequestCard(
                            title = req.title,
                            dateTime = req.dateTime,
                            collector = req.collector,
                            description = req.description,
                            status = req.status,
                            latitude = req.latitude,
                            longitude = req.longitude,
                            address = req.address
                        )
                    }
                }
                1 -> {
                    items(reportedProblems) { report ->
                        RequestCard(
                            title = "${report.type} (Report)",
                            dateTime = report.location,
                            collector = report.councilName,
                            requestedBy = report.reportedBy,
                            description = report.description,
                            status = report.status,
                            icon = Icons.Filled.Warning,
                            latitude = report.latitude,
                            longitude = report.longitude,
                            address = report.address
                        )
                    }
                }
                else -> {
                    items(historyRequests) { req ->
                        RequestCard(
                            title = req.title,
                            dateTime = req.dateTime,
                            collector = req.collector,
                            description = req.description,
                            status = req.status,
                            latitude = req.latitude,
                            longitude = req.longitude,
                            address = req.address
                        )
                    }
                }
            }
        }
    }
}
