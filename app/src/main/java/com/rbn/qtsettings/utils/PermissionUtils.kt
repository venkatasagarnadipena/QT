package com.rbn.qtsettings.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.edit
import com.rbn.qtsettings.data.WifiIdentityAccessState
import java.io.File

object PermissionUtils {

    fun hasWriteSecureSettingsPermission(context: Context): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
    }

    fun hasPreciseLocationPermission(context: Context): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun hasBackgroundLocationPermission(context: Context): Boolean =
        hasPreciseLocationPermission(context) &&
            context.checkSelfPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    // Device-local permission state; deliberately excluded from exported settings backups.
    fun setWifiLocationPermissionBlocked(context: Context, blocked: Boolean) {
        context.getSharedPreferences("wifi_location_permission", Context.MODE_PRIVATE).edit {
            putBoolean("blocked", blocked)
        }
    }

    fun openAppPermissionSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(LocationManager::class.java) ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            @Suppress("DEPRECATION")
            try {
                val mode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
                mode != Settings.Secure.LOCATION_MODE_OFF
            } catch (e: Exception) {
                false
            }
        }
    }

    fun canAccessWifiSsid(context: Context): Boolean {
        return getWifiIdentityAccessState(context) == WifiIdentityAccessState.AVAILABLE
    }

    fun getWifiIdentityAccessState(context: Context): WifiIdentityAccessState {
        return when {
            !hasPreciseLocationPermission(context) -> {
                if (context.getSharedPreferences("wifi_location_permission", Context.MODE_PRIVATE)
                        .getBoolean("blocked", false)
                ) {
                    WifiIdentityAccessState.PRECISE_LOCATION_PERMISSION_BLOCKED
                } else {
                    WifiIdentityAccessState.PRECISE_LOCATION_PERMISSION_REQUIRED
                }
            }

            !isLocationEnabled(context) -> WifiIdentityAccessState.LOCATION_SERVICES_DISABLED
            else -> WifiIdentityAccessState.AVAILABLE
        }
    }

    fun isDeveloperOptionsEnabled(context: Context): Boolean {
        return DebuggingSettingsReader.isDeveloperOptionsEnabled(context.contentResolver)
    }

    fun isDeviceRooted(): Boolean {
        val suBinaries = arrayOf(
            "/sbin/su", "/system/bin/su", "/system/xbin/su",
            "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"
        )
        for (path in suBinaries) {
            if (File(path).exists()) {
                return true
            }
        }
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))

            process.inputStream.bufferedReader().use { it.readText() }
            process.errorStream.bufferedReader().use { it.readText() }
            val exitValue = process.waitFor()
            exitValue == 0
        } catch (e: Exception) {
            false
        } finally {
            process?.destroy()
        }
    }
}
