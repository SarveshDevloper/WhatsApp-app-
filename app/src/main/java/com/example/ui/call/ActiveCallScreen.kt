package com.example.ui.call

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallType
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.viewmodel.WhatsAppUiState
import com.example.viewmodel.WhatsAppViewModel

@Composable
fun ActiveCallScreen(
    viewModel: WhatsAppViewModel,
    uiState: WhatsAppUiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.endCall()
    }

    val call = uiState.activeCall ?: run {
        viewModel.navigateBack()
        return
    }

    val isVideo = call.type == CallType.VIDEO

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isVideo) Color(0xFF1E2830) else Color(0xFF111B21))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Encryption indicator & Call Type
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = WhatsAppGreenPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "End-to-end encrypted",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = call.contactName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                val statusText = if (!call.isConnected) "Ringing..." else {
                    val minutes = call.durationSeconds / 60
                    val seconds = call.durationSeconds % 60
                    "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
                }
                Text(
                    text = statusText,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Center: Avatar or Simulated Video Stream
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color(call.avatarColorHex)),
                contentAlignment = Alignment.Center
            ) {
                if (isVideo) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Live Video",
                        tint = Color.White,
                        modifier = Modifier.size(72.dp)
                    )
                } else {
                    Text(
                        text = call.avatarInitials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 54.sp
                    )
                }
            }

            // Bottom Call Action Controls
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.45f),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Speakerphone Toggle
                    IconButton(
                        onClick = { viewModel.toggleSpeakerCall() },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (call.isSpeakerOn) Color.White else Color.White.copy(alpha = 0.2f))
                            .testTag("call_speaker_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speaker",
                            tint = if (call.isSpeakerOn) Color.Black else Color.White
                        )
                    }

                    // Video Camera Flip Toggle
                    if (isVideo) {
                        IconButton(
                            onClick = { viewModel.toggleCameraFlip() },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (call.isCameraFlipped) Color.White else Color.White.copy(alpha = 0.2f))
                                .testTag("call_flip_camera_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlipCameraAndroid,
                                contentDescription = "Flip Camera",
                                tint = if (call.isCameraFlipped) Color.Black else Color.White
                            )
                        }
                    }

                    // Mute Mic Toggle
                    IconButton(
                        onClick = { viewModel.toggleMuteCall() },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (call.isMuted) Color.White else Color.White.copy(alpha = 0.2f))
                            .testTag("call_mute_button")
                    ) {
                        Icon(
                            imageVector = if (call.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (call.isMuted) Color.Black else Color.White
                        )
                    }

                    // End Call (Red Button)
                    IconButton(
                        onClick = { viewModel.endCall() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53935))
                            .testTag("call_end_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
