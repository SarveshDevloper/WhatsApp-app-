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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatConversation
import com.example.ui.theme.WhatsAppBlueTick
import com.example.ui.theme.WhatsAppFloatingActionButton
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.viewmodel.ChatFilter
import com.example.viewmodel.WhatsAppUiState
import com.example.viewmodel.WhatsAppViewModel

@Composable
fun ChatsTab(
    viewModel: WhatsAppViewModel,
    uiState: WhatsAppUiState,
    onChatClick: (String) -> Unit,
    onNewChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChatFilterChip(
                    label = "All",
                    selected = uiState.chatFilter == ChatFilter.ALL,
                    onClick = { viewModel.setChatFilter(ChatFilter.ALL) }
                )
                ChatFilterChip(
                    label = "Unread",
                    selected = uiState.chatFilter == ChatFilter.UNREAD,
                    onClick = { viewModel.setChatFilter(ChatFilter.UNREAD) }
                )
                ChatFilterChip(
                    label = "Favorites",
                    selected = uiState.chatFilter == ChatFilter.FAVORITES,
                    onClick = { viewModel.setChatFilter(ChatFilter.FAVORITES) }
                )
                ChatFilterChip(
                    label = "Groups",
                    selected = uiState.chatFilter == ChatFilter.GROUPS,
                    onClick = { viewModel.setChatFilter(ChatFilter.GROUPS) }
                )
            }

            // Filtered Chat List
            val filteredChats = uiState.chats.filter { chat ->
                val matchesFilter = when (uiState.chatFilter) {
                    ChatFilter.ALL -> true
                    ChatFilter.UNREAD -> chat.unreadCount > 0
                    ChatFilter.FAVORITES -> chat.isFavorite
                    ChatFilter.GROUPS -> chat.isGroup
                }
                val matchesSearch = if (uiState.searchQuery.isBlank()) true else {
                    chat.name.contains(uiState.searchQuery, ignoreCase = true) ||
                            chat.lastMessageText.contains(uiState.searchQuery, ignoreCase = true)
                }
                matchesFilter && matchesSearch
            }

            if (filteredChats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(WhatsAppGreenPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = WhatsAppGreenPrimary,
                                modifier = Modifier.size(46.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) "No chats found" else "Welcome to WhatsApp, ${uiState.userProfile.displayName.ifBlank { "there" }}!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) "Try searching for a different name or message" else "Tap the green message button below to start your first chat, or add demo contacts to explore.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onNewChatClick,
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start a Chat", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { viewModel.loadDemoContactsForTesting() },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(18.dp), tint = WhatsAppGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Load Demo Contacts (Optional)", color = WhatsAppGreenPrimary)
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredChats, key = { it.id }) { chat ->
                        ChatItemRow(
                            chat = chat,
                            onClick = { onChatClick(chat.id) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 76.dp, end = 16.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            thickness = 0.5.dp
                        )
                    }

                    // Bottom padding for FAB and End-to-end lock footer
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Encrypted",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Your personal messages are end-to-end encrypted",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            }
        }

        // Floating Action Button for New Chat
        FloatingActionButton(
            onClick = onNewChatClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_new_chat"),
            containerColor = WhatsAppFloatingActionButton,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "New Chat",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ChatItemRow(
    chat: ChatConversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("chat_item_${chat.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with online badge or group/broadcast icon
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(chat.avatarColorHex)),
                contentAlignment = Alignment.Center
            ) {
                if (chat.isGroup) {
                    Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = Color.White)
                } else if (chat.isBroadcast) {
                    Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = Color.White)
                } else {
                    Text(
                        text = chat.avatarInitials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
            if (chat.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(WhatsAppGreenPrimary)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Name and Last Message
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = chat.lastMessageTime,
                    fontSize = 12.sp,
                    color = if (chat.unreadCount > 0) WhatsAppGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!chat.isGroup && !chat.isBroadcast && chat.unreadCount == 0) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Read",
                        tint = WhatsAppBlueTick,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = chat.lastMessageText,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (chat.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                if (chat.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chat.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        shape = RoundedCornerShape(16.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = WhatsAppGreenPrimary.copy(alpha = 0.15f),
            selectedLabelColor = WhatsAppGreenPrimary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = null
    )
}
