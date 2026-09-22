package com.rbn.qtsettings.services

import android.graphics.drawable.Icon
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import com.rbn.qtsettings.R
import com.rbn.qtsettings.utils.Constants
import com.rbn.qtsettings.utils.DebuggingSettingsReader
import com.rbn.qtsettings.utils.PermissionUtils

class WirelessDebuggingTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()

        if (!PermissionUtils.hasWriteSecureSettingsPermission(this)) {
            Toast.makeText(this, R.string.toast_permission_not_granted_adb, Toast.LENGTH_LONG)
                .show()
            Log.w("WirelessDebuggingTile", "WRITE_SECURE_SETTINGS permission not granted.")
            return
        }

        if (!DebuggingSettingsReader.isDeveloperOptionsEnabled(contentResolver)) {
            Toast.makeText(this, R.string.toast_developer_options_disabled, Toast.LENGTH_LONG)
                .show()
            updateTile()
            return
        }

        val isWirelessEnabled =
            Settings.Global.getInt(contentResolver, Constants.ADB_WIFI_ENABLED, 0) == 1
        val newState = if (isWirelessEnabled) 0 else 1

        try {
            Settings.Global.putInt(
                contentResolver,
                Constants.ADB_WIFI_ENABLED,
                newState
            )
            updateTile()
        } catch (e: Exception) {
            Log.e("WirelessDebuggingTile", "Error toggling Wireless Debugging", e)
            Toast.makeText(this, R.string.toast_error_saving_settings, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val isDevOptionsEnabled = DebuggingSettingsReader.isDeveloperOptionsEnabled(contentResolver)

        if (!isDevOptionsEnabled) {
            tile.state = Tile.STATE_UNAVAILABLE
            tile.label = getString(R.string.tile_label_wireless_debugging)
            tile.subtitle = getString(R.string.subtitle_dev_options_required)
            tile.icon = Icon.createWithResource(this, R.drawable.ic_wireless_debugging)
            tile.updateTile()
            return
        }

        val isWirelessEnabled =
            Settings.Global.getInt(contentResolver, Constants.ADB_WIFI_ENABLED, 0) == 1

        if (isWirelessEnabled) {
            tile.state = Tile.STATE_ACTIVE
            tile.label = getString(R.string.wireless_state_on)
            tile.icon = Icon.createWithResource(this, R.drawable.ic_wireless_debugging)
        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.label = getString(R.string.wireless_state_off)
            tile.icon = Icon.createWithResource(this, R.drawable.ic_wireless_debugging)
        }
        tile.subtitle = ""
        tile.updateTile()
    }
}
