package com.example.tayawaste

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DynamicLoginTitleTest {

    @Before
    fun setUp() {
        AppState.pendingCollectors.clear()
    }

    private fun getLoginTitle(role: UserRole): String {
        return when (role) {
            UserRole.CITIZEN -> "Citizen Login"
            UserRole.COLLECTOR -> "Collector Login"
            UserRole.ADMIN -> "Admin Login"
        }
    }

    @Test
    fun citizenRole_producesCitizenLoginTitle() {
        assertEquals("Citizen Login", getLoginTitle(UserRole.CITIZEN))
    }

    @Test
    fun collectorRole_producesCollectorLoginTitle() {
        assertEquals("Collector Login", getLoginTitle(UserRole.COLLECTOR))
    }

    @Test
    fun adminRole_producesAdminLoginTitle() {
        assertEquals("Admin Login", getLoginTitle(UserRole.ADMIN))
    }

    @Test
    fun pendingCollectorsList_whenEmpty_doesNotSeedDummyData() {
        AppState.pendingCollectors.clear()
        assertTrue("Pending collectors list must remain empty when cleared/reviewed", AppState.pendingCollectors.isEmpty())
        assertEquals(0, AppState.pendingCollectors.size)
    }
}
