package com.example.tayawaste.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.tayawaste.navigation.Routes
import com.example.tayawaste.ui.theme.GreenLight

data class BottomNavItem(val label: String, val route: String, val icon: ImageVector)

val citizenTabs = listOf(
    BottomNavItem("Home", Routes.CITIZEN_HOME, Icons.Filled.Home),
    BottomNavItem("Collectors", Routes.COLLECTORS_LIST, Icons.Filled.Groups),
    BottomNavItem("Reports", Routes.MY_REQUESTS, Icons.Filled.ListAlt),
    BottomNavItem("Profile", Routes.CITIZEN_PROFILE, Icons.Filled.Person)
)

val collectorTabs = listOf(
    BottomNavItem("Home", Routes.COLLECTOR_DASHBOARD, Icons.Filled.Home),
    BottomNavItem("Requests", Routes.COLLECTOR_REQUESTS, Icons.AutoMirrored.Filled.Assignment),
    BottomNavItem("Profile", Routes.COLLECTOR_PROFILE, Icons.Filled.Person)
)

val adminTabs = listOf(
    BottomNavItem("Home", Routes.ADMIN_DASHBOARD, Icons.Filled.Home),
    BottomNavItem("Collectors", Routes.MANAGE_COLLECTORS, Icons.Filled.Groups),
    BottomNavItem("Reports", Routes.WASTE_REPORTS, Icons.Filled.Warning),
    BottomNavItem("Profile", Routes.ADMIN_PROFILE, Icons.Filled.Person)
)

@Composable
fun TayaBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onItemClick(item) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = GreenLight
                )
            )
        }
    }
}
