package com.example.tayawaste.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale

sealed interface LocationState {
    object Idle : LocationState
    object Loading : LocationState
    data class Success(val latitude: Double, val longitude: Double, val address: String) : LocationState
    object PermissionDenied : LocationState
    object LocationDisabled : LocationState
    data class Error(val message: String) : LocationState
}

object LocationRepository {
    private const val TAG = "LocationRepository"

    fun hasLocationPermission(context: Context): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
    }

    fun getCurrentLocation(context: Context, onResult: (LocationState) -> Unit) {
        if (!hasLocationPermission(context)) {
            onResult(LocationState.PermissionDenied)
            return
        }

        if (!isGpsEnabled(context)) {
            onResult(LocationState.LocationDisabled)
            return
        }

        onResult(LocationState.Loading)

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lat = location.latitude
                    val lng = location.longitude
                    val address = resolveAddress(context, lat, lng)
                    onResult(LocationState.Success(lat, lng, address))
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            val lat = lastLoc.latitude
                            val lng = lastLoc.longitude
                            val address = resolveAddress(context, lat, lng)
                            onResult(LocationState.Success(lat, lng, address))
                        } else {
                            // Fallback to default Lilongwe location so user is never blocked
                            val defaultLat = -13.9743
                            val defaultLng = 33.7872
                            val defaultAddress = resolveAddress(context, defaultLat, defaultLng)
                            onResult(LocationState.Success(defaultLat, defaultLng, defaultAddress))
                        }
                    }.addOnFailureListener {
                        val defaultLat = -13.9743
                        val defaultLng = 33.7872
                        val defaultAddress = resolveAddress(context, defaultLat, defaultLng)
                        onResult(LocationState.Success(defaultLat, defaultLng, defaultAddress))
                    }
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error getting location, using default Malawi GPS", e)
                val defaultLat = -13.9743
                val defaultLng = 33.7872
                val defaultAddress = resolveAddress(context, defaultLat, defaultLng)
                onResult(LocationState.Success(defaultLat, defaultLng, defaultAddress))
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException", e)
            onResult(LocationState.PermissionDenied)
        } catch (e: Exception) {
            Log.e(TAG, "Exception getting location", e)
            onResult(LocationState.Error(e.localizedMessage ?: "Location request error."))
        }
    }

    fun determineCouncil(latitude: Double?, longitude: Double?, address: String): String {
        val addrUpper = address.uppercase(Locale.ROOT)
        if (addrUpper.contains("MZUZU") || (latitude != null && latitude > -12.5)) {
            return "Mzuzu City Council"
        }
        if (addrUpper.contains("ZOMBA") || (latitude != null && longitude != null && latitude <= -15.0 && latitude > -15.6 && longitude > 35.1)) {
            return "Zomba City Council"
        }
        if (addrUpper.contains("BLANTYRE") || (latitude != null && latitude <= -15.5)) {
            return "Blantyre City Council"
        }
        if (addrUpper.contains("LILONGWE") || (latitude != null && latitude <= -12.5 && latitude > -15.0)) {
            return "Lilongwe City Council"
        }
        return when {
            addrUpper.contains("NORTH") -> "Mzuzu City Council"
            addrUpper.contains("SOUTH") -> "Blantyre City Council"
            else -> "Lilongwe City Council"
        }
    }

    fun resolveAddress(context: Context, latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val feature = addr.subLocality ?: addr.locality ?: addr.thoroughfare ?: ""
                    val city = addr.locality ?: addr.adminArea ?: fallbackCityName(latitude, longitude)
                    if (feature.isNotBlank() && feature != city) "$feature, $city" else city
                } else {
                    fallbackCityName(latitude, longitude)
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val feature = addr.subLocality ?: addr.locality ?: addr.thoroughfare ?: ""
                    val city = addr.locality ?: addr.adminArea ?: fallbackCityName(latitude, longitude)
                    if (feature.isNotBlank() && feature != city) "$feature, $city" else city
                } else {
                    fallbackCityName(latitude, longitude)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Geocoding failed", e)
            fallbackCityName(latitude, longitude)
        }
    }

    private fun fallbackCityName(latitude: Double, longitude: Double): String {
        return when {
            latitude > -12.5 -> "Mzuzu, Northern Region, Malawi"
            latitude <= -15.0 && latitude > -15.6 && longitude > 35.1 -> "Zomba, Southern Region, Malawi"
            latitude <= -15.5 -> "Blantyre, Southern Region, Malawi"
            else -> "Lilongwe, Central Region, Malawi"
        }
    }
}
