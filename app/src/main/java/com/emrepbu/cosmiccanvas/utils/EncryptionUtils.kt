package com.emrepbu.cosmiccanvas.utils

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Utility class to handle secure encryption of sensitive data such as API keys
 */
object EncryptionUtils {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_ALIAS = "CosmicCanvasApiKeyEncryption"
    private const val IV_SEPARATOR = ":"

    /**
     * Encrypts the given text and returns a Base64 encoded string
     * containing both the IV and encrypted data
     */
    fun encrypt(context: Context, text: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())

        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(text.toByteArray(StandardCharsets.UTF_8))

        // Combine IV and encrypted data with a separator
        val combined = Base64.encodeToString(iv, Base64.NO_WRAP) +
                IV_SEPARATOR + Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

        return combined
    }

    /**
     * Decrypts the given Base64 encoded string that contains both IV and encrypted data
     */
    fun decrypt(context: Context, encryptedText: String): String {
        val parts = encryptedText.split(IV_SEPARATOR)
        if (parts.size != 2) throw IllegalArgumentException("Invalid encrypted text format")

        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val encryptedBytes = Base64.decode(parts[1], Base64.NO_WRAP)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, StandardCharsets.UTF_8)
    }

    /**
     * Gets or creates the encryption key from the Android KeyStore
     */
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )

            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            return keyGenerator.generateKey()
        }

        return getSecretKey()
    }

    /**
     * Gets the existing key from the Android KeyStore
     */
    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }
}