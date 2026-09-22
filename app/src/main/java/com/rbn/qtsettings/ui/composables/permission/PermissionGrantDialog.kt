package com.rbn.qtsettings.ui.composables.permission

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rbn.qtsettings.R
import com.rbn.qtsettings.ui.theme.QuickTileSettingsTheme

@Composable
fun PermissionGrantDialog(
    onDismissRequest: () -> Unit,
    onOpenDeveloperOptions: () -> Unit,
    onCopyToClipboard: (String) -> Unit,
    onGrantWithRoot: () -> Unit,
    isDeviceRooted: Boolean
) {
    var showAdbInstructionsDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = { Text(text = stringResource(R.string.permission_grant_dialog_title)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = stringResource(R.string.permission_grant_dialog_intro),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Method 1: ADB
                PermissionMethodCard(
                    title = stringResource(
                        R.string.permission_method_adb_title,
                        1,
                        if (!isDeviceRooted) stringResource(R.string.recommended_for_you) else ""
                    ),
                    description = stringResource(R.string.permission_method_adb_desc)
                ) {
                    ElevatedButton(
                        onClick = { showAdbInstructionsDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.button_show_adb_instructions))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Method 2: Root
                PermissionMethodCard(
                    title = stringResource(
                        R.string.permission_method_root_title,
                        2,
                        if (isDeviceRooted) stringResource(R.string.recommended_for_you) else ""
                    ),
                    description = if (!isDeviceRooted) {
                        stringResource(R.string.device_not_rooted_detailed)
                    } else {
                        stringResource(R.string.root_ready_to_grant_desc)
                    }
                ) {
                    if (isDeviceRooted) {
                        ElevatedButton(
                            onClick = onGrantWithRoot,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.button_grant_with_root))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onOpenDeveloperOptions,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_developer_mode),
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.button_open_developer_options))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dialog_close))
            }
        }
    )

    if (showAdbInstructionsDialog) {
        AdbInstructionDialog(
            onDismissRequest = { showAdbInstructionsDialog = false },
            onCopyToClipboard = onCopyToClipboard
        )
    }
}

@Composable
fun PermissionMethodCard(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 600)
@Composable
fun PermissionGrantDialogPreview_Root() {
    QuickTileSettingsTheme {
        PermissionGrantDialog(
            onDismissRequest = {},
            onOpenDeveloperOptions = {},
            onCopyToClipboard = {},
            onGrantWithRoot = {},
            isDeviceRooted = true
        )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 600)
@Composable
fun PermissionGrantDialogPreview_Adb() {
    QuickTileSettingsTheme {
        PermissionGrantDialog(
            onDismissRequest = {},
            onOpenDeveloperOptions = {},
            onCopyToClipboard = {},
            onGrantWithRoot = {},
            isDeviceRooted = false
        )
    }
}
