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

# Optimize Bytecode and DEX Compilation
-dontwarn androidx.compose.material.icons.**
-repackageclasses ''
-allowaccessmodification
-dontusemixedcaseclassnames

# Strip unused Kotlin Null Check Assertions
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkNotNull(...);
    public static void checkNotNullParameter(...);
    public static void checkExpressionValueIsNotNull(...);
}
