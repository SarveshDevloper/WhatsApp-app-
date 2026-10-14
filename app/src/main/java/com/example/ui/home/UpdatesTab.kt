package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.model.StatusPrivacyOption
import com.example.model.UserStatusStory
import com.example.ui.theme.WhatsAppFloatingActionButton
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.ui.theme.WhatsAppStatusSeen
import com.example.ui.theme.WhatsAppStatusUnseen
import com.example.viewmodel.WhatsAppUiState
import com.example.viewmodel.WhatsAppViewModel

@Composable
fun UpdatesTab(
    viewModel: WhatsAppViewModel,
    uiState: WhatsAppUiState,
    onStatusClick: (UserStatusStory) -> Unit,
    onCreateTextStatusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Header with Status Privacy option
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = { showPrivacyDialog = true },
                        modifier = Modifier.testTag("status_privacy_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Status Privacy",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // My Status Row
            item {
                val myStory = uiState.myStatus
                val hasStatus = myStory != null && myStory.statuses.isNotEmpty()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (hasStatus) {
                                onStatusClick(myStory)
                            } else {
                                onCreateTextStatusClick()
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("my_status_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .then(
                                    if (hasStatus) Modifier.border(2.5.dp, WhatsAppStatusUnseen, CircleShape)
                                    else Modifier
                                )
                                .padding(if (hasStatus) 3.dp else 0.dp)
                                .clip(CircleShape)
                                .background(WhatsAppGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ME",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        // Plus Icon for adding status
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(WhatsAppGreenPrimary)
                                .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add status",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "My status",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (hasStatus) "Tap to view • ${myStory.statuses.size} update (expires in 24h)"
                            else "Tap to add status update",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (hasStatus) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "Views",
                                    tint = WhatsAppGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${myStory.statuses.sumOf { it.viewsCount }}",
                                    fontSize = 12.sp,
                                    color = WhatsAppGreenPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Recent Updates Header
            val unseenStatuses = uiState.contactStatuses.filter { it.hasUnseen }
            if (unseenStatuses.isNotEmpty()) {
                item {
                    Text(
                        text = "Recent updates",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }
                items(unseenStatuses, key = { it.id }) { story ->
                    StatusItemRow(story = story, isUnseen = true, onClick = { onStatusClick(story) })
                }
            }

            // Viewed Updates Header
            val viewedStatuses = uiState.contactStatuses.filter { !it.hasUnseen }
            if (viewedStatuses.isNotEmpty()) {
                item {
                    Text(
                        text = "Viewed updates",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }
                items(viewedStatuses, key = { it.id }) { story ->
                    StatusItemRow(story = story, isUnseen = false, onClick = { onStatusClick(story) })
                }
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
                        text = "Your status updates are end-to-end encrypted",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Dual Floating Action Buttons: Camera and Pen
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Smaller Pen FAB for Text Status
            SmallFloatingActionButton(
                onClick = onCreateTextStatusClick,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_text_status")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Text Status",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Primary Camera FAB
            FloatingActionButton(
                onClick = onCreateTextStatusClick,
                containerColor = WhatsAppFloatingActionButton,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_camera_status")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera Status",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    // Status Privacy Dialog
    if (showPrivacyDialog) {
        val currentPrivacy = uiState.statusPrivacy.option
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    text = "Status privacy",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Who can see my status updates",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    StatusPrivacyOption.values().forEach { option ->
                        val label = when (option) {
                            StatusPrivacyOption.MY_CONTACTS -> "My contacts"
                            StatusPrivacyOption.MY_CONTACTS_EXCEPT -> "My contacts except..."
                            StatusPrivacyOption.ONLY_SHARE_WITH -> "Only share with..."
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateStatusPrivacy(option)
                                    showPrivacyDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentPrivacy == option,
                                onClick = {
                                    viewModel.updateStatusPrivacy(option)
                                    showPrivacyDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(label, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Changes to your privacy settings won't affect status updates that you've sent already.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("DONE", color = WhatsAppGreenPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun StatusItemRow(
    story: UserStatusStory,
    isUnseen: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("status_item_${story.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status Ring: Green for unseen, Grey for seen
        Box(
            modifier = Modifier
                .size(54.dp)
                .border(
                    width = 2.5.dp,
                    color = if (isUnseen) WhatsAppStatusUnseen else WhatsAppStatusSeen,
                    shape = CircleShape
                )
                .padding(3.dp)
                .clip(CircleShape)
                .background(Color(story.avatarColorHex)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = story.avatarInitials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = story.userName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            val latestTime = story.statuses.lastOrNull()?.formattedTime ?: "Today"
            Text(
                text = latestTime,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
