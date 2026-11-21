package com.example.tayawaste.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tayawaste.AppState
import com.example.tayawaste.UserRole
import com.example.tayawaste.ui.components.BottomNavItem
import com.example.tayawaste.ui.components.TayaBottomBar
import com.example.tayawaste.ui.components.adminTabs
import com.example.tayawaste.ui.components.citizenTabs
import com.example.tayawaste.ui.components.collectorTabs
import com.example.tayawaste.ui.screens.admin.AdminDashboardScreen
import com.example.tayawaste.ui.screens.admin.AdminProfileScreen
import com.example.tayawaste.ui.screens.admin.ChangePasswordScreen
import com.example.tayawaste.ui.screens.admin.ManageCollectorsScreen
import com.example.tayawaste.ui.screens.admin.PendingCollectorsScreen
import com.example.tayawaste.ui.screens.admin.WasteReportsScreen
import com.example.tayawaste.ui.screens.citizen.CitizenHomeScreen
import com.example.tayawaste.ui.screens.citizen.CitizenProfileScreen
import com.example.tayawaste.ui.screens.citizen.CitizenRegistrationScreen
import com.example.tayawaste.ui.screens.citizen.CollectorsListScreen
import com.example.tayawaste.ui.screens.citizen.MyRequestsScreen
import com.example.tayawaste.ui.screens.citizen.ReportProblemScreen
import com.example.tayawaste.ui.screens.citizen.RequestCollectionScreen
import com.example.tayawaste.ui.screens.collector.CollectorDashboardScreen
import com.example.tayawaste.ui.screens.collector.CollectorProfileScreen
import com.example.tayawaste.ui.screens.collector.CollectorRegistrationScreen
import com.example.tayawaste.ui.screens.collector.CollectorRequestsScreen
import com.example.tayawaste.ui.screens.collector.UpdateAvailabilityScreen
import com.example.tayawaste.ui.screens.common.ForgotPasswordScreen
import com.example.tayawaste.ui.screens.common.LoginScreen
import com.example.tayawaste.ui.screens.common.SplashScreen
import com.example.tayawaste.ui.screens.common.WelcomeScreen
import kotlinx.coroutines.delay

@Composable
fun AuthGuard(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    if (!AppState.isLoggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate(Routes.WELCOME) {
                popUpTo(0) { inclusive = true }
            }
        }
    } else {
        content()
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Determine bottom navigation tab set based on active role & current screen
    val tabs: List<BottomNavItem>? = if (AppState.isLoggedIn) {
        when (AppState.selectedRole) {
            UserRole.CITIZEN -> if (currentRoute in citizenTabs.map { it.route }) citizenTabs else null
            UserRole.COLLECTOR -> if (currentRoute in collectorTabs.map { it.route }) collectorTabs else null
            UserRole.ADMIN -> if (currentRoute in adminTabs.map { it.route }) adminTabs else null
        }
    } else null

    Scaffold(
        bottomBar = {
            if (tabs != null) {
                TayaBottomBar(
                    items = tabs,
                    currentRoute = currentRoute,
                    onItemClick = { item -> navController.navigateTab(item.route) }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(padding)
        ) {
            // Splash Screen -> Welcome Screen
            composable(Routes.SPLASH) {
                LaunchedEffect(Unit) {
                    delay(1200)
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
                SplashScreen()
            }

            // Welcome Screen (Main Login Screen: Select Role)
            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onSelectRole = { role ->
                        AppState.selectedRole = role
                        navController.navigate(Routes.LOGIN)
                    }
                )
            }

            // Login Screen
            composable(Routes.LOGIN) {
                LoginScreen(
                    role = AppState.selectedRole,
                    onBack = { navController.popBackStack() },
                    onLoginSuccess = {
                        AppState.isLoggedIn = true
                        val destination = when (AppState.selectedRole) {
                            UserRole.CITIZEN -> Routes.CITIZEN_HOME
                            UserRole.COLLECTOR -> Routes.COLLECTOR_DASHBOARD
                            UserRole.ADMIN -> Routes.ADMIN_DASHBOARD
                        }
                        navController.navigate(destination) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        val destination = when (AppState.selectedRole) {
                            UserRole.CITIZEN -> Routes.CITIZEN_REGISTER
                            UserRole.COLLECTOR -> Routes.COLLECTOR_REGISTER
                            UserRole.ADMIN -> Routes.CITIZEN_REGISTER
                        }
                        navController.navigate(destination)
                    },
                    onForgotPasswordClick = {
                        navController.navigate(Routes.FORGOT_PASSWORD)
                    }
                )
            }

            // Forgot Password Screen for Citizen and Collector
            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    role = AppState.selectedRole,
                    onBack = { navController.popBackStack() },
                    onResetSuccess = { navController.popBackStack() }
                )
            }

            // --- Citizen Flow ---
            composable(Routes.CITIZEN_REGISTER) {
                CitizenRegistrationScreen(
                    onBack = { navController.popBackStack() },
                    onRegisterSuccess = { name, email, location ->
                        AppState.userName = name
                        AppState.userEmail = email
                        AppState.userLocation = location
                        AppState.isLoggedIn = true
                        AppState.selectedRole = UserRole.CITIZEN
                        navController.navigate(Routes.CITIZEN_HOME) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.CITIZEN_HOME) {
                AuthGuard(navController) {
                    CitizenHomeScreen(
                        userName = AppState.userName,
                        onFindCollectors = { navController.navigate(Routes.COLLECTORS_LIST) },
                        onRequestCollection = { navController.navigate(Routes.COLLECTORS_LIST) },
                        onReportProblem = { navController.navigate(Routes.REPORT_PROBLEM) },
                        onMyRequests = { navController.navigate(Routes.MY_REQUESTS) },
                        onProfileClick = { navController.navigate(Routes.CITIZEN_PROFILE) }
                    )
                }
            }

            composable(Routes.COLLECTORS_LIST) {
                AuthGuard(navController) {
                    CollectorsListScreen(
                        onBack = { navController.popBackStack() },
                        onCollectorClick = { collectorName ->
                            AppState.selectedCollector = collectorName
                            navController.navigate(Routes.REQUEST_COLLECTION)
                        }
                    )
                }
            }

            composable(Routes.REQUEST_COLLECTION) {
                AuthGuard(navController) {
                    RequestCollectionScreen(
                        collector = AppState.selectedCollector,
                        defaultLocation = AppState.userLocation,
                        onBack = { navController.popBackStack() },
                        onChangeCollector = { navController.popBackStack() },
                        onSubmitSuccess = { navController.navigateTab(Routes.MY_REQUESTS) }
                    )
                }
            }

            composable(Routes.REPORT_PROBLEM) {
                AuthGuard(navController) {
                    ReportProblemScreen(
                        onBack = { navController.popBackStack() },
                        onSubmitSuccess = { navController.popBackStack() }
                    )
                }
            }

            composable(Routes.MY_REQUESTS) {
                AuthGuard(navController) {
                    MyRequestsScreen()
                }
            }

            composable(Routes.CITIZEN_PROFILE) {
                AuthGuard(navController) {
                    CitizenProfileScreen(
                        onLogoutClick = {
                            AppState.isLoggedIn = false
                            navController.navigate(Routes.WELCOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            // --- Collector Flow ---
            composable(Routes.COLLECTOR_REGISTER) {
                CollectorRegistrationScreen(
                    onBack = { navController.popBackStack() },
                    onRegisterSubmitted = {
                        AppState.isLoggedIn = false
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.COLLECTOR_DASHBOARD) {
                AuthGuard(navController) {
                    CollectorDashboardScreen(
                        onViewRequests = { navController.navigate(Routes.COLLECTOR_REQUESTS) },
                        onUpdateAvailability = { navController.navigate(Routes.UPDATE_AVAILABILITY) },
                        onViewProfile = { navController.navigate(Routes.COLLECTOR_PROFILE) }
                    )
                }
            }

            composable(Routes.COLLECTOR_REQUESTS) {
                AuthGuard(navController) {
                    CollectorRequestsScreen()
                }
            }

            composable(Routes.UPDATE_AVAILABILITY) {
                AuthGuard(navController) {
                    UpdateAvailabilityScreen(
                        onBack = { navController.popBackStack() },
                        onSubmitSuccess = { navController.popBackStack() }
                    )
                }
            }

            composable(Routes.COLLECTOR_PROFILE) {
                AuthGuard(navController) {
                    CollectorProfileScreen(
                        onLogoutClick = {
                            AppState.isLoggedIn = false
                            navController.navigate(Routes.WELCOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            // --- Admin Flow ---
            composable(Routes.ADMIN_DASHBOARD) {
                AuthGuard(navController) {
                    AdminDashboardScreen(
                        onReviewPending = { navController.navigate(Routes.PENDING_COLLECTORS) },
                        onManageCollectors = { navController.navigate(Routes.MANAGE_COLLECTORS) },
                        onViewReports = { navController.navigate(Routes.WASTE_REPORTS) },
                        onChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) }
                    )
                }
            }

            composable(Routes.CHANGE_PASSWORD) {
                AuthGuard(navController) {
                    ChangePasswordScreen(
                        onBack = { navController.popBackStack() },
                        onSubmitSuccess = { navController.popBackStack() }
                    )
                }
            }

            composable(Routes.PENDING_COLLECTORS) {
                AuthGuard(navController) {
                    PendingCollectorsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Routes.MANAGE_COLLECTORS) {
                AuthGuard(navController) {
                    ManageCollectorsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Routes.WASTE_REPORTS) {
                AuthGuard(navController) {
                    WasteReportsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Routes.ADMIN_PROFILE) {
                AuthGuard(navController) {
                    AdminProfileScreen(
                        onLogoutClick = {
                            AppState.isLoggedIn = false
                            navController.navigate(Routes.WELCOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun NavHostController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(Routes.WELCOME)
        launchSingleTop = true
    }
}
