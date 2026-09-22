# R8 & Proguard Optimization Rules for QuickTile Settings

# Keep Quick Settings Tile Services
-keep public class * extends android.service.quicksettings.TileService

# Keep Broadcast Receivers
-keep public class * extends android.content.BroadcastReceiver

# Keep Gson Serialized Data Models for Backup/Restore
-keepclassmembers class com.rbn.qtsettings.data.** {
    @com.google.gson.annotations.SerializedName <fields>;
    <fields>;
}

# Optimize Compose
-dontwarn androidx.compose.material.icons.**
