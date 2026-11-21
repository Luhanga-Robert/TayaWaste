package com.example.tayawaste.navigation

object Routes {
    // Common / Auth
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"

    // Citizen
    const val CITIZEN_REGISTER = "citizen_register"
    const val CITIZEN_HOME = "citizen_home"
    const val COLLECTORS_LIST = "collectors_list"
    const val REQUEST_COLLECTION = "request_collection"
    const val REPORT_PROBLEM = "report_problem"
    const val MY_REQUESTS = "my_requests"
    const val CITIZEN_PROFILE = "citizen_profile"

    // Collector
    const val COLLECTOR_REGISTER = "collector_register"
    const val COLLECTOR_DASHBOARD = "collector_dashboard"
    const val COLLECTOR_REQUESTS = "collector_requests"
    const val UPDATE_AVAILABILITY = "update_availability"
    const val COLLECTOR_PROFILE = "collector_profile"

    // Admin
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val CHANGE_PASSWORD = "change_password"
    const val PENDING_COLLECTORS = "pending_collectors"
    const val MANAGE_COLLECTORS = "manage_collectors"
    const val WASTE_REPORTS = "waste_reports"
    const val ADMIN_PROFILE = "admin_profile"
}
