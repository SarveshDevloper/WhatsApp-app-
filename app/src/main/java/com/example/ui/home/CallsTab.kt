package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallDirection
import com.example.model.CallRecord
import com.example.model.CallType
import com.example.ui.theme.WhatsAppFloatingActionButton
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.viewmodel.WhatsAppUiState
import com.example.viewmodel.WhatsAppViewModel

@Composable
fun CallsTab(
    viewModel: WhatsAppViewModel,
    uiState: WhatsAppUiState,
    onStartCall: (String, String, Long, String, CallType) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCallLinkDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Create Call Link Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCallLinkDialog = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("create_call_link_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Call Link",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Create call link",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Share a link for your WhatsApp call",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Recent Calls Header
            item {
                Text(
                    text = "Recent",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            // Recent Calls List
            items(uiState.callRecords, key = { it.id }) { call ->
                CallItemRow(
                    call = call,
                    onCallClick = {
                        onStartCall(
                            call.contactName,
                            call.contactNumber,
                            call.avatarColorHex,
                            call.avatarInitials,
                            call.type
                        )
                    }
                )
            }

            // End-to-end lock footer
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Your personal calls are end-to-end encrypted",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Floating Action Button to start call
        FloatingActionButton(
            onClick = {
                // Call first contact in list as quick start
                val first = uiState.chats.firstOrNull()
                if (first != null) {
                    onStartCall(first.name, first.phoneNumber, first.avatarColorHex, first.avatarInitials, CallType.VOICE)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_new_call"),
            containerColor = WhatsAppFloatingActionButton,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "New Call",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (showCallLinkDialog) {
        val link = "https://call.whatsapp.com/video/e2ee-948204"
        AlertDialog(
            onDismissRequest = { showCallLinkDialog = false },
            title = { Text("WhatsApp Call Link") },
            text = {
                Column {
                    Text("Anyone with WhatsApp can use this link to join this call. Only share it with people you trust.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = link,
                            modifier = Modifier.padding(12.dp),
                            color = WhatsAppGreenPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCallLinkDialog = false }) {
                    Text("COPY LINK", color = WhatsAppGreenPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun CallItemRow(
    call: CallRecord,
    onCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCallClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("call_item_${call.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(call.avatarColorHex)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = call.avatarInitials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Name, Call Direction, Timestamp
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = call.contactName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = if (call.direction == CallDirection.MISSED) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val (icon, tint) = when (call.direction) {
                    CallDirection.INCOMING -> Icons.Default.CallReceived to WhatsAppGreenPrimary
                    CallDirection.OUTGOING -> Icons.Default.CallMade to WhatsAppGreenPrimary
                    CallDirection.MISSED -> Icons.Default.CallMissed to Color(0xFFE53935)
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${call.formattedTime} (${call.durationText})",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Call Type Action Icon
        IconButton(onClick = onCallClick) {
            Icon(
                imageVector = if (call.type == CallType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
                contentDescription = if (call.type == CallType.VIDEO) "Video Call" else "Voice Call",
                tint = WhatsAppGreenPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
