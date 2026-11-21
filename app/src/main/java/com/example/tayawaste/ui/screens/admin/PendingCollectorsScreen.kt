package com.example.tayawaste.ui.screens.admin

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.AppState
import com.example.tayawaste.CollectorData
import com.example.tayawaste.PendingCollectorData
import com.example.tayawaste.firebase.FirebaseManager
import com.example.tayawaste.ui.components.ChipStatus
import com.example.tayawaste.ui.components.IconCircle
import com.example.tayawaste.ui.components.TayaTextField
import com.example.tayawaste.ui.components.TayaTopBar

@Composable
fun PendingCollectorsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var collectorToReject by remember { mutableStateOf<PendingCollectorData?>(null) }
    var rejectionReason by remember { mutableStateOf("") }

    if (collectorToReject != null) {
        val target = collectorToReject!!
        AlertDialog(
            onDismissRequest = { collectorToReject = null },
            title = { Text("Reject Collector Registration") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Are you sure you want to reject ${target.name} (Business ID: ${target.businessId})?")
                    TayaTextField(
                        label = "Rejection Reason (Optional)",
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        placeholder = "e.g. Invalid business documentation or service area not covered"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val collector = target
                        collectorToReject = null
                        AppState.pendingCollectors.remove(collector)
                        FirebaseManager.rejectCollector(context, collector, rejectionReason)
                        rejectionReason = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Confirm Rejection")
                }
            },
            dismissButton = {
                TextButton(onClick = { collectorToReject = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        TayaTopBar("Pending Registrations", onBack = onBack)

        if (AppState.pendingCollectors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        "No pending collectors",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "All collector applications have been reviewed.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(AppState.pendingCollectors) { collector ->
                    PendingCollectorCard(
                        collector = collector,
                        onApprove = {
                            AppState.collectors.add(
                                0,
                                CollectorData(
                                    name = collector.name,
                                    area = collector.area,
                                    types = collector.wasteTypes,
                                    businessId = collector.businessId,
                                    status = ChipStatus.AVAILABLE,
                                    icon = Icons.Filled.Engineering
                                )
                            )
                            AppState.pendingCollectors.remove(collector)
                            FirebaseManager.approveCollectorWithEmailNotification(context, collector)
                        },
                        onReject = {
                            rejectionReason = ""
                            collectorToReject = collector
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingCollectorCard(
    collector: PendingCollectorData,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconCircle(Icons.Filled.Engineering)
            Column(Modifier.weight(1f)) {
                Text(collector.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Business ID: ${collector.businessId}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Area: ${collector.area} • Email: ${collector.email}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onReject,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                modifier = Modifier.weight(1f)
            ) {
                Text("Reject")
            }

            Button(
                onClick = onApprove,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.weight(1f)
            ) {
                Text("Approve & Notify")
            }
        }
    }
}
