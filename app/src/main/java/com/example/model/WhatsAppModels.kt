package com.example.model

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

enum class MessageType {
    TEXT,
    IMAGE,
    VOICE,
    LOCATION,
    DOCUMENT,
    CONTACT,
    POLL
}

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = "10:30 AM",
    val isOutgoing: Boolean = true,
    val status: MessageStatus = MessageStatus.READ,
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val voiceDurationSeconds: Int = 0,
    val reactions: Map<String, Int> = emptyMap(),
    val isStarred: Boolean = false,
    val isEncrypted: Boolean = true,
    val encryptionDigest: String = "SHA256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
)

data class ChatConversation(
    val id: String,
    val name: String,
    val phoneNumber: String = "",
    val isGroup: Boolean = false,
    val isBroadcast: Boolean = false,
    val avatarColorHex: Long = 0xFF008069,
    val avatarInitials: String = "WA",
    val lastMessageText: String = "",
    val lastMessageTime: String = "Just now",
    val unreadCount: Int = 0,
    val isMuted: Boolean = false,
    val isPinned: Boolean = false,
    val isOnline: Boolean = false,
    val statusSubtitle: String = "online",
    val members: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val encryptionKeyId: String = "KEY-E2EE-84920"
)

enum class StatusContentType {
    TEXT,
    PHOTO,
    VIDEO
}

data class StatusItem(
    val id: String,
    val type: StatusContentType = StatusContentType.TEXT,
    val textContent: String = "",
    val caption: String? = null,
    val backgroundColorHex: Long = 0xFF008069,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = "Today, 10:15 AM",
    val expiresAtMillis: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000L),
    val viewsCount: Int = 0,
    val viewers: List<String> = emptyList()
)

data class UserStatusStory(
    val id: String,
    val userId: String,
    val userName: String,
    val avatarColorHex: Long,
    val avatarInitials: String,
    val isMyStatus: Boolean = false,
    val statuses: List<StatusItem>,
    val hasUnseen: Boolean = true
)

enum class StatusPrivacyOption {
    MY_CONTACTS,
    MY_CONTACTS_EXCEPT,
    ONLY_SHARE_WITH
}

data class StatusPrivacySettings(
    val option: StatusPrivacyOption = StatusPrivacyOption.MY_CONTACTS,
    val excludedContacts: List<String> = emptyList(),
    val includedContacts: List<String> = emptyList()
)

enum class CallType {
    VOICE,
    VIDEO
}

enum class CallDirection {
    INCOMING,
    OUTGOING,
    MISSED
}

data class CallRecord(
    val id: String,
    val contactName: String,
    val contactNumber: String,
    val avatarColorHex: Long,
    val avatarInitials: String,
    val type: CallType,
    val direction: CallDirection,
    val timestamp: Long,
    val formattedTime: String,
    val durationText: String = "0:45",
    val isEncrypted: Boolean = true
)

data class UserProfile(
    val phoneNumber: String = "",
    val countryCode: String = "+91",
    val countryName: String = "India",
    val googleEmail: String? = null,
    val displayName: String = "",
    val aboutStatus: String = "Hey there! I am using WhatsApp.",
    val avatarColorHex: Long = 0xFF008069,
    val avatarInitials: String = "",
    val isVerified: Boolean = false,
    val securityFingerprint: String = "84920 19384 72910 48291 93820 48201 84920 19384 72910 48291 93820 48201"
)
