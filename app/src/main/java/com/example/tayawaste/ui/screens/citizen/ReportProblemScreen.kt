package com.example.tayawaste.ui.screens.citizen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.tayawaste.AppState
import com.example.tayawaste.CouncilReportData
import com.example.tayawaste.firebase.FirebaseManager
import com.example.tayawaste.location.LocationRepository
import com.example.tayawaste.ui.components.ChipStatus
import com.example.tayawaste.ui.components.DropdownField
import com.example.tayawaste.ui.components.IconCircle
import com.example.tayawaste.ui.components.LocationPicker
import com.example.tayawaste.ui.components.PrimaryButton
import com.example.tayawaste.ui.components.TayaTextField
import com.example.tayawaste.ui.components.TayaTopBar
import com.example.tayawaste.ui.theme.GreenLight

@Composable
fun ReportProblemScreen(
    onBack: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    var problemType by remember { mutableStateOf("") }
    var locationAddress by remember { mutableStateOf(AppState.userLocation) }
    var capturedLat by remember { mutableStateOf<Double?>(null) }
    var capturedLng by remember { mutableStateOf<Double?>(null) }
    var description by remember { mutableStateOf("") }
    var capturedPhoto by remember { mutableStateOf<Bitmap?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedPhoto = bitmap
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    fun openCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(null)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val assignedCouncil = LocationRepository.determineCouncil(capturedLat, capturedLng, locationAddress)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Report Sent") },
            text = { Text("Your waste report has been routed and submitted to $assignedCouncil with live GPS coordinates.") },
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
        TayaTopBar("Report Waste Problem", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DropdownField(
                label = "Problem Type",
                placeholder = "Select problem type",
                options = listOf("Illegal dumping", "Missed collection", "Overflowing bin", "Hazardous waste", "Other"),
                selected = problemType,
                onSelected = { problemType = it }
            )

            Column {
                Text(
                    "Problem Location (GPS)",
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

            Surface(
                color = GreenLight,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            "Assigned Authority: $assignedCouncil",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Routed automatically based on live GPS location.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            TayaTextField(
                label = "Description",
                value = description,
                onValueChange = { description = it },
                placeholder = "Describe the problem...",
                minLines = 4
            )

            // Camera photo capture
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (capturedPhoto != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            bitmap = capturedPhoto!!.asImageBitmap(),
                            contentDescription = "Captured Waste Photo",
                            modifier = Modifier
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(onClick = { capturedPhoto = null }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remove photo", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { openCamera() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Capture Photo with Camera", fontSize = 14.sp)
                    }
                }
            }

            if (validationError.isNotBlank()) {
                Text(
                    validationError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            PrimaryButton(
                text = "Submit Report",
                isLoading = isLoading,
                onClick = {
                    if (problemType.isBlank()) {
                        validationError = "Please select a problem type."
                    } else if (description.isBlank()) {
                        validationError = "Please enter a description of the problem."
                    } else if (capturedLat == null || capturedLng == null) {
                        validationError = "Valid GPS location is required to submit a report. Please tap 'Use My Current Location'."
                    } else {
                        isLoading = true
                        val newReport = CouncilReportData(
                            type = problemType,
                            location = locationAddress.ifBlank { "Lilongwe" },
                            councilName = assignedCouncil,
                            description = description,
                            reportedBy = AppState.userName,
                            status = ChipStatus.PENDING,
                            latitude = capturedLat,
                            longitude = capturedLng,
                            address = locationAddress
                        )
                        com.example.tayawaste.offline.TayaOfflineRepository.saveWasteReportOffline(context, newReport)
                        isLoading = false
                        showDialog = true
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
