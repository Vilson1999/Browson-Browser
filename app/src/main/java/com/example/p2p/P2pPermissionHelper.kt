package com.example.p2p

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat

/**
 * Handles Wi-Fi Direct permissions and system toggles separately for each Android version:
 * - Android 13+ (API 33, 34, 35, 36): NEARBY_WIFI_DEVICES (no location needed)
 * - Android 10, 11, 12 (API 29..32): ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION + System GPS toggle ON
 * - Android 9 (API 28): ACCESS_COARSE_LOCATION & ACCESS_FINE_LOCATION + System GPS toggle ON
 */
object P2pPermissionHelper {

    fun getRequiredPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
            else -> {
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    fun hasPermissions(context: Context): Boolean {
        val permissions = getRequiredPermissions()
        return permissions.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * System Location toggle (GPS) is mandatory on Android 9, 10, 11, 12 for Wi-Fi Direct
     * peer discovery. On Android 13+, NEARBY_WIFI_DEVICES bypasses location.
     */
    fun isLocationRequirementActive(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
    }

    fun isLocationEnabled(context: Context): Boolean {
        if (!isLocationRequirementActive()) return true
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return true
        return LocationManagerCompat.isLocationEnabled(lm)
    }

    fun isWifiEnabled(context: Context): Boolean {
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        return wm?.isWifiEnabled == true
    }

    fun getAndroidVersionSummary(): String {
        val sdk = Build.VERSION.SDK_INT
        val release = Build.VERSION.RELEASE
        return when (sdk) {
            28 -> "Android 9 (Pie)"
            29 -> "Android 10 (Q)"
            30 -> "Android 11 (R)"
            31 -> "Android 12 (S)"
            32 -> "Android 12L (Sv2)"
            33 -> "Android 13 (Tiramisu)"
            34 -> "Android 14 (UpsideDownCake)"
            35 -> "Android 15 (VanillaIceCream)"
            36 -> "Android 16 (Baklava)"
            else -> "Android $release (API $sdk)"
        }
    }

    fun getPermissionRequirementsDescription(): String {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                "Android 13+ requires 'Nearby Wi-Fi Devices' permission to discover and connect to peer devices offline without internet."
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                "Android 10–12 requires 'Location' permission and System GPS to be turned ON in Android Settings for Wi-Fi Direct hardware scanning."
            }
            else -> {
                "Android 9 requires 'Location' permission and System GPS to scan for nearby peer devices over Wi-Fi."
            }
        }
    }

    fun openWifiSettings(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.startActivity(Intent(Settings.Panel.ACTION_WIFI).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            } else {
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        } catch (_: Exception) {
            context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    fun openLocationSettings(context: Context) {
        try {
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (_: Exception) {
            context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
