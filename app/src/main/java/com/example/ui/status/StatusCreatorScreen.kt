package com.example.ui.status

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.viewmodel.WhatsAppViewModel

@Composable
fun StatusCreatorScreen(
    viewModel: WhatsAppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val backgroundColors = listOf(
        0xFF008069, // WhatsApp Green
        0xFF5856D6, // Indigo
        0xFFFF2D55, // Pinkish Red
        0xFF007AFF, // Blue
        0xFFFF9500, // Orange
        0xFF795548, // Brown
        0xFF455A64  // Slate
    )

    val fontFamilies = listOf(
        FontFamily.Default to "Default",
        FontFamily.Serif to "Serif",
        FontFamily.Monospace to "Monospace",
        FontFamily.Cursive to "Cursive"
    )

    var colorIndex by remember { mutableStateOf(0) }
    var fontIndex by remember { mutableStateOf(0) }
    var statusText by remember { mutableStateOf("") }

    val currentBgColor = backgroundColors[colorIndex]
    val (currentFont, currentFontLabel) = fontFamilies[fontIndex]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(currentBgColor))
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.testTag("status_creator_close_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Font Selector Button
                IconButton(
                    onClick = { fontIndex = (fontIndex + 1) % fontFamilies.size },
                    modifier = Modifier.testTag("status_font_button")
                ) {
                    Icon(Icons.Default.TextFields, contentDescription = "Font: $currentFontLabel", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Color Palette Changer Button
                IconButton(
                    onClick = { colorIndex = (colorIndex + 1) % backgroundColors.size },
                    modifier = Modifier.testTag("status_color_button")
                ) {
                    Icon(Icons.Default.Palette, contentDescription = "Change Color", tint = Color.White)
                }
            }
        }

        // Center Text Input
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = statusText,
                onValueChange = { statusText = it },
                placeholder = {
                    Text(
                        text = "Type a status",
                        fontSize = 32.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        fontFamily = currentFont,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                textStyle = TextStyle(
                    fontSize = 32.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontFamily = currentFont,
                    fontWeight = FontWeight.SemiBold
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("status_text_input")
            )
        }

        // Bottom Bar with Privacy hint and Send Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.25f))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Status (My contacts) • Disappears in 24h",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            FloatingActionButton(
                onClick = {
                    if (statusText.isNotBlank()) {
                        viewModel.addStatusUpdate(statusText, currentBgColor)
                    }
                },
                containerColor = WhatsAppGreenPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(50.dp)
                    .testTag("status_post_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Post Status")
            }
        }
    }
}
