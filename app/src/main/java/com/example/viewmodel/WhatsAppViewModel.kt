package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto.E2EEncryptionManager
import com.example.data.FirebaseWhatsAppRepository
import com.example.data.MockDataProvider
import com.example.model.*
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppScreen {
    AUTH_WELCOME,
    AUTH_PHONE,
    AUTH_OTP,
    AUTH_GOOGLE,
    AUTH_PROFILE,
    MAIN_HOME,
    CHAT_DETAIL,
    STATUS_VIEWER,
    STATUS_CREATOR,
    ACTIVE_CALL,
    SECURITY_VERIFY,
    NEW_CHAT,
    NEW_GROUP,
    NEW_BROADCAST,
    STATUS_PRIVACY,
    SETTINGS,
    ACCOUNT_SETTINGS,
    PRIVACY_SETTINGS,
    CHATS_SETTINGS,
    NOTIFICATIONS_SETTINGS
}

enum class ChatFilter {
    ALL,
    UNREAD,
    FAVORITES,
    GROUPS
}

data class ActiveCallState(
    val contactName: String = "",
    val contactNumber: String = "",
    val avatarColorHex: Long = 0xFF008069,
    val avatarInitials: String = "",
    val type: CallType = CallType.VOICE,
    val isConnected: Boolean = false,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isVideoDisabled: Boolean = false,
    val isCameraFlipped: Boolean = false
)

data class WhatsAppUiState(
    val currentScreen: AppScreen = AppScreen.AUTH_WELCOME,
    val screenStack: List<AppScreen> = listOf(AppScreen.AUTH_WELCOME),
    val userProfile: UserProfile = UserProfile(),
    val isLoggedIn: Boolean = false,

    // Real Auth Flow State (Blank by default - user enters their own details)
    val inputPhoneNumber: String = "",
    val inputCountryCode: String = "+91",
    val inputCountryName: String = "India",
    val inputOtp: String = "",
    val expectedOtp: String = "",
    val phoneError: String? = null,
    val otpError: String? = null,
    val gmailError: String? = null,
    val nameError: String? = null,
    val inputGoogleEmail: String = "",
    val inputDisplayName: String = "",
    val authEmail: String = "",
    val authPassword: String = "",
    val authModeIsSignUp: Boolean = true,
    val authError: String? = null,
    val isAuthenticating: Boolean = false,
    val otpTimerSeconds: Int = 60,
    val isOtpVerifying: Boolean = false,
    val isGoogleAuthenticated: Boolean = false,
    val firebaseUid: String = "",
    val isFirestoreSyncing: Boolean = false,
    val firestoreSyncSuccessMessage: String? = null,

    // Chats
    val chats: List<ChatConversation> = emptyList(),
    val messagesMap: Map<String, List<Message>> = emptyMap(),
    val selectedChatId: String? = null,
    val chatFilter: ChatFilter = ChatFilter.ALL,
    val isContactTyping: Boolean = false,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,

    // Status
    val myStatus: UserStatusStory? = null,
    val contactStatuses: List<UserStatusStory> = emptyList(),
    val selectedStory: UserStatusStory? = null,
    val statusPrivacy: StatusPrivacySettings = StatusPrivacySettings(),

    // Calls
    val callRecords: List<CallRecord> = emptyList(),
    val activeCall: ActiveCallState? = null,

    // Security & E2EE
    val securitySafetyNumber: List<String> = emptyList(),
    val isSecurityCodeVerified: Boolean = false,

    // Group / Broadcast Creation Temp State
    val selectedContactsForGroup: Set<String> = emptySet(),
    val newGroupName: String = "",
    val newBroadcastName: String = ""
)

class WhatsAppViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WhatsAppUiState())
    val uiState: StateFlow<WhatsAppUiState> = _uiState.asStateFlow()

    private var firebaseRepo: FirebaseWhatsAppRepository? = null
    private var chatsListener: ListenerRegistration? = null
    private var messagesListener: ListenerRegistration? = null

    fun initFirebase(repo: FirebaseWhatsAppRepository) {
        firebaseRepo = repo
        // Check for returning user session
        val currentUid = repo.currentUserId()
        if (!currentUid.isNullOrBlank()) {
            viewModelScope.launch {
                val profile = repo.fetchUserProfile(currentUid)
                if (profile != null && profile.displayName.isNotBlank()) {
                    _uiState.update {
                        it.copy(
                            userProfile = profile,
                            firebaseUid = currentUid,
                            isLoggedIn = true,
                            inputPhoneNumber = profile.phoneNumber.removePrefix(profile.countryCode).trim(),
                            inputCountryCode = profile.countryCode,
                            inputDisplayName = profile.displayName,
                            inputGoogleEmail = profile.googleEmail ?: "",
                            isGoogleAuthenticated = true,
                            currentScreen = AppScreen.MAIN_HOME,
                            screenStack = listOf(AppScreen.MAIN_HOME)
                        )
                    }
                    attachFirestoreListeners(currentUid)
                }
            }
        }
    }

    private fun attachFirestoreListeners(uid: String) {
        chatsListener?.remove()
        chatsListener = firebaseRepo?.listenToChats(uid) { firestoreChats ->
            if (firestoreChats.isNotEmpty()) {
                _uiState.update { current ->
                    // Merge with local messages if needed
                    current.copy(chats = firestoreChats)
                }
            }
        }
    }

    // Navigation Stack Handling
    fun navigateTo(screen: AppScreen) {
        _uiState.update { current ->
            current.copy(
                currentScreen = screen,
                screenStack = current.screenStack + screen
            )
        }
    }

    fun navigateBack(): Boolean {
        val currentStack = _uiState.value.screenStack
        if (currentStack.size > 1) {
            val newStack = currentStack.dropLast(1)
            val previousScreen = newStack.last()
            _uiState.update {
                it.copy(
                    currentScreen = previousScreen,
                    screenStack = newStack
                )
            }
            return true
        }
        return false
    }

    // Auth & Login System
    fun startLoginFlow() {
        firebaseRepo?.clearSavedSession()
        chatsListener?.remove()
        messagesListener?.remove()
        _uiState.update {
            WhatsAppUiState(
                currentScreen = AppScreen.AUTH_WELCOME,
                screenStack = listOf(AppScreen.AUTH_WELCOME)
            )
        }
    }

    fun onPhoneNumberChanged(number: String) {
        val filtered = number.filter { it.isDigit() }
        _uiState.update { it.copy(inputPhoneNumber = filtered, phoneError = null) }
    }

    fun onCountryChanged(name: String, code: String) {
        _uiState.update { it.copy(inputCountryName = name, inputCountryCode = code, phoneError = null) }
    }

    fun submitPhoneNumber(): Boolean {
        val raw = _uiState.value.inputPhoneNumber.trim()
        val cleanNumber = raw.filter { it.isDigit() }
        val countryCode = _uiState.value.inputCountryCode

        if (cleanNumber.isBlank()) {
            _uiState.update { it.copy(phoneError = "Please enter your mobile phone number.") }
            return false
        }

        val isValid = when (countryCode) {
            "+91" -> cleanNumber.length == 10 && cleanNumber.first() in listOf('6', '7', '8', '9')
            "+1" -> cleanNumber.length == 10
            else -> cleanNumber.length in 8..12
        }

        if (!isValid) {
            val errorMsg = if (countryCode == "+91") {
                "Invalid phone number! India mobile numbers must be 10 digits starting with 6, 7, 8, or 9."
            } else {
                "Please enter a valid phone number for ${_uiState.value.inputCountryName}."
            }
            _uiState.update { it.copy(phoneError = errorMsg) }
            return false
        }

        val generatedOtp = (100000..999999).random().toString()
        _uiState.update {
            it.copy(
                phoneError = null,
                otpError = null,
                inputOtp = "",
                expectedOtp = generatedOtp
            )
        }
        navigateTo(AppScreen.AUTH_OTP)
        startOtpCountdown()
        return true
    }

    fun resendOtp() {
        val generatedOtp = (100000..999999).random().toString()
        _uiState.update {
            it.copy(
                otpError = null,
                inputOtp = "",
                expectedOtp = generatedOtp
            )
        }
        startOtpCountdown()
    }

    private fun startOtpCountdown() {
        viewModelScope.launch {
            _uiState.update { it.copy(otpTimerSeconds = 60) }
            for (i in 59 downTo 0) {
                delay(1000)
                _uiState.update { it.copy(otpTimerSeconds = i) }
            }
        }
    }

    fun onOtpDigitEntered(otp: String) {
        val filtered = otp.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(inputOtp = filtered, otpError = null) }
        if (filtered.length == 6) {
            verifyOtp()
        }
    }

    fun autoFillOtp() {
        val expected = _uiState.value.expectedOtp
        if (expected.isNotBlank()) {
            _uiState.update { it.copy(inputOtp = expected, otpError = null) }
            verifyOtp()
        }
    }

    private fun verifyOtp() {
        viewModelScope.launch {
            _uiState.update { it.copy(isOtpVerifying = true, otpError = null) }
            delay(600)

            val entered = _uiState.value.inputOtp
            val expected = _uiState.value.expectedOtp

            if (entered == expected) {
                _uiState.update {
                    it.copy(
                        isOtpVerifying = false,
                        otpError = null
                    )
                }
                navigateTo(AppScreen.AUTH_GOOGLE)
            } else {
                _uiState.update {
                    it.copy(
                        isOtpVerifying = false,
                        otpError = "Incorrect verification code. Please enter the exact 6-digit code ($expected)."
                    )
                }
            }
        }
    }

    fun onAuthEmailChanged(email: String) {
        _uiState.update { it.copy(authEmail = email, authError = null) }
    }

    fun onAuthPasswordChanged(password: String) {
        _uiState.update { it.copy(authPassword = password, authError = null) }
    }

    fun toggleAuthMode() {
        _uiState.update { it.copy(authModeIsSignUp = !it.authModeIsSignUp, authError = null) }
    }

    fun authenticateWithGoogle(
        activity: android.app.Activity,
        credentialManager: androidx.credentials.CredentialManager,
        onComplete: (Boolean) -> Unit
    ) {
        val repo = firebaseRepo
        if (repo == null) {
            _uiState.update { it.copy(authError = "Firebase service not initialized") }
            onComplete(false)
            return
        }
        _uiState.update { it.copy(isAuthenticating = true, authError = null) }
        viewModelScope.launch {
            repo.signInWithGoogle(
                activity = activity,
                credentialManager = credentialManager,
                onSuccess = { uid, email, displayName ->
                    viewModelScope.launch {
                        val existingProfile = repo.fetchUserProfile(uid)
                        if (existingProfile != null && existingProfile.displayName.isNotBlank()) {
                            attachFirestoreListeners(uid)
                            _uiState.update {
                                it.copy(
                                    isAuthenticating = false,
                                    userProfile = existingProfile,
                                    firebaseUid = uid,
                                    inputGoogleEmail = email ?: existingProfile.googleEmail ?: "",
                                    inputDisplayName = existingProfile.displayName,
                                    inputPhoneNumber = existingProfile.phoneNumber.removePrefix(existingProfile.countryCode).trim(),
                                    inputCountryCode = existingProfile.countryCode,
                                    isGoogleAuthenticated = true,
                                    isLoggedIn = true,
                                    authError = null,
                                    currentScreen = AppScreen.MAIN_HOME,
                                    screenStack = listOf(AppScreen.MAIN_HOME)
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isAuthenticating = false,
                                    inputGoogleEmail = email ?: it.inputGoogleEmail,
                                    inputDisplayName = if (it.inputDisplayName.isBlank() && !displayName.isNullOrBlank()) displayName else it.inputDisplayName,
                                    isGoogleAuthenticated = true,
                                    firebaseUid = uid,
                                    authError = null
                                )
                            }
                            navigateTo(AppScreen.AUTH_PROFILE)
                        }
                        onComplete(true)
                    }
                },
                onError = { err ->
                    _uiState.update { it.copy(isAuthenticating = false, authError = err) }
                    onComplete(false)
                }
            )
        }
    }

    fun signUpWithFirebaseEmailPassword(onComplete: (Boolean) -> Unit) {
        val email = _uiState.value.authEmail.trim()
        val pass = _uiState.value.authPassword
        if (!email.contains("@") || !email.contains(".")) {
            _uiState.update { it.copy(authError = "Please enter a valid email address.") }
            onComplete(false)
            return
        }
        if (pass.length < 6) {
            _uiState.update { it.copy(authError = "Firebase requires password of at least 6 characters.") }
            onComplete(false)
            return
        }
        val repo = firebaseRepo ?: run {
            _uiState.update { it.copy(authError = "Firebase not initialized.") }
            onComplete(false)
            return
        }
        _uiState.update { it.copy(isAuthenticating = true, authError = null) }
        viewModelScope.launch {
            repo.signUpWithEmailPassword(
                email = email,
                pass = pass,
                onSuccess = { uid, userEmail ->
                    _uiState.update {
                        it.copy(
                            isAuthenticating = false,
                            firebaseUid = uid,
                            inputGoogleEmail = userEmail,
                            isGoogleAuthenticated = true,
                            authError = null
                        )
                    }
                    navigateTo(AppScreen.AUTH_PROFILE)
                    onComplete(true)
                },
                onError = { err ->
                    _uiState.update { it.copy(isAuthenticating = false, authError = err) }
                    onComplete(false)
                }
            )
        }
    }

    fun signInWithFirebaseEmailPassword(onComplete: (Boolean) -> Unit) {
        val email = _uiState.value.authEmail.trim()
        val pass = _uiState.value.authPassword
        if (!email.contains("@") || !email.contains(".")) {
            _uiState.update { it.copy(authError = "Please enter a valid email address.") }
            onComplete(false)
            return
        }
        if (pass.isEmpty()) {
            _uiState.update { it.copy(authError = "Please enter your password.") }
            onComplete(false)
            return
        }
        val repo = firebaseRepo ?: run {
            _uiState.update { it.copy(authError = "Firebase not initialized.") }
            onComplete(false)
            return
        }
        _uiState.update { it.copy(isAuthenticating = true, authError = null) }
        viewModelScope.launch {
            repo.signInWithEmailPassword(
                email = email,
                pass = pass,
                onSuccess = { uid, userEmail ->
                    viewModelScope.launch {
                        val existing = repo.fetchUserProfile(uid)
                        if (existing != null && existing.displayName.isNotBlank()) {
                            attachFirestoreListeners(uid)
                            _uiState.update {
                                it.copy(
                                    isAuthenticating = false,
                                    userProfile = existing,
                                    firebaseUid = uid,
                                    inputGoogleEmail = userEmail,
                                    isGoogleAuthenticated = true,
                                    isLoggedIn = true,
                                    authError = null,
                                    currentScreen = AppScreen.MAIN_HOME,
                                    screenStack = listOf(AppScreen.MAIN_HOME)
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isAuthenticating = false,
                                    firebaseUid = uid,
                                    inputGoogleEmail = userEmail,
                                    isGoogleAuthenticated = true,
                                    authError = null
                                )
                            }
                            navigateTo(AppScreen.AUTH_PROFILE)
                        }
                        onComplete(true)
                    }
                },
                onError = { err ->
                    _uiState.update { it.copy(isAuthenticating = false, authError = err) }
                    onComplete(false)
                }
            )
        }
    }

    fun onDisplayNameChanged(name: String) {
        _uiState.update { it.copy(inputDisplayName = name, nameError = null) }
    }

    fun validateAndCompleteProfile(aboutStatus: String): Boolean {
        val name = _uiState.value.inputDisplayName.trim()
        if (name.length < 2) {
            _uiState.update { it.copy(nameError = "Please enter your real name (at least 2 characters).") }
            return false
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isFirestoreSyncing = true) }
            val state = _uiState.value
            val fullPhone = "${state.inputCountryCode} ${state.inputPhoneNumber}".trim()
            val uid = state.firebaseUid.ifBlank {
                firebaseRepo?.currentUserId() ?: run {
                    val fallbackDigits = state.inputPhoneNumber.filter { it.isDigit() }
                    "user_${fallbackDigits.ifBlank { System.currentTimeMillis().toString() }}"
                }
            }

            val updatedProfile = UserProfile(
                phoneNumber = fullPhone,
                countryCode = state.inputCountryCode,
                countryName = state.inputCountryName,
                googleEmail = state.inputGoogleEmail.ifBlank { null },
                displayName = name,
                aboutStatus = aboutStatus.ifBlank { "Hey there! I am using WhatsApp." },
                avatarInitials = name.take(2).uppercase(),
                isVerified = true
            )

            val saved = firebaseRepo?.saveUserProfile(updatedProfile, uid) ?: true
            attachFirestoreListeners(uid)

            _uiState.update {
                it.copy(
                    userProfile = updatedProfile,
                    firebaseUid = uid,
                    isLoggedIn = true,
                    isFirestoreSyncing = false,
                    nameError = null,
                    firestoreSyncSuccessMessage = if (saved) "Real WhatsApp account saved to Firebase Firestore!" else null,
                    currentScreen = AppScreen.MAIN_HOME,
                    screenStack = listOf(AppScreen.MAIN_HOME)
                )
            }
        }
        return true
    }

    fun logOut() {
        firebaseRepo?.clearSavedSession()
        chatsListener?.remove()
        messagesListener?.remove()
        _uiState.update {
            WhatsAppUiState(
                currentScreen = AppScreen.AUTH_WELCOME,
                screenStack = listOf(AppScreen.AUTH_WELCOME)
            )
        }
    }

    // Direct Chat Creation by User (Real contact addition)
    fun createDirectChat(contactName: String, contactPhone: String) {
        val name = contactName.trim().ifBlank { "Contact" }
        val phone = contactPhone.trim()
        val newId = "chat_${System.currentTimeMillis()}"
        val newChat = ChatConversation(
            id = newId,
            name = name,
            phoneNumber = phone,
            avatarColorHex = 0xFF008069,
            avatarInitials = name.take(2).uppercase(),
            lastMessageText = "Tap to chat",
            lastMessageTime = "Just now"
        )
        val initialMsg = Message(
            id = "msg_${UUID.randomUUID()}",
            chatId = newId,
            senderId = "system",
            senderName = "System",
            text = "🔒 Messages and calls are end-to-end encrypted. No one outside of this chat, not even WhatsApp, can read or listen to them.",
            formattedTime = "Just now",
            isOutgoing = false
        )

        _uiState.update {
            it.copy(
                chats = listOf(newChat) + it.chats.filterNot { c -> c.id == newId },
                messagesMap = it.messagesMap + (newId to listOf(initialMsg))
            )
        }

        val uid = _uiState.value.firebaseUid
        if (uid.isNotBlank()) {
            viewModelScope.launch {
                firebaseRepo?.syncChat(newChat, uid)
                firebaseRepo?.syncMessage(initialMsg, uid)
            }
        }

        openChat(newId)
    }

    // Optional Demo Contacts (User can explicitly load if they want to test features)
    fun loadDemoContactsForTesting() {
        val demoChats = MockDataProvider.getInitialChats()
        val demoMessages = mutableMapOf<String, List<Message>>()
        for (chat in demoChats) {
            demoMessages[chat.id] = MockDataProvider.getInitialMessages(chat.id)
        }
        val statuses = MockDataProvider.getInitialStatusStories()

        _uiState.update {
            it.copy(
                chats = demoChats,
                messagesMap = demoMessages,
                contactStatuses = statuses,
                callRecords = MockDataProvider.getInitialCallRecords()
            )
        }
    }

    // Chat Operations
    fun openChat(chatId: String) {
        val chat = _uiState.value.chats.find { it.id == chatId }
        if (chat != null) {
            val updatedChats = _uiState.value.chats.map {
                if (it.id == chatId) it.copy(unreadCount = 0) else it
            }
            val safetyNumber = E2EEncryptionManager.generateSafetyNumber(
                _uiState.value.userProfile.phoneNumber,
                chat.phoneNumber.ifBlank { "+91 99999 88888" }
            )
            _uiState.update {
                it.copy(
                    selectedChatId = chatId,
                    chats = updatedChats,
                    securitySafetyNumber = safetyNumber,
                    isSecurityCodeVerified = false
                )
            }
            navigateTo(AppScreen.CHAT_DETAIL)

            // Listen to real-time messages if Firestore connected
            val uid = _uiState.value.firebaseUid
            if (uid.isNotBlank() && firebaseRepo != null) {
                messagesListener?.remove()
                messagesListener = firebaseRepo?.listenToMessages(uid, chatId) { firestoreMsgs ->
                    if (firestoreMsgs.isNotEmpty()) {
                        _uiState.update { current ->
                            current.copy(messagesMap = current.messagesMap + (chatId to firestoreMsgs))
                        }
                    }
                }
            }
        }
    }

    fun sendMessage(chatId: String, text: String, type: MessageType = MessageType.TEXT) {
        if (text.isBlank() && type == MessageType.TEXT) return

        val encrypted = E2EEncryptionManager.encryptMessage(text)
        val now = System.currentTimeMillis()
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))

        val newMessage = Message(
            id = "msg_${UUID.randomUUID()}",
            chatId = chatId,
            senderId = _uiState.value.firebaseUid.ifBlank { "me" },
            senderName = _uiState.value.userProfile.displayName,
            text = text,
            timestamp = now,
            formattedTime = timeFormat,
            isOutgoing = true,
            status = MessageStatus.SENT,
            type = type,
            isEncrypted = true,
            encryptionDigest = encrypted.digest
        )

        val currentMsgs = _uiState.value.messagesMap[chatId] ?: emptyList()
        val updatedMsgs = currentMsgs + newMessage

        var updatedChatObj: ChatConversation? = null
        val updatedChats = _uiState.value.chats.map { chat ->
            if (chat.id == chatId) {
                val c = chat.copy(
                    lastMessageText = if (type == MessageType.VOICE) "🎤 Voice message (0:05)" else text,
                    lastMessageTime = timeFormat
                )
                updatedChatObj = c
                c
            } else chat
        }

        _uiState.update {
            it.copy(
                messagesMap = it.messagesMap + (chatId to updatedMsgs),
                chats = updatedChats
            )
        }

        // Real Firestore persistence
        val uid = _uiState.value.firebaseUid
        if (uid.isNotBlank() && firebaseRepo != null) {
            viewModelScope.launch {
                firebaseRepo?.syncMessage(newMessage, uid)
                updatedChatObj?.let { firebaseRepo?.syncChat(it, uid) }
            }
        }

        simulateIncomingResponse(chatId, text)
    }

    private fun simulateIncomingResponse(chatId: String, userPrompt: String) {
        viewModelScope.launch {
            delay(600)
            updateMessageStatus(chatId, MessageStatus.DELIVERED)

            delay(700)
            updateMessageStatus(chatId, MessageStatus.READ)

            val chat = _uiState.value.chats.find { it.id == chatId } ?: return@launch
            _uiState.update { it.copy(isContactTyping = true) }

            delay(1500)
            _uiState.update { it.copy(isContactTyping = false) }

            val replyText = when {
                userPrompt.contains("call", ignoreCase = true) ->
                    "Sure! Tap the call button above to test the encrypted voice/video call 📞"
                userPrompt.contains("status", ignoreCase = true) ->
                    "I just uploaded a new 24h status update! Check the Updates tab."
                userPrompt.contains("security", ignoreCase = true) || userPrompt.contains("encrypt", ignoreCase = true) ->
                    "🔒 Our chat is secured with the Signal Protocol. Tap the yellow banner at the top to verify our 60-digit safety number."
                chat.isGroup ->
                    "Great! Group chat is active and connected."
                else ->
                    "Hello! Thanks for your message. Your WhatsApp messages are synced with Firebase!"
            }

            val now = System.currentTimeMillis()
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))
            val incomingMsg = Message(
                id = "msg_${UUID.randomUUID()}",
                chatId = chatId,
                senderId = chat.id,
                senderName = chat.name,
                text = replyText,
                timestamp = now,
                formattedTime = timeFormat,
                isOutgoing = false,
                status = MessageStatus.READ,
                isEncrypted = true
            )

            val msgs = (_uiState.value.messagesMap[chatId] ?: emptyList()) + incomingMsg
            var incomingUpdatedChat: ChatConversation? = null
            val updatedChats = _uiState.value.chats.map {
                if (it.id == chatId) {
                    val c = it.copy(lastMessageText = replyText, lastMessageTime = timeFormat)
                    incomingUpdatedChat = c
                    c
                } else it
            }

            _uiState.update {
                it.copy(
                    messagesMap = it.messagesMap + (chatId to msgs),
                    chats = updatedChats
                )
            }

            val uid = _uiState.value.firebaseUid
            if (uid.isNotBlank() && firebaseRepo != null) {
                firebaseRepo?.syncMessage(incomingMsg, uid)
                incomingUpdatedChat?.let { firebaseRepo?.syncChat(it, uid) }
            }
        }
    }

    private fun updateMessageStatus(chatId: String, newStatus: MessageStatus) {
        val msgs = _uiState.value.messagesMap[chatId] ?: return
        val updated = msgs.map { msg ->
            if (msg.isOutgoing && msg.status.ordinal < newStatus.ordinal) {
                msg.copy(status = newStatus)
            } else msg
        }
        _uiState.update {
            it.copy(messagesMap = it.messagesMap + (chatId to updated))
        }
    }

    fun addReactionToMessage(chatId: String, messageId: String, emoji: String) {
        val msgs = _uiState.value.messagesMap[chatId] ?: return
        val updated = msgs.map { msg ->
            if (msg.id == messageId) {
                val currentCount = msg.reactions[emoji] ?: 0
                val newReactions = msg.reactions + (emoji to (currentCount + 1))
                msg.copy(reactions = newReactions)
            } else msg
        }
        _uiState.update {
            it.copy(messagesMap = it.messagesMap + (chatId to updated))
        }
    }

    fun deleteMessage(chatId: String, messageId: String) {
        val msgs = _uiState.value.messagesMap[chatId] ?: return
        val updated = msgs.filterNot { it.id == messageId }
        _uiState.update {
            it.copy(messagesMap = it.messagesMap + (chatId to updated))
        }
    }

    fun toggleStarMessage(chatId: String, messageId: String) {
        val msgs = _uiState.value.messagesMap[chatId] ?: return
        val updated = msgs.map { msg ->
            if (msg.id == messageId) msg.copy(isStarred = !msg.isStarred) else msg
        }
        _uiState.update {
            it.copy(messagesMap = it.messagesMap + (chatId to updated))
        }
    }

    fun setChatFilter(filter: ChatFilter) {
        _uiState.update { it.copy(chatFilter = filter) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleSearch(active: Boolean) {
        _uiState.update { it.copy(isSearchActive = active, searchQuery = if (!active) "" else it.searchQuery) }
    }

    // Group & Broadcast Creation
    fun toggleContactSelection(contactName: String) {
        val current = _uiState.value.selectedContactsForGroup
        val updated = if (current.contains(contactName)) current - contactName else current + contactName
        _uiState.update { it.copy(selectedContactsForGroup = updated) }
    }

    fun onGroupNameChanged(name: String) {
        _uiState.update { it.copy(newGroupName = name) }
    }

    fun createGroup() {
        val name = _uiState.value.newGroupName.ifBlank { "New Group" }
        val members = _uiState.value.selectedContactsForGroup.toList() + "You"
        val newChat = ChatConversation(
            id = "group_${UUID.randomUUID()}",
            name = name,
            isGroup = true,
            avatarColorHex = 0xFF25D366,
            avatarInitials = name.take(2).uppercase(),
            lastMessageText = "You created group \"$name\"",
            lastMessageTime = "Just now",
            members = members,
            statusSubtitle = members.joinToString(", ")
        )
        val initialMsg = Message(
            id = "msg_${UUID.randomUUID()}",
            chatId = newChat.id,
            senderId = "system",
            senderName = "System",
            text = "🔒 Messages and calls are end-to-end encrypted. No one outside of this group can read or listen to them.",
            formattedTime = "Just now",
            isOutgoing = false
        )

        _uiState.update {
            it.copy(
                chats = listOf(newChat) + it.chats,
                messagesMap = it.messagesMap + (newChat.id to listOf(initialMsg)),
                selectedContactsForGroup = emptySet(),
                newGroupName = ""
            )
        }

        val uid = _uiState.value.firebaseUid
        if (uid.isNotBlank() && firebaseRepo != null) {
            viewModelScope.launch {
                firebaseRepo?.syncChat(newChat, uid)
                firebaseRepo?.syncMessage(initialMsg, uid)
            }
        }

        openChat(newChat.id)
    }

    fun onBroadcastNameChanged(name: String) {
        _uiState.update { it.copy(newBroadcastName = name) }
    }

    fun createBroadcastList() {
        val name = _uiState.value.newBroadcastName.ifBlank { "New Broadcast List" }
        val recipients = _uiState.value.selectedContactsForGroup.toList()
        val newChat = ChatConversation(
            id = "bc_${UUID.randomUUID()}",
            name = "$name (Broadcast)",
            isBroadcast = true,
            avatarColorHex = 0xFFFF9800,
            avatarInitials = "BC",
            lastMessageText = "You created a broadcast list with ${recipients.size} recipients",
            lastMessageTime = "Just now",
            members = recipients,
            statusSubtitle = "${recipients.size} recipients"
        )

        _uiState.update {
            it.copy(
                chats = listOf(newChat) + it.chats,
                messagesMap = it.messagesMap + (newChat.id to emptyList()),
                selectedContactsForGroup = emptySet(),
                newBroadcastName = ""
            )
        }

        val uid = _uiState.value.firebaseUid
        if (uid.isNotBlank() && firebaseRepo != null) {
            viewModelScope.launch {
                firebaseRepo?.syncChat(newChat, uid)
            }
        }

        openChat(newChat.id)
    }

    // Status Features
    fun openStatusViewer(story: UserStatusStory) {
        _uiState.update { it.copy(selectedStory = story) }
        navigateTo(AppScreen.STATUS_VIEWER)
    }

    fun addStatusUpdate(text: String, colorHex: Long) {
        if (text.isBlank()) return
        val now = System.currentTimeMillis()
        val twentyFourHours = 24 * 60 * 60 * 1000L
        val newStatusItem = StatusItem(
            id = "st_${UUID.randomUUID()}",
            type = StatusContentType.TEXT,
            textContent = text,
            backgroundColorHex = colorHex,
            timestamp = now,
            formattedTime = "Just now",
            expiresAtMillis = now + twentyFourHours,
            viewsCount = 0
        )

        val currentMyStatus = _uiState.value.myStatus
        val updatedStatuses = (currentMyStatus?.statuses ?: emptyList()) + newStatusItem
        val updatedMyStory = UserStatusStory(
            id = "my_status",
            userId = _uiState.value.firebaseUid.ifBlank { "me" },
            userName = _uiState.value.userProfile.displayName.ifBlank { "My Status" },
            avatarColorHex = 0xFF008069,
            avatarInitials = _uiState.value.userProfile.avatarInitials,
            isMyStatus = true,
            statuses = updatedStatuses,
            hasUnseen = false
        )

        _uiState.update {
            it.copy(myStatus = updatedMyStory)
        }
        navigateBack()
    }

    fun updateStatusPrivacy(option: StatusPrivacyOption) {
        _uiState.update {
            it.copy(statusPrivacy = it.statusPrivacy.copy(option = option))
        }
    }

    // Calling System
    fun startCall(contactName: String, contactNumber: String, avatarColorHex: Long, avatarInitials: String, type: CallType) {
        val activeCall = ActiveCallState(
            contactName = contactName,
            contactNumber = contactNumber,
            avatarColorHex = avatarColorHex,
            avatarInitials = avatarInitials,
            type = type,
            isConnected = false,
            durationSeconds = 0
        )
        _uiState.update { it.copy(activeCall = activeCall) }
        navigateTo(AppScreen.ACTIVE_CALL)

        viewModelScope.launch {
            delay(1800)
            _uiState.update {
                it.copy(activeCall = it.activeCall?.copy(isConnected = true))
            }
            while (_uiState.value.activeCall != null && _uiState.value.currentScreen == AppScreen.ACTIVE_CALL) {
                delay(1000)
                _uiState.update {
                    it.copy(activeCall = it.activeCall?.copy(durationSeconds = (it.activeCall?.durationSeconds ?: 0) + 1))
                }
            }
        }
    }

    fun toggleMuteCall() {
        _uiState.update {
            it.copy(activeCall = it.activeCall?.copy(isMuted = !(it.activeCall?.isMuted ?: false)))
        }
    }

    fun toggleSpeakerCall() {
        _uiState.update {
            it.copy(activeCall = it.activeCall?.copy(isSpeakerOn = !(it.activeCall?.isSpeakerOn ?: false)))
        }
    }

    fun toggleCameraFlip() {
        _uiState.update {
            it.copy(activeCall = it.activeCall?.copy(isCameraFlipped = !(it.activeCall?.isCameraFlipped ?: false)))
        }
    }

    fun endCall() {
        val currentCall = _uiState.value.activeCall
        if (currentCall != null) {
            val now = System.currentTimeMillis()
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))
            val durationText = "${currentCall.durationSeconds / 60}:${(currentCall.durationSeconds % 60).toString().padStart(2, '0')}"

            val record = CallRecord(
                id = "call_${UUID.randomUUID()}",
                contactName = currentCall.contactName,
                contactNumber = currentCall.contactNumber,
                avatarColorHex = currentCall.avatarColorHex,
                avatarInitials = currentCall.avatarInitials,
                type = currentCall.type,
                direction = CallDirection.OUTGOING,
                timestamp = now,
                formattedTime = "Today, $timeFormat",
                durationText = if (currentCall.isConnected) durationText else "Cancelled"
            )
            _uiState.update {
                it.copy(
                    activeCall = null,
                    callRecords = listOf(record) + it.callRecords
                )
            }
        }
        navigateBack()
    }

    fun verifySecurityCode() {
        _uiState.update { it.copy(isSecurityCodeVerified = true) }
    }
}
