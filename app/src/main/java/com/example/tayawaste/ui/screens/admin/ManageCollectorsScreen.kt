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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.AppState
import com.example.tayawaste.CollectorData
import com.example.tayawaste.firebase.FirebaseManager
import com.example.tayawaste.ui.components.IconCircle
import com.example.tayawaste.ui.components.StatusChip
import com.example.tayawaste.ui.components.TayaTopBar

@Composable
fun ManageCollectorsScreen(onBack: () -> Unit = {}) {
    var collectorToRemove by remember { mutableStateOf<CollectorData?>(null) }

    if (collectorToRemove != null) {
        val target = collectorToRemove!!
        AlertDialog(
            onDismissRequest = { collectorToRemove = null },
            title = { Text("Remove Collector") },
            text = { Text("Are you sure you want to remove ${target.name}? They will no longer be visible to citizens.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        AppState.collectors.remove(target)
                        FirebaseManager.removeCollector(target.businessId)
                        collectorToRemove = null
                    }
                ) {
                    Text("Remove", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { collectorToRemove = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        TayaTopBar("Manage Collectors", onBack = onBack)

        if (AppState.collectors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No approved waste collectors found.",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(AppState.collectors) { collector ->
                    AdminCollectorCard(
                        collector = collector,
                        onRemoveClick = { collectorToRemove = collector }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminCollectorCard(
    collector: CollectorData,
    onRemoveClick: () -> Unit
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
            IconCircle(collector.icon)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(collector.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Business ID: ${collector.businessId}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Area: ${collector.area}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Types: ${collector.types}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                StatusChip(collector.status, Modifier.padding(top = 4.dp))
            }
        }

        OutlinedButton(
            onClick = onRemoveClick,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
            Text("Remove Collector")
        }
    }
}
