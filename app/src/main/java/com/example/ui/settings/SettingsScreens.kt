package com.example.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.viewmodel.WhatsAppUiState
import com.example.viewmodel.WhatsAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: WhatsAppViewModel,
    uiState: WhatsAppUiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val profile = uiState.userProfile
    var showQrDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    var showChatBackupDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(profile.displayName) }
    var backupStatusText by remember { mutableStateOf("Back up to Google Drive (${profile.googleEmail ?: "Google Account"})") }
    var isBackingUp by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Card Header (Clickable to edit name/status)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEditProfileDialog = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("settings_profile_card"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(WhatsAppGreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.avatarInitials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.displayName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = profile.aboutStatus,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Text(
                        text = if (!profile.googleEmail.isNullOrBlank()) "${profile.phoneNumber} • ${profile.googleEmail}" else profile.phoneNumber,
                        fontSize = 12.sp,
                        color = WhatsAppGreenPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "Firebase: ${uiState.firebaseUid.take(12)}... • Firestore Synced 🟢",
                        fontSize = 11.sp,
                        color = WhatsAppGreenPrimary,
                        maxLines = 1
                    )
                }

                IconButton(onClick = { showQrDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "My QR Code",
                        tint = WhatsAppGreenPrimary
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Settings Options List
            SettingsItemRow(
                icon = Icons.Outlined.Key,
                title = "Account",
                subtitle = "Security notifications, change number, linked Gmail",
                onClick = { showAccountDialog = true }
            )

            SettingsItemRow(
                icon = Icons.Outlined.Lock,
                title = "Privacy",
                subtitle = "Block contacts, disappearing messages, end-to-end security",
                onClick = { showPrivacyDialog = true }
            )

            SettingsItemRow(
                icon = Icons.Outlined.Chat,
                title = "Chats",
                subtitle = "Theme, wallpapers, chat history, Google Drive backup",
                onClick = { showChatBackupDialog = true }
            )

            SettingsItemRow(
                icon = Icons.Outlined.Notifications,
                title = "Notifications",
                subtitle = "Message, group & call tones",
                onClick = {}
            )

            SettingsItemRow(
                icon = Icons.Outlined.DataUsage,
                title = "Storage and data",
                subtitle = "Network usage, auto-download",
                onClick = {}
            )

            SettingsItemRow(
                icon = Icons.Outlined.Language,
                title = "App language",
                subtitle = "English (device's language)",
                onClick = {}
            )

            SettingsItemRow(
                icon = Icons.Outlined.HelpOutline,
                title = "Help",
                subtitle = "Help center, contact us, privacy policy",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            // Switch Account / Log Out Button
            Button(
                onClick = { viewModel.logOut() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("switch_account_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out / Sign Out of Firebase", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Account Options Dialog
    if (showAccountDialog) {
        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            title = { Text("Account Settings", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Phone: ${profile.phoneNumber}", fontWeight = FontWeight.SemiBold, color = WhatsAppGreenPrimary)
                    Text("Google Account: ${profile.googleEmail ?: "Verified Phone Login"}", fontSize = 14.sp)
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAccountDialog = false; viewModel.startLoginFlow() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = WhatsAppGreenPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Change phone number", fontSize = 15.sp)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAccountDialog = false }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = WhatsAppGreenPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Security notifications & Passkeys", fontSize = 15.sp)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAccountDialog = false }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = WhatsAppGreenPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Two-step verification", fontSize = 15.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAccountDialog = false }) {
                    Text("CLOSE", color = WhatsAppGreenPrimary)
                }
            }
        )
    }

    // Chat Backup Dialog
    if (showChatBackupDialog) {
        AlertDialog(
            onDismissRequest = { showChatBackupDialog = false },
            title = { Text("Google Drive Chat Backup", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Back up your messages and media to Google Drive so that if you lose your phone or switch to an Android phone, your chats are safe.")
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Google Account: ${profile.googleEmail ?: "Connected Account"}", fontWeight = FontWeight.Bold, color = WhatsAppGreenPrimary)
                    Text("Last Backup: Today at 2:00 AM (24.8 MB)")
                    Text("End-to-end encrypted backup: ON 🔒", color = WhatsAppGreenPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isBackingUp = true
                            backupStatusText = "Backup completed to ${profile.googleEmail ?: "Firebase Cloud"}! (100%)"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBackingUp) "Backing up..." else "BACK UP NOW")
                    }
                    if (isBackingUp) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(backupStatusText, fontSize = 12.sp, color = WhatsAppGreenPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showChatBackupDialog = false }) {
                    Text("DONE", color = WhatsAppGreenPrimary)
                }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Controls", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Last seen & online: My contacts", fontWeight = FontWeight.Medium)
                    Text("Profile photo: Everyone", fontWeight = FontWeight.Medium)
                    Text("About: Everyone", fontWeight = FontWeight.Medium)
                    Text("Read receipts: ON (Blue double checks)", fontWeight = FontWeight.Medium, color = WhatsAppGreenPrimary)
                    Text("Default message timer: Off", fontWeight = FontWeight.Medium)
                    Text("End-to-End Encryption: ALWAYS ON 🔒", fontWeight = FontWeight.Bold, color = WhatsAppGreenPrimary)
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("OK", color = WhatsAppGreenPrimary)
                }
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Phone: ${profile.phoneNumber}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Account: ${profile.googleEmail ?: "Verified Account"}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDisplayNameChanged(editedName)
                        showEditProfileDialog = false
                    }
                ) {
                    Text("SAVE", color = WhatsAppGreenPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    // QR Code Dialog
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = { Text(profile.displayName, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("WhatsApp contact QR code", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier.size(180.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "QR Code",
                                tint = Color.Black,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(profile.phoneNumber, fontWeight = FontWeight.Bold, color = WhatsAppGreenPrimary)
                    Text("End-to-End Encrypted Profile", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(onClick = { showQrDialog = false }) {
                    Text("CLOSE", color = WhatsAppGreenPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun SettingsItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag("settings_item_$title"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            subtitle?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = it,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
