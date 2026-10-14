package com.example.data

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.model.ChatConversation
import com.example.model.Message
import com.example.model.MessageStatus
import com.example.model.MessageType
import com.example.model.UserProfile
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirebaseWhatsAppRepository(private val context: Context) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.getString(R.string.firestore_database_id)
    )
    val auth: FirebaseAuth = Firebase.auth
    private val prefs = context.getSharedPreferences("whatsapp_prefs", Context.MODE_PRIVATE)

    fun currentUserId(): String? = auth.currentUser?.uid ?: prefs.getString("saved_uid", null)

    fun currentUserEmail(): String? = auth.currentUser?.email

    fun currentUserDisplayName(): String? = auth.currentUser?.displayName

    fun isUserAuthenticated(): Boolean = auth.currentUser != null

    fun savePersistentUid(uid: String) {
        prefs.edit().putString("saved_uid", uid).apply()
    }

    fun clearSavedSession() {
        prefs.edit().clear().apply()
        try {
            auth.signOut()
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Error signing out", e)
        }
    }

    suspend fun signInWithGoogle(
        activity: Activity,
        credentialManager: CredentialManager,
        onSuccess: (String, String?, String?) -> Unit,
        onError: (String) -> Unit
    ) {
        val serverClientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            onError("Google Sign-In configuration error: Client ID missing.")
            return
        }

        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = serverClientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

        try {
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    savePersistentUid(user.uid)
                    onSuccess(user.uid, user.email, user.displayName)
                } else {
                    onError("Failed to obtain authenticated Firebase user from Google credential.")
                }
            } else {
                onError("Unexpected credential format received from Google Play Services.")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("FirebaseAuth", "Google Sign-In was cancelled: ${e.message}")
            onError("Sign-in cancelled by user")
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Google Sign-In failed", e)
            val message = e.localizedMessage ?: "Google Authentication failed"
            onError(message)
        }
    }

    suspend fun signUpWithEmailPassword(
        email: String,
        pass: String,
        onSuccess: (String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), pass).await()
            val user = result.user
            if (user != null) {
                savePersistentUid(user.uid)
                onSuccess(user.uid, user.email ?: email)
            } else {
                onError("Failed to create Firebase user account")
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Sign up failed", e)
            val msg = e.localizedMessage ?: "Firebase Sign Up failed"
            onError(msg)
        }
    }

    suspend fun signInWithEmailPassword(
        email: String,
        pass: String,
        onSuccess: (String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val result = auth.signInWithEmailAndPassword(email.trim(), pass).await()
            val user = result.user
            if (user != null) {
                savePersistentUid(user.uid)
                onSuccess(user.uid, user.email ?: email)
            } else {
                onError("Failed to sign in to Firebase account")
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Sign in failed", e)
            val msg = e.localizedMessage ?: "Firebase Sign In failed"
            onError(msg)
        }
    }

    suspend fun saveUserProfile(profile: UserProfile, uid: String): Boolean {
        savePersistentUid(uid)
        val userRef = db.collection("users").document(uid)
        val data = hashMapOf(
            "userId" to uid,
            "phoneNumber" to profile.phoneNumber,
            "countryCode" to profile.countryCode,
            "countryName" to profile.countryName,
            "displayName" to profile.displayName,
            "aboutStatus" to profile.aboutStatus,
            "googleEmail" to (profile.googleEmail ?: ""),
            "avatarColorHex" to profile.avatarColorHex,
            "avatarInitials" to profile.avatarInitials,
            "isVerified" to true,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            userRef.set(data, SetOptions.merge()).await()
            Log.d("FirebaseSync", "User profile synced to Firestore for $uid")
            true
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Failed to sync profile to Firestore", e)
            false
        }
    }

    suspend fun fetchUserProfile(uid: String): UserProfile? {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            if (doc.exists()) {
                UserProfile(
                    phoneNumber = doc.getString("phoneNumber") ?: "",
                    countryCode = doc.getString("countryCode") ?: "+91",
                    countryName = doc.getString("countryName") ?: "India",
                    displayName = doc.getString("displayName") ?: "",
                    aboutStatus = doc.getString("aboutStatus") ?: "Hey there! I am using WhatsApp.",
                    googleEmail = doc.getString("googleEmail"),
                    avatarColorHex = doc.getLong("avatarColorHex") ?: 0xFF008069,
                    avatarInitials = doc.getString("avatarInitials") ?: "ME",
                    isVerified = doc.getBoolean("isVerified") ?: true
                )
            } else null
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error fetching user profile", e)
            null
        }
    }

    suspend fun syncChat(chat: ChatConversation, uid: String) {
        val chatRef = db.collection("users").document(uid).collection("chats").document(chat.id)
        val data = hashMapOf(
            "id" to chat.id,
            "name" to chat.name,
            "phoneNumber" to chat.phoneNumber,
            "lastMessageText" to chat.lastMessageText,
            "lastMessageTime" to chat.lastMessageTime,
            "unreadCount" to chat.unreadCount,
            "isGroup" to chat.isGroup,
            "isBroadcast" to chat.isBroadcast,
            "avatarColorHex" to chat.avatarColorHex,
            "avatarInitials" to chat.avatarInitials,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            chatRef.set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Failed to sync chat to Firestore", e)
        }
    }

    suspend fun syncMessage(message: Message, uid: String) {
        val msgRef = db.collection("users").document(uid)
            .collection("chats").document(message.chatId)
            .collection("messages").document(message.id)
        val data = hashMapOf(
            "id" to message.id,
            "chatId" to message.chatId,
            "senderId" to message.senderId,
            "senderName" to message.senderName,
            "text" to message.text,
            "formattedTime" to message.formattedTime,
            "timestamp" to message.timestamp,
            "isOutgoing" to message.isOutgoing,
            "isEncrypted" to message.isEncrypted,
            "status" to message.status.name,
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            msgRef.set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Failed to sync message to Firestore", e)
        }
    }

    fun listenToChats(uid: String, onChats: (List<ChatConversation>) -> Unit): ListenerRegistration {
        return db.collection("users").document(uid).collection("chats")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }
                val chats = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getString("id") ?: doc.id
                    val name = doc.getString("name") ?: "Contact"
                    val phone = doc.getString("phoneNumber") ?: ""
                    val lastMsg = doc.getString("lastMessageText") ?: ""
                    val lastTime = doc.getString("lastMessageTime") ?: ""
                    val unread = doc.getLong("unreadCount")?.toInt() ?: 0
                    val isGroup = doc.getBoolean("isGroup") ?: false
                    val isBroadcast = doc.getBoolean("isBroadcast") ?: false
                    val avatarColor = doc.getLong("avatarColorHex") ?: 0xFF008069
                    val initials = doc.getString("avatarInitials") ?: name.take(2).uppercase()

                    ChatConversation(
                        id = id,
                        name = name,
                        phoneNumber = phone,
                        lastMessageText = lastMsg,
                        lastMessageTime = lastTime,
                        unreadCount = unread,
                        isGroup = isGroup,
                        isBroadcast = isBroadcast,
                        avatarColorHex = avatarColor,
                        avatarInitials = initials
                    )
                }
                onChats(chats)
            }
    }

    fun listenToMessages(uid: String, chatId: String, onMessages: (List<Message>) -> Unit): ListenerRegistration {
        return db.collection("users").document(uid)
            .collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }
                val messages = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getString("id") ?: doc.id
                    val text = doc.getString("text") ?: ""
                    val senderId = doc.getString("senderId") ?: ""
                    val senderName = doc.getString("senderName") ?: ""
                    val formattedTime = doc.getString("formattedTime") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                    val isOutgoing = doc.getBoolean("isOutgoing") ?: true
                    val isEncrypted = doc.getBoolean("isEncrypted") ?: true
                    val statusStr = doc.getString("status") ?: "SENT"
                    val status = try { MessageStatus.valueOf(statusStr) } catch (_: Exception) { MessageStatus.SENT }

                    Message(
                        id = id,
                        chatId = chatId,
                        senderId = senderId,
                        senderName = senderName,
                        text = text,
                        timestamp = timestamp,
                        formattedTime = formattedTime,
                        status = status,
                        isOutgoing = isOutgoing,
                        type = MessageType.TEXT,
                        isEncrypted = isEncrypted
                    )
                }
                onMessages(messages)
            }
    }
}
