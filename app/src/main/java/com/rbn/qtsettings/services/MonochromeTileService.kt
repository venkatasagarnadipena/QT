package com.rbn.qtsettings.services

import android.graphics.drawable.Icon
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import com.rbn.qtsettings.R
import com.rbn.qtsettings.utils.PermissionUtils

class MonochromeTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()

        if (!PermissionUtils.hasWriteSecureSettingsPermission(this)) {
            Toast.makeText(this, R.string.toast_permission_not_granted_adb, Toast.LENGTH_LONG)
                .show()
            Log.w("MonochromeTile", "WRITE_SECURE_SETTINGS permission not granted.")
            return
        }

        val isEnabled = isMonochromeEnabled()
        try {
            if (isEnabled) {
                Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer_enabled", 0)
            } else {
                Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer", 0)
                Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer_enabled", 1)
            }
            updateTile()
        } catch (e: Exception) {
            Log.e("MonochromeTile", "Error toggling Monochrome / Grayscale mode", e)
            Toast.makeText(this, R.string.toast_error_saving_settings, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isMonochromeEnabled(): Boolean {
        val enabled = Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer_enabled", 0) == 1
        val type = Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer", -1)
        return enabled && type == 0
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val isEnabled = isMonochromeEnabled()
        tile.state = if (isEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = getString(R.string.tile_label_monochrome)
        tile.icon = Icon.createWithResource(this, R.drawable.ic_monochrome)
        tile.updateTile()
    }
}
