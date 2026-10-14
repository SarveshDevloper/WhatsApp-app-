package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.WhatsAppViewModel

data class CountryInfo(val name: String, val code: String)

val COUNTRIES = listOf(
    CountryInfo("India", "+91"),
    CountryInfo("United States", "+1"),
    CountryInfo("United Kingdom", "+44"),
    CountryInfo("United Arab Emirates", "+971"),
    CountryInfo("Canada", "+1"),
    CountryInfo("Saudi Arabia", "+966"),
    CountryInfo("Australia", "+61"),
    CountryInfo("Germany", "+49"),
    CountryInfo("France", "+33"),
    CountryInfo("Singapore", "+65"),
    CountryInfo("Pakistan", "+92"),
    CountryInfo("Bangladesh", "+880")
)

@Composable
fun WelcomeScreen(
    onAgreeAndContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf("English") }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 48.dp)
        ) {
            Text(
                text = "Welcome to WhatsApp",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = WhatsAppGreenPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Central WhatsApp Logo Emblem
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(WhatsAppGreenPrimary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "WhatsApp",
                    tint = WhatsAppGreenPrimary,
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Read our Privacy Policy. Tap \"Agree and continue\" to accept the Terms of Service.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Language Selector Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .testTag("language_selector_button")
                    .clickable { showLanguageDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = WhatsAppGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedLanguage,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAgreeAndContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("agree_and_continue_button"),
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "AGREE AND CONTINUE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    }

    if (showLanguageDialog) {
        val languages = listOf("English", "हिन्दी (Hindi)", "मराठी (Marathi)", "ગુજરાતી (Gujarati)", "Español")
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("App Language") },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = lang.split(" ").first()
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage.startsWith(lang.take(5)),
                                onClick = {
                                    selectedLanguage = lang.split(" ").first()
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun PhoneInputScreen(
    viewModel: WhatsAppViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showCountryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Enter your phone number",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WhatsAppGreenPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "WhatsApp will need to verify your phone number. Carrier SMS charges may apply.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Country Selector Row (Clickable)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCountryDialog = true }
                    .testTag("country_picker_button"),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.inputCountryName} (${uiState.inputCountryCode})",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select country")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Country Code + Phone Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.inputCountryCode,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .width(90.dp)
                        .testTag("country_code_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = uiState.inputPhoneNumber,
                    onValueChange = { viewModel.onPhoneNumberChanged(it) },
                    placeholder = { Text("Enter your mobile number") },
                    isError = uiState.phoneError != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("phone_number_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Real-time Phone Number Error Display
            uiState.phoneError?.let { errorMsg ->
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Help info
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = WhatsAppGreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enter your real 10-digit mobile number to link with your WhatsApp account.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Button(
            onClick = {
                val clean = uiState.inputPhoneNumber.filter { it.isDigit() }
                if (clean.isBlank()) {
                    viewModel.submitPhoneNumber()
                } else if (clean.length != 10 && uiState.inputCountryCode == "+91") {
                    viewModel.submitPhoneNumber()
                } else {
                    showConfirmDialog = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("phone_next_button"),
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("NEXT", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }

    if (showCountryDialog) {
        AlertDialog(
            onDismissRequest = { showCountryDialog = false },
            title = { Text("Choose a country", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 350.dp)) {
                    items(COUNTRIES) { country ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onCountryChanged(country.name, country.code)
                                    showCountryDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(country.name, fontSize = 16.sp)
                            Text(country.code, fontWeight = FontWeight.Bold, color = WhatsAppGreenPrimary)
                        }
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showConfirmDialog) {
        val fullNumber = "${uiState.inputCountryCode} ${uiState.inputPhoneNumber}"
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("You entered the phone number:") },
            text = {
                Column {
                    Text(
                        text = fullNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = WhatsAppGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("We will send a 6-digit verification code to verify this number. Is this OK, or would you like to edit the number?")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.submitPhoneNumber()
                    },
                    modifier = Modifier.testTag("confirm_phone_ok_button")
                ) {
                    Text("OK", color = WhatsAppGreenPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("EDIT", color = WhatsAppGreenPrimary)
                }
            }
        )
    }
}

@Composable
fun OtpVerificationScreen(
    viewModel: WhatsAppViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Verifying your number",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WhatsAppGreenPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Enter the 6-digit verification code for ${uiState.inputCountryCode} ${uiState.inputPhoneNumber}.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 6-digit OTP Box display
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val otp = uiState.inputOtp
                (0..5).forEach { index ->
                    val char = if (index < otp.length) otp[index].toString() else "—"
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .border(
                                width = if (index == otp.length) 2.dp else 1.dp,
                                color = if (uiState.otpError != null) MaterialTheme.colorScheme.error
                                else if (index == otp.length) WhatsAppGreenPrimary
                                else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.otpError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-time Text Input for OTP
            OutlinedTextField(
                value = uiState.inputOtp,
                onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) viewModel.onOtpDigitEntered(it) },
                placeholder = { Text("Type 6-digit code") },
                isError = uiState.otpError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .testTag("otp_input_field"),
                shape = RoundedCornerShape(12.dp)
            )

            // OTP Error Display
            uiState.otpError?.let { err ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isOtpVerifying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = WhatsAppGreenPrimary,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Verifying security code...", fontSize = 12.sp, color = WhatsAppGreenPrimary)
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.autoFillOtp() }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = WhatsAppGreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Security Code: ${uiState.expectedOtp}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Tap here to verify for ${uiState.inputCountryCode} ${uiState.inputPhoneNumber}", fontSize = 12.sp, color = WhatsAppGreenPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { if (uiState.otpTimerSeconds == 0) viewModel.resendOtp() },
                    enabled = uiState.otpTimerSeconds == 0
                ) {
                    Text(
                        text = "Resend Code",
                        color = if (uiState.otpTimerSeconds == 0) WhatsAppGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (uiState.otpTimerSeconds > 0) "${uiState.otpTimerSeconds}s" else "Ready",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        TextButton(
            onClick = { viewModel.navigateBack() },
            modifier = Modifier.testTag("wrong_number_button")
        ) {
            Text("Wrong number?", color = WhatsAppGreenPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GoogleAccountLinkScreen(
    viewModel: WhatsAppViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Firebase User Authentication",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WhatsAppGreenPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Connect with Firebase to create your real WhatsApp account, secure your chats, and sync in real time.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Verified Status Banner if already authenticated
            if (uiState.isGoogleAuthenticated && uiState.inputGoogleEmail.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WhatsAppGreenPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = WhatsAppGreenPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(uiState.inputGoogleEmail, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Authenticated with Firebase Auth", fontSize = 12.sp, color = WhatsAppGreenPrimary)
                            }
                        }
                        if (uiState.firebaseUid.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Firebase UID: ${uiState.firebaseUid.take(12)}...",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                // Method 1: Real Google Sign-In Card via Credential Manager
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val activity = context as? android.app.Activity
                            if (activity != null) {
                                val credentialManager = androidx.credentials.CredentialManager.create(context)
                                viewModel.authenticateWithGoogle(activity, credentialManager) { success ->
                                    // Managed inside ViewModel
                                }
                            }
                        }
                        .testTag("google_signin_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4285F4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sign in with Google", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("One-tap sign-in with your device Google Account", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (uiState.isAuthenticating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = WhatsAppGreenPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text("  OR FIREBASE EMAIL & PASSWORD  ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle tabs: Sign Up vs Sign In
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { if (!uiState.authModeIsSignUp) viewModel.toggleAuthMode() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.authModeIsSignUp) WhatsAppGreenPrimary else Color.Transparent,
                            contentColor = if (uiState.authModeIsSignUp) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sign Up", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Button(
                        onClick = { if (uiState.authModeIsSignUp) viewModel.toggleAuthMode() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!uiState.authModeIsSignUp) WhatsAppGreenPrimary else Color.Transparent,
                            contentColor = if (!uiState.authModeIsSignUp) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Log In", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = uiState.authEmail,
                    onValueChange = { viewModel.onAuthEmailChanged(it) },
                    placeholder = { Text("Enter your email address") },
                    label = { Text("Firebase Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_email_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.authPassword,
                    onValueChange = { viewModel.onAuthPasswordChanged(it) },
                    placeholder = { Text("Password (at least 6 characters)") },
                    label = { Text("Password") },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_password_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Auth Error Banner
                uiState.authError?.let { err ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (uiState.authModeIsSignUp) {
                            viewModel.signUpWithFirebaseEmailPassword { }
                        } else {
                            viewModel.signInWithFirebaseEmailPassword { }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("firebase_email_submit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
                    shape = RoundedCornerShape(20.dp),
                    enabled = !uiState.isAuthenticating
                ) {
                    if (uiState.isAuthenticating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (uiState.authModeIsSignUp) "CREATE FIREBASE ACCOUNT" else "SIGN IN TO FIREBASE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = WhatsAppGreenPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Real-time sync with Firebase Firestore cloud storage enabled",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.navigateTo(AppScreen.AUTH_PROFILE)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("google_continue_button"),
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("CONTINUE TO PROFILE SETUP", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun ProfileSetupScreen(
    viewModel: WhatsAppViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf(uiState.inputDisplayName) }
    var selectedAbout by remember { mutableStateOf("Hey there! I am using WhatsApp.") }

    val aboutPresets = listOf(
        "Hey there! I am using WhatsApp.",
        "Available",
        "Busy",
        "At work",
        "In a meeting",
        "Urgent calls only"
    )

    if (uiState.isFirestoreSyncing) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(54.dp),
                    color = WhatsAppGreenPrimary,
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Saving Account to Firebase Firestore...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhatsAppGreenPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Generating end-to-end encryption keys and setting up your real-time WhatsApp session.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Profile info",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WhatsAppGreenPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Please enter your real name and select your status.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar with dynamic initials from typed name
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(WhatsAppGreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.trim().take(2).uppercase().ifBlank { "?" },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Add Photo",
                        tint = WhatsAppGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    viewModel.onDisplayNameChanged(it)
                },
                placeholder = { Text("Enter your real name") },
                label = { Text("Your Name") },
                isError = uiState.nameError != null,
                trailingIcon = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.EmojiEmotions, contentDescription = "Emoji", tint = WhatsAppGreenPrimary)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_name_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Name Error Display
            uiState.nameError?.let { err ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ $err",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Verified Phone and Gmail Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = WhatsAppGreenPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Phone: ${uiState.inputCountryCode} ${uiState.inputPhoneNumber}".ifBlank { "Phone: Registered" },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    if (uiState.inputGoogleEmail.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = WhatsAppGreenPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Firebase Account: ${uiState.inputGoogleEmail}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About status presets
            Text(
                text = "Select your About status:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                aboutPresets.take(4).forEach { preset ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedAbout = preset },
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedAbout == preset) WhatsAppGreenPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedAbout == preset,
                                onClick = { selectedAbout = preset }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(preset, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // E2EE Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFF9C4).copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF856404), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "🔒 Your profile and chats are secured with end-to-end encryption in Firebase.",
                        fontSize = 12.sp,
                        color = Color(0xFF856404)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.onDisplayNameChanged(name)
                viewModel.validateAndCompleteProfile(selectedAbout)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("finish_profile_button"),
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("FINISH & CREATE ACCOUNT", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
