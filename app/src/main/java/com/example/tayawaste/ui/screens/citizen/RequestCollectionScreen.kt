package com.example.tayawaste.ui.screens.citizen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.AppState
import com.example.tayawaste.RequestData
import com.example.tayawaste.firebase.FirebaseManager
import com.example.tayawaste.ui.components.ChipStatus
import com.example.tayawaste.ui.components.DropdownField
import com.example.tayawaste.ui.components.LocationPicker
import com.example.tayawaste.ui.components.PrimaryButton
import com.example.tayawaste.ui.components.TayaTextField
import com.example.tayawaste.ui.components.TayaTopBar

@Composable
fun RequestCollectionScreen(
    collector: String = "Green Future Services",
    defaultLocation: String = "Lilongwe City",
    onBack: () -> Unit = {},
    onChangeCollector: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    var wasteType by remember { mutableStateOf("") }
    var locationAddress by remember { mutableStateOf(defaultLocation) }
    var capturedLat by remember { mutableStateOf<Double?>(null) }
    var capturedLng by remember { mutableStateOf<Double?>(null) }
    var dateTime by remember { mutableStateOf("") }
    var additionalInfo by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Request Submitted") },
            text = { Text("Your collection request has been sent to $collector.") },
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
        TayaTopBar("Request Collection", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DropdownField(
                label = "Waste Type",
                placeholder = "Select waste type",
                options = listOf("Plastic", "Paper", "Organic", "Metal", "General"),
                selected = wasteType,
                onSelected = { wasteType = it }
            )

            Column {
                Text(
                    "Real-Time Location (GPS)",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LocationPicker(
                    currentLat = capturedLat,
                    currentLng = capturedLng,
                    currentAddress = locationAddress,
                    onLocationUpdated = { lat, lng, address ->
                        capturedLat = lat
                        capturedLng = lng
                        locationAddress = address
                        validationError = ""
                    }
                )
            }

            TayaTextField(
                label = "Preferred Date & Time",
                value = dateTime,
                onValueChange = { dateTime = it },
                placeholder = "Select date and time",
                leadingIcon = Icons.Filled.CalendarMonth
            )

            TayaTextField(
                label = "Additional Information (optional)",
                value = additionalInfo,
                onValueChange = { additionalInfo = it },
                placeholder = "e.g. Special instructions...",
                minLines = 3
            )

            if (validationError.isNotBlank()) {
                Text(
                    validationError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            PrimaryButton(
                text = "Submit Request",
                isLoading = isLoading,
                onClick = {
                    if (wasteType.isBlank()) {
                        validationError = "Please select a waste type."
                    } else if (dateTime.isBlank()) {
                        validationError = "Please enter preferred date & time."
                    } else if (capturedLat == null || capturedLng == null) {
                        validationError = "Valid GPS location is required to submit a collection request. Please tap 'Use My Current Location'."
                    } else {
                        isLoading = true
                        val newReq = RequestData(
                            title = "$wasteType Waste Collection",
                            dateTime = dateTime,
                            collector = collector,
                            requestedBy = AppState.userName,
                            description = additionalInfo,
                            status = ChipStatus.PENDING,
                            location = locationAddress.ifBlank { "Lilongwe City" },
                            latitude = capturedLat,
                            longitude = capturedLng,
                            address = locationAddress
                        )
                        com.example.tayawaste.offline.TayaOfflineRepository.saveCollectionRequestOffline(context, newReq)
                        isLoading = false
                        showDialog = true
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
