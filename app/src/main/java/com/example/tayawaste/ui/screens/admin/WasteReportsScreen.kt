package com.example.tayawaste.ui.screens.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.AppState
import com.example.tayawaste.CouncilReportData
import com.example.tayawaste.ui.components.IconCircle
import com.example.tayawaste.ui.components.StatusChip
import com.example.tayawaste.ui.components.TayaTopBar

@Composable
fun WasteReportsScreen(onBack: () -> Unit = {}) {
    Column(Modifier.fillMaxSize()) {
        TayaTopBar("Waste Reports", onBack = onBack)
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(AppState.councilReports) { report ->
                ReportCard(report)
            }
        }
    }
}

@Composable
private fun ReportCard(report: CouncilReportData) {
    val context = LocalContext.current

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
            IconCircle(Icons.Filled.Warning)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(report.type, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${report.location} • Reported by ${report.reportedBy}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Assigned Council: ${report.councilName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                if (report.description.isNotBlank()) {
                    Text(report.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (report.latitude != null && report.longitude != null) {
                    Text("GPS: ${"%.4f".format(report.latitude)}, ${"%.4f".format(report.longitude)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
                StatusChip(report.status, Modifier.padding(top = 4.dp))
            }
        }

        if (report.latitude != null && report.longitude != null) {
            OutlinedButton(
                onClick = {
                    val uri = Uri.parse("geo:${report.latitude},${report.longitude}?q=${report.latitude},${report.longitude}(${Uri.encode(report.type)})")
                    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                    try {
                        context.startActivity(mapIntent)
                    } catch (_: Exception) {
                        val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${report.latitude},${report.longitude}")
                        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.size(16.dp).padding(end = 4.dp))
                Text("Open Report Location in Maps", fontSize = 12.sp)
            }
        }
    }
}
