package com.example.ui.group

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.MockDataProvider
import com.example.model.ChatConversation
import com.example.ui.theme.WhatsAppFloatingActionButton
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.WhatsAppUiState
import com.example.viewmodel.WhatsAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatScreen(
    viewModel: WhatsAppViewModel,
    uiState: WhatsAppUiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val contacts = MockDataProvider.getAllContacts()

    var showAddContactDialog by remember { mutableStateOf(false) }
    var newContactName by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }
    var contactError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Select contact", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("${contacts.size} contacts available", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("new_chat_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // New Group Action Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AppScreen.NEW_GROUP) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("action_new_group"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("New group", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            // New Contact Action Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAddContactDialog = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("action_new_contact"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("New contact", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text("Add any real person to chat", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // New Broadcast Action Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AppScreen.NEW_BROADCAST) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("action_new_broadcast"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("New broadcast", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            // Contacts on WhatsApp header
            item {
                Text(
                    text = "Contacts on WhatsApp",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            // Contacts list
            items(contacts) { (name, phone) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Find existing chat or create direct chat
                            val existing = uiState.chats.find { it.name == name }
                            if (existing != null) {
                                viewModel.openChat(existing.id)
                            } else {
                                val newId = "direct_${name.hashCode()}"
                                val newChat = ChatConversation(
                                    id = newId,
                                    name = name,
                                    phoneNumber = phone,
                                    avatarColorHex = 0xFF008069,
                                    avatarInitials = name.take(2).uppercase(),
                                    lastMessageText = "Tap here to chat",
                                    lastMessageTime = "Just now"
                                )
                                viewModel.openChat(newChat.id)
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(name.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text(phone, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (showAddContactDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddContactDialog = false
                contactError = null
            },
            title = { Text("New Contact", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Enter the contact's name and mobile number to start a secure end-to-end encrypted chat.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newContactName,
                        onValueChange = {
                            newContactName = it
                            contactError = null
                        },
                        label = { Text("Name") },
                        placeholder = { Text("e.g. Vikram") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_contact_name_input")
                    )
                    OutlinedTextField(
                        value = newContactPhone,
                        onValueChange = {
                            newContactPhone = it
                            contactError = null
                        },
                        label = { Text("Phone Number") },
                        placeholder = { Text("+91 98123 45678") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_contact_phone_input")
                    )
                    contactError?.let { err ->
                        Text(err, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val nameTrim = newContactName.trim()
                        val phoneTrim = newContactPhone.trim()
                        if (nameTrim.isBlank()) {
                            contactError = "Please enter contact name."
                            return@Button
                        }
                        if (phoneTrim.isBlank()) {
                            contactError = "Please enter contact phone number."
                            return@Button
                        }
                        showAddContactDialog = false
                        newContactName = ""
                        newContactPhone = ""
                        contactError = null
                        viewModel.createDirectChat(nameTrim, phoneTrim)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary)
                ) {
                    Text("SAVE & CHAT", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddContactDialog = false
                        contactError = null
                    }
                ) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGroupScreen(
    viewModel: WhatsAppViewModel,
    uiState: WhatsAppUiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val contacts = MockDataProvider.getAllContacts()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("New group", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("${uiState.selectedContactsForGroup.size} selected", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (uiState.selectedContactsForGroup.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { viewModel.createGroup() },
                    containerColor = WhatsAppFloatingActionButton,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_confirm_create_group")
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Create Group")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Group Subject Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Group Icon", tint = WhatsAppGreenPrimary)
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedTextField(
                    value = uiState.newGroupName,
                    onValueChange = { viewModel.onGroupNameChanged(it) },
                    placeholder = { Text("Type group subject here...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("group_name_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Selected Participant Chips
            if (uiState.selectedContactsForGroup.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.selectedContactsForGroup.toList()) { contact ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(contact, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { viewModel.toggleContactSelection(contact) }
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

            // Contact List with Checkboxes
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(contacts) { (name, phone) ->
                    val isSelected = uiState.selectedContactsForGroup.contains(name)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleContactSelection(name) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .testTag("group_contact_$name"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(WhatsAppGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(name.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text(phone, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { viewModel.toggleContactSelection(name) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBroadcastScreen(
    viewModel: WhatsAppViewModel,
    uiState: WhatsAppUiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val contacts = MockDataProvider.getAllContacts()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("New broadcast", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("${uiState.selectedContactsForGroup.size} of ${contacts.size} selected", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (uiState.selectedContactsForGroup.size >= 2) {
                FloatingActionButton(
                    onClick = { viewModel.createBroadcastList() },
                    containerColor = WhatsAppFloatingActionButton,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_confirm_create_broadcast")
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Create Broadcast")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Info text banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Only contacts with ${uiState.userProfile.phoneNumber} in their address book will receive your broadcast messages.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 16.sp
                )
            }

            // Broadcast Name Input
            OutlinedTextField(
                value = uiState.newBroadcastName,
                onValueChange = { viewModel.onBroadcastNameChanged(it) },
                placeholder = { Text("Broadcast list name (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("broadcast_name_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Contact List with Checkboxes
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(contacts) { (name, phone) ->
                    val isSelected = uiState.selectedContactsForGroup.contains(name)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleContactSelection(name) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .testTag("broadcast_contact_$name"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF9800)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(name.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text(phone, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { viewModel.toggleContactSelection(name) }
                        )
                    }
                }
            }
        }
    }
}
