package com.example.ui.status

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StatusContentType
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.viewmodel.WhatsAppUiState
import com.example.viewmodel.WhatsAppViewModel

@Composable
fun StatusViewerScreen(
    viewModel: WhatsAppViewModel,
    uiState: WhatsAppUiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val story = uiState.selectedStory ?: run {
        viewModel.navigateBack()
        return
    }

    val statuses = story.statuses
    var currentIndex by remember { mutableStateOf(0) }
    val progress = remember { Animatable(0f) }
    var isPaused by remember { mutableStateOf(false) }
    var showReplySentSnackbar by remember { mutableStateOf(false) }

    val currentStatus = statuses.getOrNull(currentIndex) ?: return

    // Auto-advance timer (5 seconds per status item)
    LaunchedEffect(currentIndex, isPaused) {
        if (!isPaused) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
            )
            if (currentIndex < statuses.size - 1) {
                currentIndex++
            } else {
                viewModel.navigateBack()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(currentStatus.backgroundColorHex))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    },
                    onTap = { offset ->
                        if (offset.x < size.width * 0.35f) {
                            if (currentIndex > 0) currentIndex--
                        } else {
                            if (currentIndex < statuses.size - 1) currentIndex++ else viewModel.navigateBack()
                        }
                    }
                )
            }
    ) {
        // Main Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (currentStatus.type == StatusContentType.PHOTO) {
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(90.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Text(
                    text = currentStatus.textContent,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )

                currentStatus.caption?.let { caption ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = caption,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Top Overlay Header: Segmented Progress Bars & Contact Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(top = 16.dp, bottom = 12.dp)
        ) {
            // Segmented Progress Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                statuses.indices.forEach { index ->
                    val segmentProgress = when {
                        index < currentIndex -> 1f
                        index == currentIndex -> progress.value
                        else -> 0f
                    }
                    LinearProgressIndicator(
                        progress = { segmentProgress },
                        modifier = Modifier
                            .weight(1f)
                            .height(2.5.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Contact Info Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("status_viewer_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(story.avatarColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(story.avatarInitials, color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = story.userName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${currentStatus.formattedTime} • Disappears in 24h",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }

                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                }
            }
        }

        // Bottom Controls / Reply Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            if (story.isMyStatus) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${currentStatus.viewsCount} views",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showReplySentSnackbar = true
                            },
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reply...",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    listOf("❤️", "😂", "🔥").forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .clickable { showReplySentSnackbar = true }
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        if (showReplySentSnackbar) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                containerColor = WhatsAppGreenPrimary,
                contentColor = Color.White
            ) {
                Text("Reply sent to ${story.userName} (End-to-End Encrypted 🔒)")
            }
        }
    }
}
