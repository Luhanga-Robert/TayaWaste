package com.example.tayawaste

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.tayawaste.ui.components.ChipStatus

enum class UserRole {
    CITIZEN,
    COLLECTOR,
    ADMIN
}

data class CollectorData(
    val name: String,
    val area: String,
    val types: String,
    val businessId: String = "CW-00123",
    val status: ChipStatus = ChipStatus.AVAILABLE,
    val icon: ImageVector = Icons.Filled.Eco
)

data class RequestData(
    val id: String = "",
    val title: String,
    val dateTime: String,
    val collector: String,
    val requestedBy: String = "John Moyo",
    val description: String = "",
    val status: ChipStatus,
    val location: String = "Lilongwe City",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String = ""
)

data class CouncilReportData(
    val id: String = "",
    val type: String,
    val location: String,
    val councilName: String = "Lilongwe City Council",
    val description: String,
    val reportedBy: String,
    val status: ChipStatus = ChipStatus.PENDING,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String = ""
)

data class PendingCollectorData(
    val name: String,
    val businessId: String,
    val email: String,
    val area: String,
    val wasteTypes: String,
    val availability: String
)

object AppState {
    var selectedRole: UserRole by mutableStateOf(UserRole.CITIZEN)
    var isLoggedIn: Boolean by mutableStateOf(false)

    var userName: String by mutableStateOf("John Doe")
    var userEmail: String by mutableStateOf("johndoe@tayawaste.com")
    var userLocation: String by mutableStateOf("Area 25, Lilongwe")

    // Collector profile info
    var businessId: String by mutableStateOf("CW-00123")
    var serviceArea: String by mutableStateOf("Lilongwe City")
    var wasteTypes: String by mutableStateOf("Plastic, Paper, General")
    var availability: String by mutableStateOf("Mon - Sat (8:00 AM - 5:00 PM)")

    // Selection state for forms
    var selectedCollector: String by mutableStateOf("Green Future Services")

    // Collections / Data (Populated in Real-time from Firebase Firestore)
    val collectors = mutableStateListOf<CollectorData>()
    val requests = mutableStateListOf<RequestData>()
    val councilReports = mutableStateListOf<CouncilReportData>()
    val pendingCollectors = mutableStateListOf<PendingCollectorData>()
}
