package com.example.tayawaste.ui.screens.citizen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.AppState
import com.example.tayawaste.ui.components.CollectorCard
import com.example.tayawaste.ui.components.TayaTopBar

@Composable
fun CollectorsListScreen(
    selecting: Boolean = false,
    onBack: () -> Unit = {},
    onCollectorClick: (collectorName: String) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val filtered = AppState.collectors.filter {
        it.name.contains(query, ignoreCase = true) ||
                it.area.contains(query, ignoreCase = true) ||
                it.types.contains(query, ignoreCase = true)
    }

    Column(Modifier.fillMaxSize()) {
        TayaTopBar(if (selecting) "Choose a Collector" else "Waste Collectors", onBack = onBack)
        if (selecting) {
            Text(
                "Select the collector you want to send your request to.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp)
            )
        }
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search by name, area or waste type...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered) { c ->
                CollectorCard(
                    name = c.name,
                    area = c.area,
                    types = c.types,
                    icon = c.icon,
                    status = c.status,
                    onClick = { onCollectorClick(c.name) }
                )
            }
        }
    }
}
