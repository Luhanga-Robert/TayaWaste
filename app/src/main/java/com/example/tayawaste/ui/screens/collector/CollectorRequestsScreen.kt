package com.example.tayawaste.ui.screens.collector

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
import com.example.tayawaste.AppState
import com.example.tayawaste.firebase.FirebaseManager
import com.example.tayawaste.ui.components.ChipStatus
import com.example.tayawaste.ui.components.RequestCard
import com.example.tayawaste.ui.components.TayaTopBar

@Composable
fun CollectorRequestsScreen(onBack: (() -> Unit)? = null) {
    var tabIndex by remember { mutableIntStateOf(0) }

    val pendingRequests = AppState.requests.filter { it.status == ChipStatus.PENDING }
    val acceptedRequests = AppState.requests.filter { it.status == ChipStatus.ACCEPTED }
    val completedRequests = AppState.requests.filter { it.status == ChipStatus.COMPLETED || it.status == ChipStatus.REJECTED }

    Column(Modifier.fillMaxSize()) {
        TayaTopBar("Collection Requests", onBack = onBack)
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Pending (${pendingRequests.size})") })
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Accepted (${acceptedRequests.size})") })
            Tab(selected = tabIndex == 2, onClick = { tabIndex = 2 }, text = { Text("Done (${completedRequests.size})") })
        }

        val displayList = when (tabIndex) {
            0 -> pendingRequests
            1 -> acceptedRequests
            else -> completedRequests
        }

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(displayList) { req ->
                RequestCard(
                    title = req.title,
                    dateTime = req.dateTime,
                    collector = req.collector,
                    requestedBy = req.requestedBy,
                    description = req.description,
                    status = req.status,
                    latitude = req.latitude,
                    longitude = req.longitude,
                    address = req.address,
                    onAccept = if (tabIndex == 0) {
                        { FirebaseManager.updateRequestStatus(req.id, ChipStatus.ACCEPTED) }
                    } else null,
                    onReject = if (tabIndex == 0) {
                        { FirebaseManager.updateRequestStatus(req.id, ChipStatus.REJECTED) }
                    } else null
                )
            }
        }
    }
}
