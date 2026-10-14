package com.example.data

import com.example.model.*

object MockDataProvider {

    fun getInitialChats(): List<ChatConversation> = listOf(
        ChatConversation(
            id = "chat_1",
            name = "Priya Sharma",
            phoneNumber = "+91 98123 45678",
            avatarColorHex = 0xFF008069,
            avatarInitials = "PS",
            lastMessageText = "Are we still meeting for the project discussion today?",
            lastMessageTime = "10:45 AM",
            unreadCount = 2,
            isOnline = true,
            statusSubtitle = "online",
            isFavorite = true
        ),
        ChatConversation(
            id = "chat_group_1",
            name = "Android Developers 🤖",
            phoneNumber = "",
            isGroup = true,
            avatarColorHex = 0xFF25D366,
            avatarInitials = "AD",
            lastMessageText = "Rahul: Jetpack Compose 1.8 has some amazing updates!",
            lastMessageTime = "10:30 AM",
            unreadCount = 5,
            isOnline = false,
            statusSubtitle = "Priya, Rahul, Aman, Neha, You",
            members = listOf("Priya Sharma", "Rahul Verma", "Aman Gupta", "Neha Singh", "You")
        ),
        ChatConversation(
            id = "chat_2",
            name = "Mom ❤️",
            phoneNumber = "+91 98450 11223",
            avatarColorHex = 0xFFE91E63,
            avatarInitials = "MO",
            lastMessageText = "Remember to take your lunch on time beta!",
            lastMessageTime = "Yesterday",
            unreadCount = 0,
            isPinned = true,
            isFavorite = true
        ),
        ChatConversation(
            id = "chat_3",
            name = "Rahul Verma",
            phoneNumber = "+91 97234 56789",
            avatarColorHex = 0xFF2196F3,
            avatarInitials = "RV",
            lastMessageText = "Shared the design system files with you.",
            lastMessageTime = "Yesterday",
            unreadCount = 0,
            isOnline = false,
            statusSubtitle = "last seen yesterday at 11:20 PM"
        ),
        ChatConversation(
            id = "chat_broadcast_1",
            name = "Diwali Wishes 🪔 (Broadcast)",
            phoneNumber = "",
            isBroadcast = true,
            avatarColorHex = 0xFFFF9800,
            avatarInitials = "BC",
            lastMessageText = "Wishing everyone a very Happy and prosperous Diwali! ✨",
            lastMessageTime = "Monday",
            unreadCount = 0,
            members = listOf("Mom ❤️", "Priya Sharma", "Rahul Verma", "Uncle Raj", "Aman Gupta")
        ),
        ChatConversation(
            id = "chat_4",
            name = "Neha Singh",
            phoneNumber = "+91 96543 21098",
            avatarColorHex = 0xFF9C27B0,
            avatarInitials = "NS",
            lastMessageText = "Voice message (0:24)",
            lastMessageTime = "Sunday",
            unreadCount = 0,
            statusSubtitle = "last seen Sunday at 4:15 PM"
        ),
        ChatConversation(
            id = "chat_group_2",
            name = "Family 👨‍👩‍👧‍👦",
            phoneNumber = "",
            isGroup = true,
            avatarColorHex = 0xFF4CAF50,
            avatarInitials = "FM",
            lastMessageText = "Dad: Dinner plan for Sunday is confirmed.",
            lastMessageTime = "Sunday",
            unreadCount = 0,
            members = listOf("Mom ❤️", "Dad", "Rohan", "Pooja", "You")
        )
    )

    fun getInitialMessages(chatId: String): List<Message> {
        val now = System.currentTimeMillis()
        return when (chatId) {
            "chat_1" -> listOf(
                Message(
                    id = "m1_1",
                    chatId = chatId,
                    senderId = "priya",
                    senderName = "Priya Sharma",
                    text = "Hey! Did you check out the new WhatsApp clone features?",
                    timestamp = now - 3600000,
                    formattedTime = "9:45 AM",
                    isOutgoing = false,
                    status = MessageStatus.READ
                ),
                Message(
                    id = "m1_2",
                    chatId = chatId,
                    senderId = "me",
                    senderName = "You",
                    text = "Yes! It has complete end-to-end encryption, 24h status, voice notes, calls, and groups!",
                    timestamp = now - 3500000,
                    formattedTime = "9:46 AM",
                    isOutgoing = true,
                    status = MessageStatus.READ,
                    reactions = mapOf("❤️" to 1)
                ),
                Message(
                    id = "m1_3",
                    chatId = chatId,
                    senderId = "priya",
                    senderName = "Priya Sharma",
                    text = "That's fantastic. Can we test the audio call and status update features?",
                    timestamp = now - 1800000,
                    formattedTime = "10:15 AM",
                    isOutgoing = false,
                    status = MessageStatus.READ
                ),
                Message(
                    id = "m1_4",
                    chatId = chatId,
                    senderId = "priya",
                    senderName = "Priya Sharma",
                    text = "Are we still meeting for the project discussion today?",
                    timestamp = now - 600000,
                    formattedTime = "10:45 AM",
                    isOutgoing = false,
                    status = MessageStatus.READ
                )
            )
            "chat_group_1" -> listOf(
                Message(
                    id = "mg_1",
                    chatId = chatId,
                    senderId = "aman",
                    senderName = "Aman Gupta",
                    text = "Hey everyone! Welcome to the Android Devs group.",
                    timestamp = now - 7200000,
                    formattedTime = "8:30 AM",
                    isOutgoing = false,
                    status = MessageStatus.READ
                ),
                Message(
                    id = "mg_2",
                    chatId = chatId,
                    senderId = "priya",
                    senderName = "Priya Sharma",
                    text = "Are all messages in this group encrypted too?",
                    timestamp = now - 5400000,
                    formattedTime = "9:00 AM",
                    isOutgoing = false,
                    status = MessageStatus.READ
                ),
                Message(
                    id = "mg_3",
                    chatId = chatId,
                    senderId = "me",
                    senderName = "You",
                    text = "Yes, WhatsApp group chats are protected with end-to-end encryption! 🔒",
                    timestamp = now - 3600000,
                    formattedTime = "9:30 AM",
                    isOutgoing = true,
                    status = MessageStatus.READ,
                    reactions = mapOf("👍" to 3, "🔥" to 2)
                ),
                Message(
                    id = "mg_4",
                    chatId = chatId,
                    senderId = "rahul",
                    senderName = "Rahul Verma",
                    text = "Jetpack Compose 1.8 has some amazing updates!",
                    timestamp = now - 900000,
                    formattedTime = "10:30 AM",
                    isOutgoing = false,
                    status = MessageStatus.READ
                )
            )
            "chat_2" -> listOf(
                Message(
                    id = "m2_1",
                    chatId = chatId,
                    senderId = "mom",
                    senderName = "Mom ❤️",
                    text = "Did you reach office safely?",
                    timestamp = now - 86400000,
                    formattedTime = "Yesterday 9:15 AM",
                    isOutgoing = false,
                    status = MessageStatus.READ
                ),
                Message(
                    id = "m2_2",
                    chatId = chatId,
                    senderId = "me",
                    senderName = "You",
                    text = "Yes Mom, reached 10 mins ago. Everything is fine!",
                    timestamp = now - 86300000,
                    formattedTime = "Yesterday 9:20 AM",
                    isOutgoing = true,
                    status = MessageStatus.READ,
                    reactions = mapOf("❤️" to 1)
                ),
                Message(
                    id = "m2_3",
                    chatId = chatId,
                    senderId = "mom",
                    senderName = "Mom ❤️",
                    text = "Remember to take your lunch on time beta!",
                    timestamp = now - 86000000,
                    formattedTime = "Yesterday 1:30 PM",
                    isOutgoing = false,
                    status = MessageStatus.READ
                )
            )
            else -> listOf(
                Message(
                    id = "def_1",
                    chatId = chatId,
                    senderId = "me",
                    senderName = "You",
                    text = "Hello! Connected securely via WhatsApp End-to-End Encryption 🔒",
                    timestamp = now - 1800000,
                    formattedTime = "10:00 AM",
                    isOutgoing = true,
                    status = MessageStatus.READ
                )
            )
        }
    }

    fun getInitialStatusStories(): List<UserStatusStory> {
        val now = System.currentTimeMillis()
        val twentyFourHours = 24 * 60 * 60 * 1000L

        return listOf(
            UserStatusStory(
                id = "status_priya",
                userId = "priya",
                userName = "Priya Sharma",
                avatarColorHex = 0xFF008069,
                avatarInitials = "PS",
                hasUnseen = true,
                statuses = listOf(
                    StatusItem(
                        id = "st_p1",
                        type = StatusContentType.TEXT,
                        textContent = "Coffee & Code ☕💻 Perfect morning vibes!",
                        backgroundColorHex = 0xFF795548,
                        timestamp = now - 3600000,
                        formattedTime = "1 hour ago",
                        expiresAtMillis = now - 3600000 + twentyFourHours,
                        viewsCount = 14
                    ),
                    StatusItem(
                        id = "st_p2",
                        type = StatusContentType.PHOTO,
                        textContent = "Sunset at Marine Drive 🌅",
                        caption = "Beautiful evening in Mumbai!",
                        backgroundColorHex = 0xFFE65100,
                        timestamp = now - 1800000,
                        formattedTime = "30 minutes ago",
                        expiresAtMillis = now - 1800000 + twentyFourHours,
                        viewsCount = 9
                    )
                )
            ),
            UserStatusStory(
                id = "status_rahul",
                userId = "rahul",
                userName = "Rahul Verma",
                avatarColorHex = 0xFF2196F3,
                avatarInitials = "RV",
                hasUnseen = true,
                statuses = listOf(
                    StatusItem(
                        id = "st_r1",
                        type = StatusContentType.TEXT,
                        textContent = "Weekend road trip to Goa! 🚗💨 Who is joining?",
                        backgroundColorHex = 0xFF00897B,
                        timestamp = now - 7200000,
                        formattedTime = "2 hours ago",
                        expiresAtMillis = now - 7200000 + twentyFourHours,
                        viewsCount = 22
                    )
                )
            ),
            UserStatusStory(
                id = "status_neha",
                userId = "neha",
                userName = "Neha Singh",
                avatarColorHex = 0xFF9C27B0,
                avatarInitials = "NS",
                hasUnseen = false,
                statuses = listOf(
                    StatusItem(
                        id = "st_n1",
                        type = StatusContentType.TEXT,
                        textContent = "Just finished reading 'Atomic Habits' 📖 Highly recommended!",
                        backgroundColorHex = 0xFF6A1B9A,
                        timestamp = now - 18000000,
                        formattedTime = "5 hours ago",
                        expiresAtMillis = now - 18000000 + twentyFourHours,
                        viewsCount = 31
                    )
                )
            )
        )
    }

    fun getInitialCallRecords(): List<CallRecord> {
        val now = System.currentTimeMillis()
        return listOf(
            CallRecord(
                id = "call_1",
                contactName = "Priya Sharma",
                contactNumber = "+91 98123 45678",
                avatarColorHex = 0xFF008069,
                avatarInitials = "PS",
                type = CallType.VIDEO,
                direction = CallDirection.INCOMING,
                timestamp = now - 1800000,
                formattedTime = "Today, 10:15 AM",
                durationText = "12:34"
            ),
            CallRecord(
                id = "call_2",
                contactName = "Mom ❤️",
                contactNumber = "+91 98450 11223",
                avatarColorHex = 0xFFE91E63,
                avatarInitials = "MO",
                type = CallType.VOICE,
                direction = CallDirection.OUTGOING,
                timestamp = now - 36000000,
                formattedTime = "Yesterday, 8:40 PM",
                durationText = "05:12"
            ),
            CallRecord(
                id = "call_3",
                contactName = "Rahul Verma",
                contactNumber = "+91 97234 56789",
                avatarColorHex = 0xFF2196F3,
                avatarInitials = "RV",
                type = CallType.VOICE,
                direction = CallDirection.MISSED,
                timestamp = now - 86400000,
                formattedTime = "Yesterday, 3:10 PM",
                durationText = "Missed"
            ),
            CallRecord(
                id = "call_4",
                contactName = "Neha Singh",
                contactNumber = "+91 96543 21098",
                avatarColorHex = 0xFF9C27B0,
                avatarInitials = "NS",
                type = CallType.VIDEO,
                direction = CallDirection.OUTGOING,
                timestamp = now - (2 * 86400000),
                formattedTime = "October 12, 6:45 PM",
                durationText = "02:50"
            )
        )
    }

    fun getAllContacts(): List<Pair<String, String>> = listOf(
        "Priya Sharma" to "+91 98123 45678",
        "Rahul Verma" to "+91 97234 56789",
        "Mom ❤️" to "+91 98450 11223",
        "Dad" to "+91 98450 11224",
        "Neha Singh" to "+91 96543 21098",
        "Aman Gupta" to "+91 99887 76655",
        "Rohan" to "+91 91234 56789",
        "Pooja" to "+91 92345 67890",
        "Uncle Raj" to "+91 93456 78901",
        "Vikram Malhotra" to "+91 94567 89012"
    )
}
