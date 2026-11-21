package com.example.tayawaste.ui.screens.collector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tayawaste.AppState
import com.example.tayawaste.firebase.FirebaseManager
import com.example.tayawaste.ui.components.DropdownField
import com.example.tayawaste.ui.components.PrimaryButton
import com.example.tayawaste.ui.components.TayaTextField
import com.example.tayawaste.ui.components.TayaTopBar

@Composable
fun UpdateAvailabilityScreen(
    onBack: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    var serviceArea by remember { mutableStateOf(AppState.serviceArea) }
    var wasteTypes by remember { mutableStateOf(AppState.wasteTypes) }
    var availability by remember { mutableStateOf(AppState.availability) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Availability Updated") },
            text = { Text("Your working schedule and service areas have been saved.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onSubmitSuccess()
                }) {
                    Text("OK")
                }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        TayaTopBar("Update Availability", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TayaTextField(
                label = "Service Area",
                value = serviceArea,
                onValueChange = { serviceArea = it },
                placeholder = "Enter your service area (e.g. Area 25, Kawale, Lilongwe)",
                leadingIcon = Icons.Filled.LocationOn
            )

            DropdownField(
                label = "Waste Types Handled",
                placeholder = "Select waste types",
                options = listOf("Plastic, Paper, General", "Organic, General", "Plastic, Metal, General", "All Types"),
                selected = wasteTypes,
                onSelected = { wasteTypes = it },
                leadingIcon = Icons.Filled.Recycling
            )

            DropdownField(
                label = "Working Hours & Schedule",
                placeholder = "Select availability schedule",
                options = listOf("Mon - Sat (8:00 AM - 5:00 PM)", "Weekdays Only", "Full Time", "On Demand"),
                selected = availability,
                onSelected = { availability = it },
                leadingIcon = Icons.Filled.CalendarMonth
            )

            PrimaryButton(
                text = "Save Schedule",
                onClick = {
                    AppState.serviceArea = serviceArea
                    AppState.wasteTypes = wasteTypes
                    AppState.availability = availability
                    FirebaseManager.updateAvailability(
                        businessId = AppState.businessId,
                        area = serviceArea,
                        types = wasteTypes,
                        availability = availability
                    )
                    showDialog = true
                },
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
