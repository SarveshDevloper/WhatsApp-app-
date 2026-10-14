package com.example.ui.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallType
import com.example.model.Message
import com.example.model.MessageStatus
import com.example.model.MessageType
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.WhatsAppUiState
import com.example.viewmodel.WhatsAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    viewModel: WhatsAppViewModel,
    uiState: WhatsAppUiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val chatId = uiState.selectedChatId ?: return
    val chat = uiState.chats.find { it.id == chatId } ?: return
    val messages = uiState.messagesMap[chatId] ?: emptyList()

    var inputText by remember { mutableStateOf("") }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var selectedMessageForReaction by remember { mutableStateOf<Message?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    val isDark = isSystemInDarkTheme()
    val chatBg = if (isDark) WhatsAppChatBackgroundDark else WhatsAppChatBackgroundLight

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo(AppScreen.SECURITY_VERIFY) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(chat.avatarColorHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (chat.isGroup) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            } else if (chat.isBroadcast) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            } else {
                                Text(chat.avatarInitials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = chat.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val subtitle = if (uiState.isContactTyping) "typing..." else chat.statusSubtitle
                            Text(
                                text = subtitle,
                                fontSize = 12.sp,
                                color = if (uiState.isContactTyping) WhatsAppGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("chat_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.startCall(chat.name, chat.phoneNumber, chat.avatarColorHex, chat.avatarInitials, CallType.VIDEO)
                        },
                        modifier = Modifier.testTag("chat_video_call_button")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call")
                    }

                    IconButton(
                        onClick = {
                            viewModel.startCall(chat.name, chat.phoneNumber, chat.avatarColorHex, chat.avatarInitials, CallType.VOICE)
                        },
                        modifier = Modifier.testTag("chat_voice_call_button")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Voice Call")
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("View contact") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Verify security code (E2EE)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = WhatsAppGreenPrimary) },
                                onClick = {
                                    showMenu = false
                                    viewModel.navigateTo(AppScreen.SECURITY_VERIFY)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Media, links, and docs") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Search") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Mute notifications") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Disappearing messages") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Wallpaper") },
                                onClick = { showMenu = false }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(chatBg)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    reverseLayout = false
                ) {
                    // Authentic WhatsApp End-to-End Encryption Banner
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) Color(0xFF182229) else Color(0xFFFFF9C4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .clickable { viewModel.navigateTo(AppScreen.SECURITY_VERIFY) }
                                .testTag("encryption_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Encrypted",
                                    tint = if (isDark) WhatsAppGreenLight else Color(0xFF856404),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🔒 Messages and calls are end-to-end encrypted. No one outside of this chat, not even WhatsApp, can read or listen to them. Tap to learn more.",
                                    fontSize = 12.sp,
                                    color = if (isDark) Color(0xFFE9EDEF) else Color(0xFF856404),
                                    lineHeight = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Date Pill
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isDark) Color(0xFF182229) else Color(0xFFFFFFFF),
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    text = "TODAY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Messages
                    items(messages, key = { it.id }) { msg ->
                        MessageBubble(
                            message = msg,
                            isGroup = chat.isGroup,
                            onLongClick = { selectedMessageForReaction = msg }
                        )
                    }
                }

                // Bottom Input Area
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Input Capsule
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(26.dp),
                            color = if (isDark) WhatsAppDarkCard else Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {}) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEmotions,
                                        contentDescription = "Emoji",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                TextField(
                                    value = inputText,
                                    onValueChange = { inputText = it },
                                    placeholder = { Text("Message", fontSize = 16.sp) },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("chat_text_input"),
                                    maxLines = 4
                                )

                                IconButton(
                                    onClick = { showAttachmentSheet = !showAttachmentSheet },
                                    modifier = Modifier.testTag("attachment_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AttachFile,
                                        contentDescription = "Attach",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (inputText.isBlank()) {
                                    IconButton(onClick = {}) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Camera",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Mic or Send FAB
                        FloatingActionButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendMessage(chatId, inputText)
                                    inputText = ""
                                } else {
                                    // Send voice note simulation
                                    viewModel.sendMessage(chatId, "🎤 Voice note (0:12)", MessageType.VOICE)
                                }
                            },
                            containerColor = WhatsAppGreenPrimary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("chat_send_button")
                        ) {
                            Icon(
                                imageVector = if (inputText.isNotBlank()) Icons.Default.Send else Icons.Default.Mic,
                                contentDescription = if (inputText.isNotBlank()) "Send" else "Voice Note",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Attachment Sheet Overlay
            if (showAttachmentSheet) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { showAttachmentSheet = false },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    AttachmentSheet(
                        onSelectOption = { option ->
                            showAttachmentSheet = false
                            val messageText = when (option) {
                                "Location" -> "📍 Live Location: Mumbai, India (Encrypted)"
                                "Contact" -> "👤 Contact: Vikram Malhotra (+91 94567 89012)"
                                "Poll" -> "📊 Poll: Team meetup this Friday? (Yes / No)"
                                "Document" -> "📄 Document: Project_Specs_v2.pdf"
                                else -> "📷 Shared $option"
                            }
                            viewModel.sendMessage(chatId, messageText)
                        },
                        onDismiss = { showAttachmentSheet = false },
                        modifier = Modifier.padding(bottom = 60.dp)
                    )
                }
            }

            // Reaction Dialog / Pill Overlay
            selectedMessageForReaction?.let { msg ->
                AlertDialog(
                    onDismissRequest = { selectedMessageForReaction = null },
                    title = { Text("React to Message", fontSize = 16.sp) },
                    text = {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                listOf("❤️", "👍", "😂", "😮", "😢", "🙏").forEach { emoji ->
                                    Text(
                                        text = emoji,
                                        fontSize = 28.sp,
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.addReactionToMessage(chatId, msg.id, emoji)
                                                selectedMessageForReaction = null
                                            }
                                            .padding(6.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.toggleStarMessage(chatId, msg.id)
                                        selectedMessageForReaction = null
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = WhatsAppGreenPrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(if (msg.isStarred) "Unstar" else "Star")
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.deleteMessage(chatId, msg.id)
                                        selectedMessageForReaction = null
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Delete for everyone", color = Color.Red)
                            }
                        }
                    },
                    confirmButton = {}
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isGroup: Boolean,
    onLongClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val isOutgoing = message.isOutgoing

    val bubbleBg = if (isOutgoing) {
        if (isDark) WhatsAppDarkGreenBubble else WhatsAppLightGreenBubble
    } else {
        if (isDark) WhatsAppIncomingBubbleDark else WhatsAppIncomingBubbleLight
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        contentAlignment = if (isOutgoing) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isOutgoing) 14.dp else 2.dp,
                bottomEnd = if (isOutgoing) 2.dp else 14.dp
            ),
            color = bubbleBg,
            shadowElevation = 1.dp,
            modifier = Modifier
                .widthIn(min = 80.dp, max = 300.dp)
                .clickable(onClick = onLongClick)
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                if (isGroup && !isOutgoing) {
                    Text(
                        text = message.senderName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhatsAppGreenPrimary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                if (message.type == MessageType.VOICE) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(WhatsAppGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // Waveform simulation
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            listOf(8, 16, 24, 12, 18, 28, 14, 20, 10, 22, 16, 8, 14).forEach { height ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(height.dp)
                                        .background(WhatsAppGreenPrimary.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("1x", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    Text(
                        text = message.text,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.isStarred) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Starred",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Text(
                        text = message.formattedTime,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    if (isOutgoing) {
                        Spacer(modifier = Modifier.width(4.dp))
                        val (icon, tint) = when (message.status) {
                            MessageStatus.SENDING -> Icons.Default.AccessTime to MaterialTheme.colorScheme.onSurfaceVariant
                            MessageStatus.SENT -> Icons.Default.Check to WhatsAppGreyTick
                            MessageStatus.DELIVERED -> Icons.Default.DoneAll to WhatsAppGreyTick
                            MessageStatus.READ -> Icons.Default.DoneAll to WhatsAppBlueTick
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = message.status.name,
                            tint = tint,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Reactions Badge
                if (message.reactions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        message.reactions.forEach { (emoji, count) ->
                            Text("$emoji $count", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
