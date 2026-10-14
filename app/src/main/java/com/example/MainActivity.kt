package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.auth.*
import com.example.ui.call.ActiveCallScreen
import com.example.ui.chat.ChatDetailScreen
import com.example.ui.group.NewBroadcastScreen
import com.example.ui.group.NewChatScreen
import com.example.ui.group.NewGroupScreen
import com.example.ui.home.WhatsAppHomeScreen
import com.example.ui.security.SecurityVerifyScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.status.StatusCreatorScreen
import com.example.ui.status.StatusViewerScreen
import com.example.ui.theme.WhatsAppTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.WhatsAppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhatsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WhatsAppAppRoot()
                }
            }
        }
    }
}

@Composable
fun WhatsAppAppRoot(
    viewModel: WhatsAppViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.runtime.LaunchedEffect(Unit) {
        val repo = com.example.data.FirebaseWhatsAppRepository(context.applicationContext)
        viewModel.initFirebase(repo)
    }

    val uiState by viewModel.uiState.collectAsState()

    when (uiState.currentScreen) {
        AppScreen.AUTH_WELCOME -> WelcomeScreen(
            onAgreeAndContinue = { viewModel.navigateTo(AppScreen.AUTH_PHONE) }
        )
        AppScreen.AUTH_PHONE -> PhoneInputScreen(
            viewModel = viewModel
        )
        AppScreen.AUTH_OTP -> OtpVerificationScreen(
            viewModel = viewModel
        )
        AppScreen.AUTH_GOOGLE -> GoogleAccountLinkScreen(
            viewModel = viewModel
        )
        AppScreen.AUTH_PROFILE -> ProfileSetupScreen(
            viewModel = viewModel
        )
        AppScreen.MAIN_HOME -> WhatsAppHomeScreen(
            viewModel = viewModel,
            uiState = uiState
        )
        AppScreen.CHAT_DETAIL -> ChatDetailScreen(
            viewModel = viewModel,
            uiState = uiState
        )
        AppScreen.STATUS_VIEWER -> StatusViewerScreen(
            viewModel = viewModel,
            uiState = uiState
        )
        AppScreen.STATUS_CREATOR -> StatusCreatorScreen(
            viewModel = viewModel
        )
        AppScreen.ACTIVE_CALL -> ActiveCallScreen(
            viewModel = viewModel,
            uiState = uiState
        )
        AppScreen.SECURITY_VERIFY -> SecurityVerifyScreen(
            viewModel = viewModel,
            uiState = uiState
        )
        AppScreen.NEW_CHAT -> NewChatScreen(
            viewModel = viewModel,
            uiState = uiState
        )
        AppScreen.NEW_GROUP -> NewGroupScreen(
            viewModel = viewModel,
            uiState = uiState
        )
        AppScreen.NEW_BROADCAST -> NewBroadcastScreen(
            viewModel = viewModel,
            uiState = uiState
        )
        AppScreen.SETTINGS -> SettingsScreen(
            viewModel = viewModel,
            uiState = uiState
        )
        else -> WhatsAppHomeScreen(
            viewModel = viewModel,
            uiState = uiState
        )
    }
}
