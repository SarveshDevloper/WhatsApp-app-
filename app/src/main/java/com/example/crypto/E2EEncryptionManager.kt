package com.example.crypto

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

object E2EEncryptionManager {

    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    // Pre-computed session secret key (32 bytes for AES-256)
    private val AES_KEY_BYTES = byteArrayOf(
        0x1f, 0x4e, 0x82.toByte(), 0x3d, 0x09, 0xa1.toByte(), 0x55, 0x67,
        0x90.toByte(), 0x11, 0x34, 0xbb.toByte(), 0xcc.toByte(), 0xdd.toByte(), 0xee.toByte(), 0xff.toByte(),
        0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x80.toByte(),
        0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
    )
    private val FIXED_IV = byteArrayOf(
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
    )

    /**
     * Encrypts plaintext message using AES-256 CBC.
     * Returns encrypted ciphertext with SHA-256 verification hash.
     */
    fun encryptMessage(plainText: String): EncryptedPayload {
        return try {
            val secretKey = SecretKeySpec(AES_KEY_BYTES, "AES")
            val ivSpec = IvParameterSpec(FIXED_IV)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            val cipherBase64 = Base64.encodeToString(cipherBytes, Base64.NO_WRAP)
            val digest = computeSha256(plainText)

            EncryptedPayload(
                cipherText = cipherBase64,
                digest = digest,
                isEncrypted = true,
                keyFingerprint = digest.take(12)
            )
        } catch (e: Exception) {
            EncryptedPayload(
                cipherText = plainText,
                digest = computeSha256(plainText),
                isEncrypted = false,
                keyFingerprint = "LOCAL-ERR"
            )
        }
    }

    /**
     * Decrypts ciphertext back to plaintext using the session key.
     */
    fun decryptMessage(cipherBase64: String): String {
        return try {
            val secretKey = SecretKeySpec(AES_KEY_BYTES, "AES")
            val ivSpec = IvParameterSpec(FIXED_IV)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            val cipherBytes = Base64.decode(cipherBase64, Base64.NO_WRAP)
            val plainBytes = cipher.doFinal(cipherBytes)
            String(plainBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            // Fallback to raw text if not base64/aes
            cipherBase64
        }
    }

    /**
     * Computes SHA-256 hash of a string.
     */
    fun computeSha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Generates standard WhatsApp 60-digit safety number format:
     * 12 groups of 5 digits separated by spaces.
     */
    fun generateSafetyNumber(myNumber: String, contactNumber: String): List<String> {
        val combined = "$myNumber-$contactNumber-WHATSAPP-E2EE-SALT-2026"
        val hash = computeSha256(combined)
        
        // Derive 60 digits from hash deterministically
        val digits = StringBuilder()
        for (ch in hash) {
            digits.append(ch.code % 10)
            if (digits.length >= 60) break
        }
        while (digits.length < 60) {
            digits.append(((digits.length * 7) % 10))
        }

        // Chunk into 12 groups of 5 digits
        return (0 until 12).map { index ->
            digits.substring(index * 5, (index + 1) * 5)
        }
    }
}

data class EncryptedPayload(
    val cipherText: String,
    val digest: String,
    val isEncrypted: Boolean,
    val keyFingerprint: String
)
