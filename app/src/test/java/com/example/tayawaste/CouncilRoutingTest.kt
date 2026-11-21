package com.example.tayawaste

import com.example.tayawaste.location.LocationRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class CouncilRoutingTest {

    @Test
    fun determineCouncil_whenInMzuzu_routesToMzuzuCityCouncil() {
        val lat = -11.4583
        val lng = 34.0151
        val address = "Luwinga, Mzuzu, Northern Region, Malawi"

        val council = LocationRepository.determineCouncil(lat, lng, address)

        assertEquals("Mzuzu City Council", council)
    }

    @Test
    fun determineCouncil_whenInZomba_routesToZombaCityCouncil() {
        val lat = -15.3833
        val lng = 35.3333
        val address = "Chikanda, Zomba, Southern Region, Malawi"

        val council = LocationRepository.determineCouncil(lat, lng, address)

        assertEquals("Zomba City Council", council)
    }

    @Test
    fun determineCouncil_whenInBlantyre_routesToBlantyreCityCouncil() {
        val lat = -15.7861
        val lng = 35.0058
        val address = "Limbe, Blantyre, Southern Region, Malawi"

        val council = LocationRepository.determineCouncil(lat, lng, address)

        assertEquals("Blantyre City Council", council)
    }

    @Test
    fun determineCouncil_whenInLilongwe_routesToLilongweCityCouncil() {
        val lat = -13.9743
        val lng = 33.7872
        val address = "Area 25, Lilongwe, Central Region, Malawi"

        val council = LocationRepository.determineCouncil(lat, lng, address)

        assertEquals("Lilongwe City Council", council)
    }
}
