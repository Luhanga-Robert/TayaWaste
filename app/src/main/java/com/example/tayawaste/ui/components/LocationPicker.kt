package com.example.tayawaste.ui.components

import android.Manifest
import android.content.Intent
import android.provider.Settings
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tayawaste.location.LocationRepository
import com.example.tayawaste.location.LocationState
import com.example.tayawaste.ui.theme.GreenLight

@Composable
fun LocationPicker(
    currentLat: Double?,
    currentLng: Double?,
    currentAddress: String,
    onLocationUpdated: (Double, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var locationState by remember { mutableStateOf<LocationState>(LocationState.Idle) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            LocationRepository.getCurrentLocation(context) { state ->
                locationState = state
                if (state is LocationState.Success) {
                    onLocationUpdated(state.latitude, state.longitude, state.address)
                }
            }
        } else {
            locationState = LocationState.PermissionDenied
        }
    }

    fun requestGps() {
        if (!LocationRepository.hasLocationPermission(context)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else if (!LocationRepository.isGpsEnabled(context)) {
            locationState = LocationState.LocationDisabled
        } else {
            LocationRepository.getCurrentLocation(context) { state ->
                locationState = state
                if (state is LocationState.Success) {
                    onLocationUpdated(state.latitude, state.longitude, state.address)
                }
            }
        }
    }

    // Auto-fetch GPS on initial composable load if null
    LaunchedEffect(Unit) {
        if (currentLat == null || currentLng == null) {
            requestGps()
        } else {
            locationState = LocationState.Success(currentLat, currentLng, currentAddress)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Map Preview Box displaying actual GPS location
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            when (val state = locationState) {
                is LocationState.Loading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                        Text(
                            "Acquiring live phone GPS location...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                is LocationState.Success -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    webViewClient = WebViewClient()
                                    webChromeClient = WebChromeClient()
                                    settings.apply {
                                        javaScriptEnabled = true
                                        domStorageEnabled = true
                                        databaseEnabled = true
                                        loadWithOverviewMode = true
                                        useWideViewPort = true
                                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    }
                                    addJavascriptInterface(object {
                                        @JavascriptInterface
                                        fun onLocationSelected(lat: Double, lng: Double) {
                                            val newAddress = LocationRepository.resolveAddress(ctx, lat, lng)
                                            locationState = LocationState.Success(lat, lng, newAddress)
                                            onLocationUpdated(lat, lng, newAddress)
                                        }
                                    }, "AndroidBridge")
                                }
                            },
                            update = { webView ->
                                val escapeAddr = state.address.replace("'", "\\'")
                                val htmlContent = """
                                    <!DOCTYPE html>
                                    <html>
                                    <head>
                                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                                        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
                                        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                                        <style>
                                            html, body, #map { height: 100%; width: 100%; margin: 0; padding: 0; background: #e2e8f0; }
                                            .leaflet-control-attribution { display: none; }
                                        </style>
                                    </head>
                                    <body>
                                        <div id="map"></div>
                                        <script>
                                            var map = L.map('map', {zoomControl: true}).setView([${state.latitude}, ${state.longitude}], 15);
                                            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                                                maxZoom: 19
                                            }).addTo(map);
                                            var marker = L.marker([${state.latitude}, ${state.longitude}], {draggable: true}).addTo(map);
                                            marker.bindPopup("${escapeAddr}").openPopup();

                                            marker.on('dragend', function(e) {
                                                var position = marker.getLatLng();
                                                if (window.AndroidBridge) {
                                                    window.AndroidBridge.onLocationSelected(position.lat, position.lng);
                                                }
                                            });

                                            map.on('click', function(e) {
                                                var lat = e.latlng.lat;
                                                var lng = e.latlng.lng;
                                                marker.setLatLng([lat, lng]);
                                                if (window.AndroidBridge) {
                                                    window.AndroidBridge.onLocationSelected(lat, lng);
                                                }
                                            });
                                        </script>
                                    </body>
                                    </html>
                                """.trimIndent()
                                webView.loadDataWithBaseURL("https://openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                is LocationState.PermissionDenied -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "Location Permission Required",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Please grant location permission to capture report GPS.",
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                is LocationState.LocationDisabled -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            Icons.Filled.GpsFixed,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "Device GPS is Turned Off",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Turn on location services in device settings.",
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                is LocationState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            state.message,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        // Details bar displaying live phone GPS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenLight)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (currentLat != null && currentLng != null) {
                Text(
                    "Live Phone GPS: ${"%.5f".format(currentLat)}, ${"%.5f".format(currentLng)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (currentAddress.isNotBlank()) {
                    Text(
                        "Resolved Address: $currentAddress",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    "No location captured yet.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (locationState) {
                    is LocationState.PermissionDenied -> {
                        Button(
                            onClick = { requestGps() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Grant Permission", fontSize = 12.sp)
                        }
                    }
                    is LocationState.LocationDisabled -> {
                        OutlinedButton(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            }
                        ) {
                            Text("Enable GPS in Settings", fontSize = 12.sp)
                        }
                    }
                    else -> {
                        OutlinedButton(
                            onClick = { requestGps() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Filled.MyLocation,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp).padding(end = 4.dp)
                            )
                            Text("Use My Current Location", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
