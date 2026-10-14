package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallType
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.WhatsAppUiState
import com.example.viewmodel.WhatsAppViewModel

enum class HomeTab {
    CHATS,
    UPDATES,
    COMMUNITIES,
    CALLS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppHomeScreen(
    viewModel: WhatsAppViewModel,
    uiState: WhatsAppUiState,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(HomeTab.CHATS) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (uiState.isSearchActive) {
                TopAppBar(
                    title = {
                        TextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = { Text("Search...") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("top_search_input")
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.toggleSearch(false) }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Close Search")
                        }
                    },
                    actions = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = "WhatsApp",
                            color = WhatsAppGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.STATUS_CREATOR) },
                            modifier = Modifier.testTag("top_camera_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = "Camera",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleSearch(true) },
                            modifier = Modifier.testTag("top_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.testTag("top_more_options_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More Options",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("New group") },
                                    leadingIcon = { Icon(Icons.Default.GroupAdd, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.navigateTo(AppScreen.NEW_GROUP)
                                    },
                                    modifier = Modifier.testTag("menu_new_group")
                                )
                                DropdownMenuItem(
                                    text = { Text("New broadcast") },
                                    leadingIcon = { Icon(Icons.Default.Campaign, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.navigateTo(AppScreen.NEW_BROADCAST)
                                    },
                                    modifier = Modifier.testTag("menu_new_broadcast")
                                )
                                DropdownMenuItem(
                                    text = { Text("Linked devices") },
                                    leadingIcon = { Icon(Icons.Default.Devices, contentDescription = null) },
                                    onClick = { showMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Starred messages") },
                                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                                    onClick = { showMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Payments") },
                                    leadingIcon = { Icon(Icons.Default.Payment, contentDescription = null) },
                                    onClick = { showMenu = false }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.navigateTo(AppScreen.SETTINGS)
                                    },
                                    modifier = Modifier.testTag("menu_settings")
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val totalUnread = uiState.chats.sumOf { it.unreadCount }
                val hasUnseenStatus = uiState.contactStatuses.any { it.hasUnseen }

                NavigationBarItem(
                    selected = selectedTab == HomeTab.CHATS,
                    onClick = { selectedTab = HomeTab.CHATS },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (totalUnread > 0) {
                                    Badge(
                                        containerColor = WhatsAppGreenPrimary,
                                        contentColor = Color.White
                                    ) {
                                        Text("$totalUnread")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selectedTab == HomeTab.CHATS) Icons.Default.Chat else Icons.Outlined.Chat,
                                contentDescription = "Chats"
                            )
                        }
                    },
                    label = { Text("Chats", fontWeight = if (selectedTab == HomeTab.CHATS) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("bottom_nav_chats")
                )

                NavigationBarItem(
                    selected = selectedTab == HomeTab.UPDATES,
                    onClick = { selectedTab = HomeTab.UPDATES },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (hasUnseenStatus) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(WhatsAppGreenPrimary)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selectedTab == HomeTab.UPDATES) Icons.Default.MotionPhotosOn else Icons.Outlined.MotionPhotosOn,
                                contentDescription = "Updates"
                            )
                        }
                    },
                    label = { Text("Updates", fontWeight = if (selectedTab == HomeTab.UPDATES) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("bottom_nav_updates")
                )

                NavigationBarItem(
                    selected = selectedTab == HomeTab.COMMUNITIES,
                    onClick = { selectedTab = HomeTab.COMMUNITIES },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == HomeTab.COMMUNITIES) Icons.Default.Groups else Icons.Outlined.Groups,
                            contentDescription = "Communities"
                        )
                    },
                    label = { Text("Communities", fontWeight = if (selectedTab == HomeTab.COMMUNITIES) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("bottom_nav_communities")
                )

                NavigationBarItem(
                    selected = selectedTab == HomeTab.CALLS,
                    onClick = { selectedTab = HomeTab.CALLS },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == HomeTab.CALLS) Icons.Default.Call else Icons.Outlined.Call,
                            contentDescription = "Calls"
                        )
                    },
                    label = { Text("Calls", fontWeight = if (selectedTab == HomeTab.CALLS) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("bottom_nav_calls")
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                HomeTab.CHATS -> ChatsTab(
                    viewModel = viewModel,
                    uiState = uiState,
                    onChatClick = { chatId -> viewModel.openChat(chatId) },
                    onNewChatClick = { viewModel.navigateTo(AppScreen.NEW_CHAT) }
                )
                HomeTab.UPDATES -> UpdatesTab(
                    viewModel = viewModel,
                    uiState = uiState,
                    onStatusClick = { story -> viewModel.openStatusViewer(story) },
                    onCreateTextStatusClick = { viewModel.navigateTo(AppScreen.STATUS_CREATOR) }
                )
                HomeTab.COMMUNITIES -> CommunitiesTab(
                    onNewCommunityClick = { viewModel.navigateTo(AppScreen.NEW_GROUP) }
                )
                HomeTab.CALLS -> CallsTab(
                    viewModel = viewModel,
                    uiState = uiState,
                    onStartCall = { name, number, color, initials, type ->
                        viewModel.startCall(name, number, color, initials, type)
                    }
                )
            }
        }
    }
}
