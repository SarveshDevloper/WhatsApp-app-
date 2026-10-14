package com.example.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AttachmentSheet(
    onSelectOption: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AttachmentItem(
                    icon = Icons.Default.InsertDriveFile,
                    label = "Document",
                    bgColor = Color(0xFF7F66FF),
                    onClick = { onSelectOption("Document") }
                )
                AttachmentItem(
                    icon = Icons.Default.PhotoCamera,
                    label = "Camera",
                    bgColor = Color(0xFFD3396D),
                    onClick = { onSelectOption("Camera") }
                )
                AttachmentItem(
                    icon = Icons.Default.Image,
                    label = "Gallery",
                    bgColor = Color(0xFFAC44CF),
                    onClick = { onSelectOption("Gallery") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AttachmentItem(
                    icon = Icons.Default.Headphones,
                    label = "Audio",
                    bgColor = Color(0xFFFF6600),
                    onClick = { onSelectOption("Audio") }
                )
                AttachmentItem(
                    icon = Icons.Default.LocationOn,
                    label = "Location",
                    bgColor = Color(0xFF0F9D58),
                    onClick = { onSelectOption("Location") }
                )
                AttachmentItem(
                    icon = Icons.Default.Person,
                    label = "Contact",
                    bgColor = Color(0xFF007BFF),
                    onClick = { onSelectOption("Contact") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                AttachmentItem(
                    icon = Icons.Default.Poll,
                    label = "Poll",
                    bgColor = Color(0xFF00A884),
                    onClick = { onSelectOption("Poll") }
                )
            }
        }
    }
}

@Composable
private fun AttachmentItem(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("attachment_$label")
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}
