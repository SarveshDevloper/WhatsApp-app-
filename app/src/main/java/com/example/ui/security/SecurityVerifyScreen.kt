package com.example.ui.security

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.viewmodel.WhatsAppUiState
import com.example.viewmodel.WhatsAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityVerifyScreen(
    viewModel: WhatsAppViewModel,
    uiState: WhatsAppUiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val chatId = uiState.selectedChatId
    val chat = uiState.chats.find { it.id == chatId }
    val contactName = chat?.name ?: "Contact"
    val safetyNumberBlocks = uiState.securitySafetyNumber.ifEmpty {
        listOf(
            "84920", "19384", "72910",
            "48291", "93820", "48201",
            "57291", "02948", "19402",
            "83910", "48291", "01928"
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Verify security code", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("security_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // QR Code Simulation Card
            Surface(
                modifier = Modifier
                    .size(200.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "Security QR Code",
                            tint = Color.Black,
                            modifier = Modifier.size(150.dp)
                        )
                    }

                    // Green WhatsApp lock in center of QR
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 60-digit safety numbers grid (4 rows of 3 blocks)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "60-Digit Safety Number",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    safetyNumberBlocks.chunked(3).forEach { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            row.forEach { block ->
                                Text(
                                    text = block,
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Verified Badge or Verify Button
            if (uiState.isSecurityCodeVerified) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = WhatsAppGreenPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = WhatsAppGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Security code verified with $contactName",
                            fontWeight = FontWeight.Bold,
                            color = WhatsAppGreenPrimary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = { viewModel.verifySecurityCode() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verify_code_button"),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = WhatsAppGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan Code or Mark Verified", color = WhatsAppGreenPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Explanation Text
            Text(
                text = "To verify that messages and calls with $contactName are end-to-end encrypted, scan this code on their phone, or ask them to scan your code. You can also compare the 60-digit number above.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Learn more about end-to-end encryption",
                fontSize = 13.sp,
                color = WhatsAppGreenPrimary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
